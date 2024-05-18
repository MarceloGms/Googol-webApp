package com.googol.googolfe.web.websockets;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * The WebSocketConfig class configures WebSocket endpoints and message brokers for the application.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

   /**
    * Registers STOMP endpoints.
    * @param registry the STOMP endpoint registry
    */
   @Override
   public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint("/ws").withSockJS();
   }

   /**
    * Configures message brokers.
    * @param registry the message broker registry
    */
   @Override
   public void configureMessageBroker(MessageBrokerRegistry registry) {
      registry.setApplicationDestinationPrefixes("/app");
      registry.enableSimpleBroker("/topic");
   }
}
