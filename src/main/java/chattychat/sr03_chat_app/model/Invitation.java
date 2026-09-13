package chattychat.sr03_chat_app.model;


import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "invitations")
public class Invitation implements Serializable {

    // Clé primaire de l'invitation
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long id;

    // Utilisateur invité
    // Plusieurs invitations peuvent concerner le même utilisateur
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Chat concerné par l'invitation
    // Plusieurs invitations peuvent concerner le même chat
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    // Statut de l'invitation : EN_ATTENTE, ACCEPTEE, REFUSEE
    @Enumerated(EnumType.STRING)
    private StatutInvitation statut = StatutInvitation.EN_ATTENTE;
    
    // Constructeur vide obligatoire pour JPA
    public Invitation() {
    }

    // Constructeur complet
    public Invitation(User user, Chat chat, StatutInvitation statut) {
        this.user = user;
        this.chat = chat;
        this.statut = statut;
    }

    // GETTERS

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Chat getChat() {
        return chat;
    }

    public StatutInvitation getStatut() {
        return statut;
    }

    // SETTERS

    public void setUser(User user) {this.user = user;}

    public void setChat(Chat chat) {this.chat = chat;}

    public void setStatut(StatutInvitation statut) {this.statut = statut;}
}

