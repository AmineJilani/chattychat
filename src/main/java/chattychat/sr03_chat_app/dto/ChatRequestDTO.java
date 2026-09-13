package chattychat.sr03_chat_app.dto;

import java.time.LocalDateTime;

/**
 * DTO utilisé quand React envoie les données pour créer ou modifier un chat.
 */
public class ChatRequestDTO {

    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private Integer dureeValiditeMinutes;
    private Long proprietaireId;

    public ChatRequestDTO() {
    }

    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public Integer getDureeValiditeMinutes() { return dureeValiditeMinutes; }
    public Long getProprietaireId() { return proprietaireId; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public void setDureeValiditeMinutes(Integer dureeValiditeMinutes) { this.dureeValiditeMinutes = dureeValiditeMinutes; }
    public void setProprietaireId(Long proprietaireId) { this.proprietaireId = proprietaireId; }
}