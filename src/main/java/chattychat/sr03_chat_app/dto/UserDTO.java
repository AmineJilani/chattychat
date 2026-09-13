package chattychat.sr03_chat_app.dto;

/**
 * DTO utilisé pour envoyer les informations d'un utilisateur au frontend React.
 * On n'envoie pas le mot de passe pour des raisons de sécurité.
 */
public class UserDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Boolean admin;
    private Boolean actif;

    public UserDTO() {
    }

    public UserDTO(Long id, String nom, String prenom, String email, Boolean admin, Boolean actif) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.admin = admin;
        this.actif = actif;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public Boolean getAdmin() { return admin; }
    public Boolean getActif() { return actif; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setEmail(String email) { this.email = email; }
    public void setAdmin(Boolean admin) { this.admin = admin; }
    public void setActif(Boolean actif) { this.actif = actif; }
}