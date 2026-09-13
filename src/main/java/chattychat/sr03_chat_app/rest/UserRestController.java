package chattychat.sr03_chat_app.rest;

import org.springframework.web.bind.annotation.*;
import chattychat.sr03_chat_app.dto.UserDTO;
import chattychat.sr03_chat_app.model.User;
import java.util.List;
import java.util.stream.Collectors;

import chattychat.sr03_chat_app.repository.UserRepository;

/**
 * Web Service REST dédié aux utilisateurs.
 * Les méthodes de cette classe retournent du JSON.
 */
@RestController // Spring retourne JSON pas une page html
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retourne les informations d'un utilisateur.
     */
    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));

        return new UserDTO(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getAdmin(),
                user.getActif()
        );
    }
    // retourne tous les users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findByActifTrue().stream()
                .map(u -> new UserDTO(u.getId(), u.getNom(), u.getPrenom(), u.getEmail(), u.getAdmin(), u.getActif()))
                .collect(Collectors.toList());
    }
}