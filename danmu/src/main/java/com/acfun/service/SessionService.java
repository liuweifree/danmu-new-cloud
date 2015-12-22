package com.acfun.service;

import com.acfun.model.WsResultModel;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jack on 15/6/26.
 */
@Service
@Slf4j
public class SessionService {

  private List<WebSocketSession> playerSessionList = Collections.synchronizedList(new ArrayList<>());

  private Set<WebSocketSession> adminSessionSet = Collections.synchronizedSet(new HashSet<>());

  public boolean addPlayer(WebSocketSession session) {
    return playerSessionList.add(session);
  }

  public boolean removePlayer(WebSocketSession session) {
    return playerSessionList.remove(session);
  }

  private Integer indexToNum(String index) {
    try {
      return Integer.parseInt(index);
    } catch (NumberFormatException e) {
      return Integer.MAX_VALUE;
    }
  }

  public void sortPlayerList() {
    playerSessionList.sort((p1, p2) -> {
      String s1 = p1.getAttributes().get("index") + "";
      String s2 = p2.getAttributes().get("index") + "";
      return indexToNum(s1).compareTo(indexToNum(s2));
    });
  }

  public List<String> getPlayerIndexList() {
    return playerSessionList.stream().map((p) -> p.getAttributes().get("index") + "").collect(Collectors.toList());
  }

  public boolean sendFirstPlayer(String type, Object data) {
    if (CollectionUtils.isEmpty(playerSessionList)) {
      return false;
    }
    WebSocketSession session = playerSessionList.get(0);
    if (session != null && session.getAttributes().get("index") != null) {
      return sendMsg(session, type, data);
    }
    return false;
  }


  public boolean sendNextPlayer(WebSocketSession session, String type, Object data) {
    if (session == null || CollectionUtils.isEmpty(playerSessionList)) {
      return false;
    }
    int index = playerSessionList.indexOf(session);
    if (index < playerSessionList.size() - 1) {
      WebSocketSession nextSession = playerSessionList.get(index + 1);
      if (nextSession.getAttributes().get("index") != null) {
        return sendMsg(nextSession, type, data);
      }
    }
    return false;
  }

  public void sendPlayerMsg(String type, Object data) {
    sendMsg(playerSessionList, type, data);
  }

  public boolean addAdmin(WebSocketSession session) {
    return adminSessionSet.add(session);
  }

  public boolean removeAdmin(WebSocketSession session) {
    return adminSessionSet.remove(session);
  }

  public void sendAdminMsg(String type, Object data) {
    sendMsg(adminSessionSet, type, data);
  }

  public boolean sendMsg(WebSocketSession session, String type, Object data) {
    WsResultModel wsResultModel = new WsResultModel(type, data);
    String json = JSON.toJSONString(wsResultModel);
    TextMessage textMessage = new TextMessage(json);
    return sendMsg(session, textMessage);
  }

  public boolean sendMsg(WebSocketSession session, TextMessage textMessage) {
    if (session != null && session.isOpen()) {
      try {
        synchronized (session) {
          session.sendMessage(textMessage);
        }
        log.debug("{} sendMsg:{}", session, textMessage.getPayload());
        return true;
      } catch (IOException e) {
        log.error("ws sendMsg error", e);
      }
    }
    return false;
  }

  private void sendMsg(Collection<WebSocketSession> sessionSet, String type, Object data) {
    if (!CollectionUtils.isEmpty(sessionSet)) {
      WsResultModel wsResultModel = new WsResultModel(type);
      wsResultModel.setData(data);
      String json = JSON.toJSONString(wsResultModel);
      TextMessage textMessage = new TextMessage(json);
      Iterator<WebSocketSession> iterator = sessionSet.iterator();
      while (iterator.hasNext()) {
        sendMsg(iterator.next(), textMessage);
      }
    }
  }

}
