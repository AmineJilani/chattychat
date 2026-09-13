package chattychat.sr03_chat_app.websocket;

/**
 * représente un message échangé dans un salon de discussion.
 * NB :les messages ne sont pas sauvegardés en base de données.
 * Cette classe sert uniquement à transporter les données entre React et le serveur WebSocket.
 */
public class ChatMessage {

    // Identifiant du chat concerné
    private Long chatId;

    // Nom de l'utilisateur qui envoie le message
    private String sender;

    // Contenu du message
    private String content;

    // Type du message : CHAT, JOIN, LEAVE
    private String type;

    public ChatMessage() {} // constructueur

    public ChatMessage(Long chatId, String sender, String content, String type) {
        this.chatId = chatId;
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    //GETTERS
    public Long getChatId() {return chatId;}
    public String getSender() {return sender;}
    public String getContent() {return content;}
    public String getType() { return type;}
    
//SETTERS
    public void setChatId(Long chatId) {this.chatId = chatId;}
    public void setSender(String sender) {this.sender = sender;}
    public void setContent(String content) {this.content = content;}
    public void setType(String type) {this.type = type;}
}