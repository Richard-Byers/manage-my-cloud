package com.authorisation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/progress").setAllowedOrigins(ALLOWED_ORIGIN).withSockJS();
        registry.addEndpoint("/recommendation-progress").setAllowedOrigins(ALLOWED_ORIGIN).withSockJS();
        registry.addEndpoint("/deletion-progress").setAllowedOrigins(ALLOWED_ORIGIN).withSockJS();
    }
}
