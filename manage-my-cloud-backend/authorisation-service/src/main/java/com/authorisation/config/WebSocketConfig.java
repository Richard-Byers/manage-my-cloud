package com.authorisation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import static com.authorisation.config.WebConfig.ENVIRONMENT;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        String allowedOrigin;

        if (ENVIRONMENT != null && ENVIRONMENT.equals("production")) {
            allowedOrigin = System.getenv("FRONTEND_URL");
        } else {
            allowedOrigin = "http://localhost:3000";
        }

        registry.addEndpoint("/progress").setAllowedOrigins(allowedOrigin).withSockJS();
        registry.addEndpoint("/recommendation-progress").setAllowedOrigins(allowedOrigin).withSockJS();
        registry.addEndpoint("/deletion-progress").setAllowedOrigins(allowedOrigin).withSockJS();
    }
}
