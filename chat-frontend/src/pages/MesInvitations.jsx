import { useEffect, useState } from "react";
import { getInvitedChats } from "../services/api";

function MesInvitations() {
	// récuperer le user connecté et sotcker son id
	const connectedUser = JSON.parse(localStorage.getItem("user"));

	const userId = connectedUser.id;  const [invitedChats, setInvitedChats] = useState([]);
  useEffect(() => {
    getInvitedChats(userId)
      .then((response) => {
        setInvitedChats(response.data);
      })
      .catch((error) => {
        console.error("Erreur récupération invitations :", error);
      });
  }, []);

  return (
	<div className="card">
      <h2>Mes invitations</h2>

      {invitedChats.length === 0 ? (
        <p>Aucune invitation.</p>
      ) : (
        <table border="1" cellPadding="10">
          <thead>
            <tr>
              <th>Titre</th>
              <th>Description</th>
              <th>Propriétaire</th>
              <th>Statut</th>
            </tr>
          </thead>

          <tbody>
            {invitedChats.map((chat) => (
              <tr key={chat.id}>
                <td>{chat.titre}</td>
                <td>{chat.description}</td>
                <td>{chat.proprietaireNom}</td>
                <td>{chat.valable ? "Valable" : "Expiré"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default MesInvitations;