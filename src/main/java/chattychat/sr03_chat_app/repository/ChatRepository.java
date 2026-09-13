package chattychat.sr03_chat_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import chattychat.sr03_chat_app.model.Chat;
import chattychat.sr03_chat_app.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Tous les chats créés par un utilisateur
    List<Chat> findByProprietaire(User proprietaire);

}