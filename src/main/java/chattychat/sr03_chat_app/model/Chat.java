package chattychat.sr03_chat_app.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "chats")
public class Chat implements Serializable {

    // Clé primaire du chat
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    // Titre du salon de discussion
    private String titre;

    // Description du salon
    private String description;

    // Date et heure de début du chat
    private LocalDateTime dateDebut;

    // Durée de validité du chat en minutes
    private Integer dureeValiditeMinutes;

    // Propriétaire du chat
    // Plusieurs chats peuvent appartenir à un même utilisateur
    @ManyToOne
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private User proprietaire;

    // Liste des invitations liées à ce chat
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations;

    // Constructeur vide obligatoire pour JPA
    public Chat() {
    }

    // Constructeur complet
    public Chat(String titre, String description, LocalDateTime dateCreation,
                Integer dureeValiditeMinutes, User proprietaire) {
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateCreation;
        this.dureeValiditeMinutes = dureeValiditeMinutes;
        this.proprietaire = proprietaire;
    }

    // GETTERS
    public Long getId() {return id;}
    public String getTitre() {return titre;}

    public String getDescription() {return description;}

    public LocalDateTime getDateCreation() {return dateDebut;}

    public Integer getDureeValiditeMinutes() {return dureeValiditeMinutes;}

    public User getProprietaire() {return proprietaire;}

    public List<Invitation> getInvitations() {return invitations;}

    // SETTERS
    public void setTitre(String titre) {this.titre = titre;}

    public void setDescription(String description) {this.description = description;}

    public void setDateCreation(LocalDateTime dateDebut) {this.dateDebut = dateDebut;}

    public void setDureeValiditeMinutes(Integer dureeValiditeMinutes) {this.dureeValiditeMinutes = dureeValiditeMinutes;}

    public void setProprietaire(User proprietaire) {this.proprietaire = proprietaire;}

    public void setInvitations(List<Invitation> invitations) {this.invitations = invitations; }
    
    //Autres méthodes
    
    public LocalDateTime getDateExpiration() {
        if (dateDebut == null || dureeValiditeMinutes == null) {
            return null;
        }

        return dateDebut.plusMinutes(dureeValiditeMinutes);
    }

    public boolean isValable() {
        LocalDateTime expiration = getDateExpiration();

        if (expiration == null) {
            return false;
        }

        return LocalDateTime.now().isBefore(expiration);
    }
}