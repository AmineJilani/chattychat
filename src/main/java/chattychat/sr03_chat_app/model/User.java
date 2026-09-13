package chattychat.sr03_chat_app.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Entity
@Table(name = "users")
public class User implements Serializable {

    // Clé primaire de l'utilisateur
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    // Email unique obligatoire
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(unique = true, nullable = false)
    private String email;

    // Mot de passe de l'utilisateur
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 12, message = "Le mot de passe doit contenir au moins 12 caractères")
    private String password;

    // Permet de savoir si l'utilisateur est administrateur
    private Boolean admin = false;

    // Permet de désactiver un utilisateur sans le supprimer
    private Boolean actif = true;

    // Nombre de tentatives de connexion échouées
    private Integer nbTentatives = 0;

    // Permet de bloquer un compte
    private Boolean bloque = false;

    // Liste des invitations reçues par l'utilisateur
    @OneToMany(mappedBy = "user")
    private List<Invitation> invitations;

    // Liste des chats créés par l'utilisateur
    @OneToMany(mappedBy = "proprietaire")
    private List<Chat> chatsCrees;

    // Constructeur vide obligatoire pour JPA
    public User() {
    }

    // Constructeur complet
    public User(String nom, String prenom, String email, String password, Boolean admin, Boolean actif) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.admin = admin;
        this.actif = actif;
    }
    //GETTERS
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public Boolean getAdmin() { return admin; }
    public Boolean getActif() { return actif; }
    public Integer getNbTentatives() { return nbTentatives; }
    public Boolean getBloque() { return bloque; }
    public List<Invitation> getInvitations() { return invitations; }
    public List<Chat> getChatsCrees() { return chatsCrees; }
    //SETTERS
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setAdmin(Boolean admin) { this.admin = admin; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public void setNbTentatives(Integer nbTentatives) { this.nbTentatives = nbTentatives; }
    public void setBloque(Boolean bloque) { this.bloque = bloque; }
    public void setInvitations(List<Invitation> invitations) { this.invitations = invitations; }
    public void setChatsCrees(List<Chat> chatsCrees) { this.chatsCrees = chatsCrees; }
    
}