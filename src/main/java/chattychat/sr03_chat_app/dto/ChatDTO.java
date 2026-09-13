package chattychat.sr03_chat_app.dto;

import java.time.LocalDateTime;

/**
 * DTO utilisé pour envoyer les informations d'un chat au frontend React.
 * Il contient aussi le statut calculé du chat : valable ou expiré.
 */
public class ChatDTO {

    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private Integer dureeValiditeMinutes;
    private LocalDateTime dateExpiration;
    private Boolean valable;

    private Long proprietaireId;
    private String proprietaireNom;

    public ChatDTO() {
    }

    public ChatDTO(Long id, String titre, String description,
                   LocalDateTime dateCreation,
                   Integer dureeValiditeMinutes,
                   LocalDateTime dateExpiration,
                   Boolean valable,
                   Long proprietaireId,
                   String proprietaireNom) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dureeValiditeMinutes = dureeValiditeMinutes;
        this.dateExpiration = dateExpiration;
        this.valable = valable;
        this.proprietaireId = proprietaireId;
        this.proprietaireNom = proprietaireNom;
    }

    public Long getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Integer getDureeValiditeMinutes() { return dureeValiditeMinutes; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public Boolean getValable() { return valable; }
    public Long getProprietaireId() { return proprietaireId; }
    public String getProprietaireNom() { return proprietaireNom; }

    public void setId(Long id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public void setDureeValiditeMinutes(Integer dureeValiditeMinutes) { this.dureeValiditeMinutes = dureeValiditeMinutes; }
    public void setDateExpiration(LocalDateTime dateExpiration) { this.dateExpiration = dateExpiration; }
    public void setValable(Boolean valable) { this.valable = valable; }
    public void setProprietaireId(Long proprietaireId) { this.proprietaireId = proprietaireId; }
    public void setProprietaireNom(String proprietaireNom) { this.proprietaireNom = proprietaireNom; }
}