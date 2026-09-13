package chattychat.sr03_chat_app.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import chattychat.sr03_chat_app.model.Chat;
import chattychat.sr03_chat_app.model.User;
import chattychat.sr03_chat_app.model.Invitation;
import chattychat.sr03_chat_app.model.StatutInvitation;
import chattychat.sr03_chat_app.repository.ChatRepository;
import chattychat.sr03_chat_app.repository.UserRepository;
import chattychat.sr03_chat_app.repository.InvitationRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/chats")
public class ChatController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    public ChatController(ChatRepository chatRepository, UserRepository userRepository, InvitationRepository invitationRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.invitationRepository=invitationRepository;
    }

    private boolean isNotConnected(HttpSession session) {
        return session.getAttribute("admin") == null;
    }

    @GetMapping
    /**
     * Affiche la liste de tous les chats.
     */
    public String listChats(Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("chats", chatRepository.findAll());
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/chats";
    }

    @GetMapping("/new")
    public String newChat(Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("chat", new Chat());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/chats-form";
    }

    @PostMapping("/save")
    public String saveChat(@ModelAttribute Chat chat,
                           @RequestParam Long proprietaireId,
                           HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User proprietaire = userRepository.findById(proprietaireId).orElseThrow();
        chat.setProprietaire(proprietaire);

        chatRepository.save(chat);

        return "redirect:/admin/chats";
    }

    @GetMapping("/edit/{id}")
    public String editChat(@PathVariable Long id, Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        Chat chat = chatRepository.findById(id).orElseThrow();

        model.addAttribute("chat", chat);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/chats-form";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable Long id, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        chatRepository.deleteById(id);

        return "redirect:/admin/chats";
    }
    
    //// Inviter un user + Filtre : exclut les utilisateurs inactifs ET le propriétaire
    @GetMapping("/{id}/invitations")
    public String invitations(
            @PathVariable Long id,
            Model model,
            HttpSession session) {
     
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }
     
        Chat chat = chatRepository.findById(id).orElseThrow();
     
        model.addAttribute("chat", chat);
        model.addAttribute("invitations", invitationRepository.findByChat(chat));
     
        // Seuls les utilisateurs actifs ET différents du propriétaire
        // peuvent être proposés comme invités
        List<User> invitables = userRepository.findByActifTrue()
                .stream()
                .filter(u -> !u.getId().equals(chat.getProprietaire().getId()))
                .collect(java.util.stream.Collectors.toList());
     
        model.addAttribute("users", invitables);
        model.addAttribute("adminUser", session.getAttribute("admin"));
     
        return "admin/chat-invitations";
    }
  
    /**
     * Ajoute un ou plusieurs utilisateurs comme invités à un chat.
     * La méthode évite les doublons grâce à existsByUserAndChat.
     */
    @PostMapping("/{id}/invite")
    public String invite(@PathVariable Long id,
                         @RequestParam List<Long> userIds,
                         HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }
        //récupération de chat
        Chat chat = chatRepository.findById(id).orElseThrow();

        for (Long userId : userIds) {
            // Récupération de l'utilisateur
            User user = userRepository.findById(userId).orElseThrow();
            // Vérification de l'existence d'une invitation
            if (!invitationRepository.existsByUserAndChat(user, chat)) {

                Invitation invitation = new Invitation(
                        user,
                        chat,
                        StatutInvitation.EN_ATTENTE
                );

                invitationRepository.save(invitation);
            }
        }

        return "redirect:/admin/chats/" + id + "/invitations";
    }
    
    @GetMapping("/invitation/delete/{id}")
    /*Supprimer une invitation associé à un chat*/
    public String deleteInvitation(@PathVariable Long id) {
    	//récupération de l'invitation depuis la base
        Invitation invitation =invitationRepository.findById(id).orElseThrow();
        //récupération de chat associé
        Long chatId =invitation.getChat().getId();

        //Suppression de l'invitation
        invitationRepository.delete(invitation);
        //redirection vers la page des invitations du chat
        return "redirect:/admin/chats/"
                + chatId
                + "/invitations";
    }
   
}

