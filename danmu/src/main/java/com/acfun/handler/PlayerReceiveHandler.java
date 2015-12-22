package com.acfun.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 15/6/26.
 */
@Component
@Slf4j
public class PlayerReceiveHandler extends BaseTextWebSocketHandler {

  @Override
  protected void handler(WebSocketSession session, String type, JSONObject json) throws IOException {
    if ("init".equals(type)) {
      String index = json.getString("index");
      if (!StringUtils.isEmpty(index)) {
        Map<String, Object> result = new HashMap<String, Object>() {{
          put("video_name", videoService.getVideoName());
          put("player_status", videoService.getPlayerStatus());
        }};
        sendMsg(session, TYPE_INIT, result);
        //排序播放器列表
        session.getAttributes().put("index", index);
        sessionService.sortPlayerList();

        sessionService.sendAdminMsg("playerIndexList", sessionService.getPlayerIndexList());
      } else {
        sendErr(session, "index is empty");
      }
    } else if ("sendDmToNext".equals(type)) {
      sessionService.sendNextPlayer(session, type, json.get("data"));
    } else if ("sendExToNext".equals(type)) {
      sessionService.sendNextPlayer(session, type, json.get("data"));
    }
  }

  @Override
  public synchronized void afterConnectionEstablished(WebSocketSession session) throws Exception {
    log.info("open {}", session);
    sessionService.addPlayer(session);
    sessionService.sendAdminMsg("playerIndexList", sessionService.getPlayerIndexList());
  }

  @Override
  public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    log.info("close {}", session);
    sessionService.removePlayer(session);
    sessionService.sendAdminMsg("playerIndexList", sessionService.getPlayerIndexList());
  }

  @Override
  protected void handlerBrokenPipe(WebSocketSession session) throws Exception {
    log.info("close {}", session);
    sessionService.removePlayer(session);
    sessionService.sendAdminMsg("playerIndexList", sessionService.getPlayerIndexList());
  }

}
