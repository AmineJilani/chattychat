package chattychat.sr03_chat_app.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration du serveur WebSocket.
 * elle définit le point d'entrée WebSocket (/ws) et les canaux de diffusion.
 * permet la communication temp reel entre React et Spring Boot
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Point d'entrée utilisé par React pour se connecter au serveur WebSocket.
     * URL côté React : http://localhost:8088/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .withSockJS();
    }

    /**
     * Configuration du broker de messages.
     * /topic : canal utilisé pour recevoir les messages.
     * /app : préfixe utilisé pour envoyer un message au serveur.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}