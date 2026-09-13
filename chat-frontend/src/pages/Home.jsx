import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getUser, getOwnerChats, getInvitedChats } from "../services/api";

function Home() {
    const [user, setUser] = useState(null);
    const [ownerChats, setOwnerChats] = useState([]);
    const [invitedChats, setInvitedChats] = useState([]);
    const [loading, setLoading] = useState(true);

    const connectedUser = JSON.parse(localStorage.getItem("user"));
    const userId = connectedUser.id;

    useEffect(() => {
        Promise.all([
            getUser(userId),
            getOwnerChats(userId),
            getInvitedChats(userId)
        ]).then(([userRes, ownerRes, invitedRes]) => {
            setUser(userRes.data);
            setOwnerChats(ownerRes.data);
            setInvitedChats(invitedRes.data);
            setLoading(false);
        }).catch((error) => {
            console.error("Erreur chargement accueil :", error);
            setLoading(false);
        });
    }, []);

    // Fusionne les deux listes en ajoutant un indicateur de rôle
    const allChats = [
        ...ownerChats.map((chat) => ({ ...chat, role: "Propriétaire" })),
        ...invitedChats.map((chat) => ({ ...chat, role: "Invité" }))
    ];

    if (loading) {
        return <div className="card">Chargement...</div>;
    }

    return (
        <div className="card">
            <h2>Accueil utilisateur</h2>

            {user && (
                <p className="welcome-text">
                    Bienvenue {user.prenom} {user.nom}
                </p>
            )}

            <h3>Mes chats</h3>

            {allChats.length === 0 ? (
                <p className="empty-state">Aucun chat pour le moment.</p>
            ) : (
                <div className="chat-list">
                    {allChats.map((chat) => (
                        <div className="chat-item" key={chat.id + chat.role}>

                            <div className="chat-item-info">
                                <span className="chat-item-title">{chat.titre}</span>

                                <span className={`chat-item-badge ${chat.role === "Propriétaire" ? "badge-owner" : "badge-invited"}`}>
                                    {chat.role}
                                </span>

                                <span className={`chat-item-status ${chat.valable ? "status-valid" : "status-expired"}`}>
                                    {chat.valable ? "Valable" : "Expiré"}
                                </span>
                            </div>

                            <Link to={`/chat/${chat.id}`} className="chat-open-btn">
                                Ouvrir le chat
                            </Link>

                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default Home;