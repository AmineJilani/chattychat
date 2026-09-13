import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getOwnerChats, updateChat, deleteChat, getAllUsers, inviteUsers } from "../services/api";

function MesSalons() {
    const connectedUser = JSON.parse(localStorage.getItem("user"));
    const userId = connectedUser.id;

    const [ownerChats, setOwnerChats] = useState([]);
    const [editingChat, setEditingChat] = useState(null);

    const [invitingChat, setInvitingChat] = useState(null); // chat en cours d'invitation
    const [allUsers, setAllUsers] = useState([]);
    const [selectedUserIds, setSelectedUserIds] = useState([]);

    const loadChats = () => {
        getOwnerChats(userId)
            .then((response) => {
                setOwnerChats(response.data);
            })
            .catch((error) => {
                console.error("Erreur récupération salons :", error);
            });
    };

    useEffect(() => {
        loadChats();

        getAllUsers()
            .then((response) => {
                setAllUsers(response.data.filter((u) => u.id !== userId));
            })
            .catch((error) => {
                console.error("Erreur récupération utilisateurs :", error);
            });
    }, []);

    const handleDeleteChat = (id) => {
        if (!window.confirm("Supprimer ce chat ?")) return;

        deleteChat(id)
            .then(() => {
                alert("Chat supprimé");
                loadChats();
            })
            .catch((error) => {
                console.error("Erreur suppression :", error);
            });
    };

    const handleEditClick = (chat) => {
        setEditingChat({
            id: chat.id,
            titre: chat.titre,
            description: chat.description,
            dateCreation: chat.dateCreation?.slice(0, 16),
            dureeValiditeMinutes: chat.dureeValiditeMinutes,
            proprietaireId: userId
        });
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditingChat({ ...editingChat, [name]: value });
    };

    const handleUpdateChat = (e) => {
        e.preventDefault();

        updateChat(editingChat.id, {
            titre: editingChat.titre,
            description: editingChat.description,
            dateCreation: editingChat.dateCreation,
            dureeValiditeMinutes: editingChat.dureeValiditeMinutes,
            proprietaireId: userId
        })
            .then(() => {
                alert("Chat modifié");
                setEditingChat(null);
                loadChats();
            })
            .catch((error) => {
                console.error("Erreur modification :", error);
            });
    };

    // Ouvre le panneau d'invitation pour un chat précis
    const handleInviteClick = (chat) => {
        setInvitingChat(chat);
        setSelectedUserIds([]);
    };

    const handleInviteSelectChange = (e) => {
        const selected = Array.from(e.target.selectedOptions).map((opt) => Number(opt.value));
        setSelectedUserIds(selected);
    };

    const handleSendInvites = () => {
        if (selectedUserIds.length === 0) {
            alert("Sélectionnez au moins un utilisateur.");
            return;
        }

        inviteUsers(invitingChat.id, selectedUserIds)
            .then(() => {
                alert("Invitations envoyées");
                setInvitingChat(null);
                setSelectedUserIds([]);
            })
            .catch((error) => {
                console.error("Erreur envoi invitations :", error);
                alert("Erreur lors de l'envoi des invitations");
            });
    };

    return (
        <div>
            <h2>Mes salons de discussion</h2>

            {editingChat && (
                <div className="card">
                    <h3>Modifier le chat</h3>

                    <form onSubmit={handleUpdateChat}>
                        <input
                            type="text"
                            name="titre"
                            value={editingChat.titre}
                            onChange={handleEditChange}
                            required
                        />

                        <br /><br />

                        <textarea
                            name="description"
                            value={editingChat.description}
                            onChange={handleEditChange}
                            required
                        />

                        <br /><br />

                        <input
                            type="datetime-local"
                            name="dateCreation"
                            value={editingChat.dateCreation}
                            onChange={handleEditChange}
                            required
                        />

                        <br /><br />

                        <input
                            type="number"
                            name="dureeValiditeMinutes"
                            value={editingChat.dureeValiditeMinutes}
                            onChange={handleEditChange}
                            min="1"
                            required
                        />

                        <br /><br />

                        <button type="submit">Enregistrer</button>
                        <button type="button" onClick={() => setEditingChat(null)}>
                            Annuler
                        </button>
                    </form>

                    <hr />
                </div>
            )}

            {invitingChat && (
                <div className="card">
                    <h3>Inviter des utilisateurs — {invitingChat.titre}</h3>

                    <select
                        multiple
                        size="8"
                        className="multi-select"
                        onChange={handleInviteSelectChange}
                    >
                        {allUsers.map((user) => (
                            <option key={user.id} value={user.id}>
                                {user.nom} {user.prenom} — {user.email}
                            </option>
                        ))}
                    </select>

                    <br /><br />

                    <button type="button" onClick={handleSendInvites}>
                        Envoyer les invitations
                    </button>
                    <button type="button" onClick={() => setInvitingChat(null)}>
                        Annuler
                    </button>

                    <hr />
                </div>
            )}

            {ownerChats.length === 0 ? (
                <p>Aucun salon créé.</p>
            ) : (
                <table border="1" cellPadding="10">
                    <thead>
                        <tr>
                            <th>Titre</th>
                            <th>Description</th>
                            <th>Début</th>
                            <th>Expiration</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                        {/* Parcourir la liste des salons avec map */}
                        {ownerChats.map((chat) => (
                            <tr key={chat.id}>
                                <td>{chat.titre}</td>
                                <td>{chat.description}</td>
                                <td>{chat.dateCreation}</td>
                                <td>{chat.dateExpiration}</td>
                                <td>{chat.valable ? "Valable" : "Expiré"}</td>
                                <td>
                                    <button onClick={() => handleEditClick(chat)}>Modifier</button>
                                    <button onClick={() => handleDeleteChat(chat.id)}>Supprimer</button>
                                    <button onClick={() => handleInviteClick(chat)}>Inviter</button>
                                    <Link to={`/chat/${chat.id}`}>
                                        Ouvrir le chat
                                    </Link>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default MesSalons;