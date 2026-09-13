package chattychat.sr03_chat_app.rest;

import chattychat.sr03_chat_app.dto.LoginRequest;
import chattychat.sr03_chat_app.dto.UserDTO;
import chattychat.sr03_chat_app.model.User;
import chattychat.sr03_chat_app.repository.UserRepository;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
/*
 * API REST d'authentification qui permet aux users REACT de se connecter
 */
public class AuthRestController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthRestController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    //ResponseEntity<?> permet de retourner différentes types de corps de réponse  (? =wildcard)
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Utilisateur introuvable");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Mot de passe incorrect");
        }

        if (!user.getActif()) {
            return ResponseEntity.status(403).body("Compte désactivé");
        }

        return ResponseEntity.ok(new UserDTO(
            user.getId(), user.getNom(), user.getPrenom(),
            user.getEmail(), user.getAdmin(), user.getActif()
        ));
    }
}