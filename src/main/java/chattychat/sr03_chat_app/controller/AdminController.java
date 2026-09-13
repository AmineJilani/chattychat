package chattychat.sr03_chat_app.controller;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import chattychat.sr03_chat_app.model.User;
import chattychat.sr03_chat_app.model.Chat;
import chattychat.sr03_chat_app.model.Invitation;
import chattychat.sr03_chat_app.repository.UserRepository;
import chattychat.sr03_chat_app.repository.ChatRepository;
import chattychat.sr03_chat_app.repository.InvitationRepository;
import chattychat.sr03_chat_app.config.SecurityConfig;

import jakarta.servlet.http.HttpSession;

import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final ChatRepository chatRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AdminController(UserRepository userRepository, ChatRepository chatRepository, 
    		InvitationRepository invitationRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.invitationRepository=invitationRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    private boolean isNotConnected(HttpSession session) {
        return session.getAttribute("admin") == null;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }
        User admin = (User) session.getAttribute("admin");

        model.addAttribute("adminUser", admin);

        return "admin/home";
    }
    
    @GetMapping("/infos")
    public String infosAdmin(Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User admin = (User) session.getAttribute("admin");

        model.addAttribute("adminUser", admin);

        return "admin/admin-infos";
    }

    @GetMapping("/users")
    public String users(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "nom") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String role,
            
            Model model,
            HttpSession session
    ) {
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, 4, Sort.by(sortDirection, sort));

        Page<User> users;

        if (keyword == null || keyword.isBlank()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository
                    .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            keyword, keyword, keyword, pageable
                    );
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseDirection", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model, HttpSession session) {
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           Model model,
                           HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Cette adresse mail existe déjà");
        }

        if (result.hasErrors()) {
            return "admin/user-form";
        }

        user.setActif(true);
        user.setBloque(false);
        user.setNbTentatives(0);
        
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        System.out.println("Mail de confirmation envoyé à : " + user.getEmail());
        System.out.println("Mot de passe temporaire : " + user.getPassword());

        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, HttpSession session) {
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);

        return "admin/user-form";
    }

    @GetMapping("/users/details/{id}")
    public String userDetails(@PathVariable Long id, Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElseThrow();

        List<Chat> chats = chatRepository.findByProprietaire(user);
        List<Invitation> invitations =invitationRepository.findByUser(user);

        model.addAttribute("invitations", invitations);
        model.addAttribute("adminUser", session.getAttribute("admin")); 
        model.addAttribute("user", user);
        model.addAttribute("chats", chats);

        return "admin/user-details";
    }

    @GetMapping("/users/activate/{id}")
    public String activateUser(@PathVariable Long id) {

        User user = userRepository.findById(id).orElseThrow();
        user.setActif(true);
        user.setBloque(false);
        user.setNbTentatives(0);

        userRepository.save(user);

        return "redirect:/admin/users";
    }
    @GetMapping("/users/edit-status/{id}")
    public String editStatus(@PathVariable Long id, Model model, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/edit-status";
    }

    @PostMapping("/users/update-status")
    public String updateStatus(@RequestParam Long id,
                               @RequestParam Boolean actif,
                               @RequestParam Boolean admin,
                               HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User user = userRepository.findById(id).orElseThrow();

        user.setActif(actif);
        user.setAdmin(admin);

        if (Boolean.TRUE.equals(actif)) {
            user.setBloque(false);
            user.setNbTentatives(0);
        }

        userRepository.save(user);

        return "redirect:/admin/users";
    }
    /**
     * Supprime un utilisateur depuis l'interface d'administration.
     *
     * @param id identifiant de l'utilisateur à supprimer
     * @return redirection vers la liste des utilisateurs
     */
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        User admin = (User) session.getAttribute("admin");

        userRepository.deleteById(id);

        // Si l'admin vient de se supprimer lui-même, on invalide sa session
        if (admin.getId().equals(id)) {
            session.invalidate();
            return "redirect:/admin/login";
        }

        return "redirect:/admin/users";
    }
    @GetMapping("/disab"
    		+ "led")
    public String disabledUsers(@RequestParam(defaultValue = "0") int page,
                                Model model,
                                HttpSession session) {

        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }

        Pageable pageable = PageRequest.of(page, 5, Sort.by("nom").ascending());
        model.addAttribute("users", userRepository.findByActifFalse(pageable));
        model.addAttribute("adminUser", session.getAttribute("admin"));

        return "admin/disabled";
    }
    
    @GetMapping("/users/disable/{id}")
    public String disableUser(@PathVariable Long id, HttpSession session) {
        if (isNotConnected(session)) {
            return "redirect:/admin/login";
        }
        User user = userRepository.findById(id).orElseThrow();
        user.setActif(false);
        userRepository.save(user);
        return "redirect:/admin/users";
    }

}