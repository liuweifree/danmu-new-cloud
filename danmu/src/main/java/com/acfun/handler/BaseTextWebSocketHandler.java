package com.acfun.handler;

import com.acfun.model.WsResultModel;
import com.acfun.service.SessionService;
import com.acfun.service.VideoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * Created by jack on 15/7/1.
 */
@Slf4j
public abstract class BaseTextWebSocketHandler extends TextWebSocketHandler {

  protected static final String TYPE_INIT = "init";
  protected static final String TYPE_ERROR = "error";

  @Autowired
  protected SessionService sessionService;
  @Autowired
  protected VideoService videoService;

  protected void sendMsg(WebSocketSession session, String type, Object msg) throws IOException {
    if (session != null && session.isOpen()) {
      sessionService.sendMsg(session, type, msg);
    }
  }

  protected void sendErr(WebSocketSession session, String errMsg) throws IOException {
    if (session != null && session.isOpen()) {
      String json = JSON.toJSONString(new WsResultModel(TYPE_ERROR, errMsg));
      sessionService.sendMsg(session, new TextMessage(json));
    }
  }

  protected abstract void handler(WebSocketSession session, String type, JSONObject json) throws IOException;

  protected abstract void handlerBrokenPipe(WebSocketSession session) throws Exception;

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    if (message != null && !StringUtils.isEmpty(message)) {
      try {
        JSONObject json = JSON.parseObject(message.getPayload());
        if (json.containsKey("type")) {
          String type = json.getString("type");
          handler(session, type, json);
          return;
        }
      } catch (JSONException e) {
        sendErr(session, "json解析出错");
      } catch (Exception e) {
        log.error("", e);
        sendErr(session, "未知错误");
      }
    } else {
      sendErr(session, "message为空");
    }
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    String cause = exception.getCause() + "";
    if (cause.contains("java.io.IOException: Broken pipe")
        || cause.contains("java.io.IOException: 断开的管道")) {
      log.warn("{} is broken pipe!", session);
      handlerBrokenPipe(session);
    } else {
      log.error("", exception);
    }
  }

}
