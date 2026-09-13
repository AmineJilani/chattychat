package chattychat.sr03_chat_app.controller;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import chattychat.sr03_chat_app.model.User;
import chattychat.sr03_chat_app.repository.UserRepository;
import jakarta.servlet.http.HttpSession;


@Controller
public class AdminAuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AdminAuthController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @GetMapping("/")
    public String root() {
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Utilisateur introuvable");
            return "admin/login";
        }

        User user = optionalUser.get();

        if (!Boolean.TRUE.equals(user.getAdmin())) {
            model.addAttribute("error", "Accès réservé aux administrateurs");
            return "admin/login";
        }

        if (!Boolean.TRUE.equals(user.getActif())) {
            model.addAttribute("error", "Compte désactivé");
            return "admin/login";
        }

        if (Boolean.TRUE.equals(user.getBloque())) {
            model.addAttribute("error", "Compte bloqué temporairement");
            return "admin/login";
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setNbTentatives(user.getNbTentatives() + 1);

            if (user.getNbTentatives() >= 3) {
                user.setBloque(true);
            }

            userRepository.save(user);

            model.addAttribute("error", "Mot de passe incorrect");
            return "admin/login";
        }

        user.setNbTentatives(0);
        userRepository.save(user);

        session.setAttribute("admin", user);

        return "redirect:/admin";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}