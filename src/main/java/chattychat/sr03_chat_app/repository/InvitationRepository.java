package chattychat.sr03_chat_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import chattychat.sr03_chat_app.model.Chat;
import chattychat.sr03_chat_app.model.Invitation;
import chattychat.sr03_chat_app.model.User;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Toutes les invitations recues par un utilisateur
    List<Invitation> findByUser(User user);

    // Toutes les invitations d'un chat
    List<Invitation> findByChat(Chat chat);

    // Vérifier si un utilisateur est déjà invité dans un chat --> éviter les doublons
    boolean existsByUserAndChat(User user, Chat chat);
}