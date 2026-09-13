package chattychat.sr03_chat_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import chattychat.sr03_chat_app.model.User;


public interface UserRepository extends JpaRepository<User, Long> {

	 // Trouver un utilisateur par email, utile pour login
    Optional<User> findByEmail(String email);
    
    //vérifier si un utilisateur exite par email --> éviter les doublons
    boolean existsByEmail(String email);

    // Chercher les utilisateurs actifs
    List<User> findByActifTrue();

    // Chercher les utilisateurs désactivés
    List<User> findByActifFalse();
    
    //Onglet "comptes désactivés"
    Page<User> findByActifTrue(Pageable pageable);

    Page<User> findByActifFalse(Pageable pageable);

    // Rechercher par nom, prénom ou email
    Page<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
    	String nom,
        String prenom,
        String email,
        Pageable pageable
    );
}