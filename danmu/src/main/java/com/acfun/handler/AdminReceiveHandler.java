package com.acfun.handler;

import com.acfun.model.DanmuModel;
import com.acfun.repository.DanmuRepository;
import com.acfun.service.DanmuService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

/**
 * Created by jack on 15/6/26.
 */
@Component
@Slf4j
public class AdminReceiveHandler extends BaseTextWebSocketHandler {

  @Autowired
  private DanmuService danmuService;
  @Autowired
  private DanmuRepository danmuRepository;

  private final List<DanmuModel> testList = Collections.synchronizedList(new ArrayList<>());
  private int testLitIndex = 0;

  @Scheduled(initialDelay = 0, fixedDelay = 500)
  private void startTestThread() {
    if (danmuService.isTestIsOpen() && !CollectionUtils.isEmpty(testList)) {
      DanmuModel danmuModel = testList.get(testLitIndex++ % testList.size());
      danmuService.add(danmuModel.getMsg(), "#ffffff", false);
    }
  }

  @Override
  protected void handler(WebSocketSession session, String type, JSONObject json) throws IOException {
    //发送弹幕
    if ("sendDm".equals(type)) {
      String msg = json.getString("msg");
      String color = json.getString("color");
      if (StringUtils.isEmpty(msg) || msg.length() > DanmuService.MAX_CHAR_LEN || StringUtils.isEmpty(color)) {
        sendMsg(session, type, false);
      } else {
        sendMsg(session, type, danmuService.add(msg, color));
      }
      //发送表情
    } else if ("sendEx".equals(type)) {
      String expression = json.getString("expression");
      if (StringUtils.isEmpty(expression)) {
        sendMsg(session, type, false);
      } else {
        sendMsg(session, type, danmuService.sendExpression(expression));
      }
      //弹幕延时开关
    } else if ("isDelayEnable".equals(type)) {
      boolean status = json.getBoolean("status");
      videoService.setIsDelayEnable(status);
      sessionService.sendAdminMsg(type, videoService.isDelayEnable());
      //弹幕屏蔽
    } else if ("danmuBlock".equals(type)) {
      String id = json.getString("id");
      boolean isOk = danmuService.addBlockDanmuId(id);
      sessionService.sendAdminMsg(type, isOk ? id : isOk);
      //弹幕解蔽
    } else if ("danmuUnBlock".equals(type)) {
      String id = json.getString("id");
      boolean isOk = danmuService.removeBlockDanmuId(id);
      sessionService.sendAdminMsg(type, isOk ? id : isOk);
      //屏蔽词开关
    } else if ("isBlockEnable".equals(type)) {
      boolean status = json.getBoolean("status");
      videoService.setIsBlockEnable(status);
      sessionService.sendAdminMsg(type, videoService.isBlockEnable());
      //弹幕延时调整
    } else if ("delaySecond".equals(type)) {
      boolean add = json.getBoolean("add");
      if (add) {
        videoService.setDelaySecond(videoService.getDelaySecond() + 1);
      } else {
        videoService.setDelaySecond(videoService.getDelaySecond() - 1);
        if (videoService.getDelaySecond() < 0) {
          videoService.setDelaySecond(0);
        }
      }
      sessionService.sendAdminMsg(type, videoService.getDelaySecond());
      //播放器控制
    } else if ("playerStatus".equals(type)) {
      int status = json.getInteger("status");
      if (status == 0 || status == 1 || status == 2) {
        videoService.setPlayerStatus(status);
      }
      sessionService.sendAdminMsg(type, videoService.getPlayerStatus());
      sessionService.sendPlayerMsg(type, videoService.getPlayerStatus());
      //测试弹幕
    } else if ("test".equals(type)) {
      boolean status = json.getBoolean("status");
      testList.clear();
      if (status) {
        testList.addAll(danmuRepository.findByTest(1));
        danmuService.setTestIsOpen(true);
      } else {
        danmuService.setTestIsOpen(false);
      }
      sessionService.sendAdminMsg(type, danmuService.isTestIsOpen());
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    log.info("open {}", session);

    Map<String, Object> result = new HashMap<String, Object>() {{
      put("delaySecond", videoService.getDelaySecond());
      put("isDelayEnable", videoService.isDelayEnable());
      put("playerStatus", videoService.getPlayerStatus());
      put("isBlockEnable", videoService.isBlockEnable());
      put("colors", videoService.getColors());
      put("expressions", videoService.getExpressions());
      put("danmuList", danmuService.getDanmuList());
      put("playerIndexList", sessionService.getPlayerIndexList());
      put("test", danmuService.isTestIsOpen());
    }};

    sendMsg(session, TYPE_INIT, result);

    sessionService.addAdmin(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    log.info("close {}", session);
    sessionService.removeAdmin(session);
  }

  @Override
  protected void handlerBrokenPipe(WebSocketSession session) throws Exception {
    log.info("close {}", session);
    sessionService.removeAdmin(session);
  }

}