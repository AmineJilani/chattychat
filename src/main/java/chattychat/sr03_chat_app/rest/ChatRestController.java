package chattychat.sr03_chat_app.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import chattychat.sr03_chat_app.dto.ChatDTO;
import chattychat.sr03_chat_app.dto.ChatRequestDTO;
import chattychat.sr03_chat_app.dto.InviteRequestDTO; 

import chattychat.sr03_chat_app.model.Chat;
import chattychat.sr03_chat_app.model.User;
import chattychat.sr03_chat_app.model.Invitation;
import chattychat.sr03_chat_app.model.StatutInvitation;
import chattychat.sr03_chat_app.repository.ChatRepository;
import chattychat.sr03_chat_app.repository.UserRepository;
import chattychat.sr03_chat_app.repository.InvitationRepository;

/**
 * Web Service REST dédié aux chats.
 */
@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatRestController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    public ChatRestController(
            ChatRepository chatRepository,
            UserRepository userRepository,
            InvitationRepository invitationRepository) {

        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * Retourne la liste des chats dont l'utilisateur
     * est propriétaire.
     */
    @GetMapping("/owner/{userId}")
    public List<ChatDTO> getOwnerChats(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        List<Chat> chats = chatRepository.findByProprietaire(user);

        return chats.stream()
                .map(chat -> new ChatDTO(
                        chat.getId(),
                        chat.getTitre(),
                        chat.getDescription(),
                        chat.getDateCreation(),
                        chat.getDureeValiditeMinutes(),
                        chat.getDateExpiration(),
                        chat.isValable(),
                        user.getId(),
                        user.getNom() + " " + user.getPrenom()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste des chats auxquels
     * l'utilisateur est invité.
     */
    @GetMapping("/invited/{userId}")
    public List<ChatDTO> getInvitedChats(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

        List<Invitation> invitations = invitationRepository.findByUser(user);

        return invitations.stream()
                .map(invitation -> {

                    Chat chat = invitation.getChat();

                    return new ChatDTO(
                            chat.getId(),
                            chat.getTitre(),
                            chat.getDescription(),
                            chat.getDateCreation(),
                            chat.getDureeValiditeMinutes(),
                            chat.getDateExpiration(),
                            chat.isValable(),
                            chat.getProprietaire().getId(),
                            chat.getProprietaire().getNom()
                                    + " "
                                    + chat.getProprietaire().getPrenom()
                    );
                })
                .toList();
    }

    /**
     * Planifie un nouveau chat.
     * React envoie un JSON contenant les informations du chat.
     */
    @PostMapping
    public ResponseEntity<?> createChat(@RequestBody ChatRequestDTO dto) {

        if (dto.getDureeValiditeMinutes() == null || dto.getDureeValiditeMinutes() <= 0) {
            return ResponseEntity.badRequest().body("La durée de validité doit être positive");
        }

        User proprietaire = userRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire introuvable"));

        Chat chat = new Chat();
        chat.setTitre(dto.getTitre());
        chat.setDescription(dto.getDescription());
        chat.setDateCreation(dto.getDateCreation());
        chat.setDureeValiditeMinutes(dto.getDureeValiditeMinutes());
        chat.setProprietaire(proprietaire);

        Chat saved = chatRepository.save(chat);

        ChatDTO result = new ChatDTO(
                saved.getId(),
                saved.getTitre(),
                saved.getDescription(),
                saved.getDateCreation(),
                saved.getDureeValiditeMinutes(),
                saved.getDateExpiration(),
                saved.isValable(),
                proprietaire.getId(),
                proprietaire.getNom() + " " + proprietaire.getPrenom()
        );

        return ResponseEntity.status(201).body(result);
    }

    /**
     * Modifie un chat existant.
     * React envoie les nouvelles données du chat.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChat(@PathVariable Long id,
                                         @RequestBody ChatRequestDTO dto) {

        if (dto.getDureeValiditeMinutes() == null || dto.getDureeValiditeMinutes() <= 0) {
            return ResponseEntity.badRequest().body("La durée de validité doit être positive");
        }

        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat introuvable : " + id));

        User proprietaire = userRepository.findById(dto.getProprietaireId())
                .orElseThrow(() -> new RuntimeException("Propriétaire introuvable"));

        chat.setTitre(dto.getTitre());
        chat.setDescription(dto.getDescription());
        chat.setDateCreation(dto.getDateCreation());
        chat.setDureeValiditeMinutes(dto.getDureeValiditeMinutes());
        chat.setProprietaire(proprietaire);

        Chat saved = chatRepository.save(chat);

        ChatDTO result = new ChatDTO(
                saved.getId(),
                saved.getTitre(),
                saved.getDescription(),
                saved.getDateCreation(),
                saved.getDureeValiditeMinutes(),
                saved.getDateExpiration(),
                saved.isValable(),
                proprietaire.getId(),
                proprietaire.getNom() + " " + proprietaire.getPrenom()
        );

        return ResponseEntity.ok(result);
    }

    /**
     * Supprime un chat existant.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {

        if (!chatRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        chatRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
    /**
     * Retourne les détails d'un chat précis (utilisé par ChatRoom
     * pour afficher le titre et calculer le temps restant).
     */
    @GetMapping("/{id}")
    public ChatDTO getChatById(@PathVariable Long id) {

        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat introuvable : " + id));

        User proprietaire = chat.getProprietaire();

        return new ChatDTO(
                chat.getId(),
                chat.getTitre(),
                chat.getDescription(),
                chat.getDateCreation(),
                chat.getDureeValiditeMinutes(),
                chat.getDateExpiration(),
                chat.isValable(),
                proprietaire.getId(),
                proprietaire.getNom() + " " + proprietaire.getPrenom()
        );
    }

    /**
     * Invite une liste d'utilisateurs à un chat existant.
     * Évite les doublons grâce à existsByUserAndChat.
     */
    @PostMapping("/{id}/invite")
    public ResponseEntity<?> inviteUsers(@PathVariable Long id,
                                          @RequestBody InviteRequestDTO request) {

        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chat introuvable : " + id));

        for (Long userId : request.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + userId));

            if (!invitationRepository.existsByUserAndChat(user, chat)) {
                Invitation invitation = new Invitation(user, chat, StatutInvitation.EN_ATTENTE);
                invitationRepository.save(invitation);
            }
        }

        return ResponseEntity.ok("Invitations ajoutées");
    }
    
}