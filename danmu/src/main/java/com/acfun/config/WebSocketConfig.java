package com.acfun.config;

import com.acfun.handler.AdminReceiveHandler;
import com.acfun.handler.PlayerReceiveHandler;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;


/**
 * Created by jack on 15/5/12.
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Autowired
  private PlayerReceiveHandler playerReceiveHandler;
  @Autowired
  private AdminReceiveHandler adminReceiveHandler;

  @Bean
  public WebSocketServerFactory webSocketServerFactory() {
    WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
//    policy.setInputBufferSize(8192);
//    policy.setIdleTimeout(60 * 1000);
    return new WebSocketServerFactory(policy);
  }

  @Bean
  public DefaultHandshakeHandler handshakeHandler() {
    return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(webSocketServerFactory()));
  }

  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(playerReceiveHandler, "/ws/player")
        .addHandler(adminReceiveHandler, "/ws/admin")
        .setAllowedOrigins("*");
  }

}
