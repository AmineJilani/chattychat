import { useEffect, useState } from "react";
import { createChat, getAllUsers, inviteUsers } from "../services/api";

function PlanifierChat() {
    const connectedUser = JSON.parse(localStorage.getItem("user"));
    const userId = connectedUser.id;

    const [newChat, setNewChat] = useState({
        titre: "",
        description: "",
        dateCreation: "",
        dureeValiditeMinutes: 60,
        proprietaireId: userId
    });

    const [allUsers, setAllUsers] = useState([]);
    const [selectedUserIds, setSelectedUserIds] = useState([]);

    // Charge la liste des utilisateurs invitables (actifs, hors soi-même)
    useEffect(() => {
        getAllUsers()
            .then((response) => {
                const invitables = response.data.filter((u) => u.id !== userId);
                setAllUsers(invitables);
            })
            .catch((error) => {
                console.error("Erreur récupération utilisateurs :", error);
            });
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setNewChat({ ...newChat, [name]: value });
    };

    const handleSelectChange = (e) => {
        const selected = Array.from(e.target.selectedOptions).map((opt) => Number(opt.value));
        setSelectedUserIds(selected);
    };

    const handleCreateChat = (e) => {
        e.preventDefault();

        createChat({ ...newChat, proprietaireId: userId })
            .then((response) => {
                const createdChatId = response.data.id;

                // Si des invités ont été sélectionnés, on les invite juste après création
                if (selectedUserIds.length > 0) {
                    return inviteUsers(createdChatId, selectedUserIds);
                }
            })
            .then(() => {
                alert("Chat créé avec succès");

                setNewChat({
                    titre: "",
                    description: "",
                    dateCreation: "",
                    dureeValiditeMinutes: 60,
                    proprietaireId: userId
                });
                setSelectedUserIds([]);
            })
            .catch((error) => {
                console.error("Erreur création chat :", error);
                alert("Erreur lors de la création");
            });
    };

    return (
        <div className="card">
            <h2>Planifier une discussion</h2>

            <div className="planifier-layout">

                {/* COLONNE GAUCHE — formulaire du chat */}
                <form onSubmit={handleCreateChat} className="planifier-form">
                    <div>
                        <label>Titre :</label><br />
                        <input
                            type="text"
                            name="titre"
                            value={newChat.titre}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <br />

                    <div>
                        <label>Description :</label><br />
                        <textarea
                            name="description"
                            value={newChat.description}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <br />

                    <div>
                        <label>Date et heure :</label><br />
                        <input
                            type="datetime-local"
                            name="dateCreation"
                            value={newChat.dateCreation}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <br />

                    <div>
                        <label>Durée de validité en minutes :</label><br />
                        <input
                            type="number"
                            name="dureeValiditeMinutes"
                            value={newChat.dureeValiditeMinutes}
                            onChange={handleChange}
                            min="1"
                            required
                        />
                    </div>

                    <br />

                    <button type="submit">Créer le chat</button>
                </form>

                {/* COLONNE DROITE — sélection des invités */}
                <div className="planifier-invite">
                    <label htmlFor="inviteSelect">
                        Inviter des utilisateurs
                        <span className="hint"> (Ctrl/Cmd + clic pour plusieurs)</span>
                    </label>

                    <select
                        id="inviteSelect"
                        multiple
                        size="10"
                        className="multi-select"
                        onChange={handleSelectChange}
                    >
                        {allUsers.map((user) => (
                            <option key={user.id} value={user.id}>
                                {user.nom} {user.prenom} — {user.email}
                            </option>
                        ))}
                    </select>

                    {selectedUserIds.length > 0 && (
                        <p className="selected-count">
                            {selectedUserIds.length} utilisateur(s) sélectionné(s)
                        </p>
                    )}
                </div>

            </div>

        </div>
    );
}

export default PlanifierChat;