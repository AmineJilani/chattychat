package chattychat.sr03_chat_app.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Contrôleur WebSocket qui reçoit les messages envoyés par React 
 * et les diffuse aux utilisateurs connectés au même salon.
 * On diffuse un message en temps réel.
 */

@Controller
//diffuse un message dans le salon concerné
public class ChatWebSocketController {
	// Garde en mémoire, pour chaque chatId, la liste des utilisateurs connectés
    private final Map<Long, List<String>> connectedUsersByChat = new ConcurrentHashMap<>();

	//ce champ permet de choisir dynamiquement la destination du message
    private final SimpMessagingTemplate messagingTemplate;


    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    /**
     * Reçoit un message envoyé par un client React.
     * Côté React, le message sera envoyé vers :/app/chat.sendMessage
     *
     * Ensuite, il sera diffusé à tous les clients abonnés à : /topic/chat/{chatId}
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message) {

        Long chatId = message.getChatId();

        connectedUsersByChat.putIfAbsent(chatId, new CopyOnWriteArrayList<>());
        List<String> users = connectedUsersByChat.get(chatId);

        if ("JOIN".equals(message.getType())) {
            if (!users.contains(message.getSender())) {
                users.add(message.getSender());
            }
        } else if ("LEAVE".equals(message.getType())) {
            users.remove(message.getSender());
        }

        // Diffuse le message classique (JOIN/LEAVE/CHAT) à tous les abonnés
        messagingTemplate.convertAndSend("/topic/chat/" + chatId, message);

        // Diffuse en plus la liste complète et à jour des connectés
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/users", users);
    }
}