import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import { getChatById } from "../services/api";


/*
*Composant représentant une fenetre de chat
*/
function ChatRoom() {

    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");
    const stompClientRef = useRef(null);

    const { chatId } = useParams(); //recupere l'id de salon ouvert
    const [connectedUsers, setConnectedUsers] = useState([]);

    const [chatInfo, setChatInfo] = useState(null);
    const [timeLeft, setTimeLeft] = useState("");

    //Récuprer le user conncecté
    const connectedUser = JSON.parse(localStorage.getItem("user"));
    //stocker son prénom+nom
    const username = connectedUser.prenom + " " + connectedUser.nom;

    // Récupère les infos du chat (titre, date d'expiration) au chargement
    useEffect(() => {
        getChatById(chatId)
            .then((response) => {
                setChatInfo(response.data);
            })
            .catch((error) => {
                console.error("Erreur récupération du chat :", error);
            });
    }, [chatId]);

    // Calcule et met à jour le temps restant chaque seconde
    useEffect(() => {
        if (!chatInfo || !chatInfo.dateExpiration) return;

        const updateTimeLeft = () => {
            const expiration = new Date(chatInfo.dateExpiration).getTime();
            const now = Date.now();
            const diff = expiration - now;

            if (diff <= 0) {
                setTimeLeft("Expiré");
                return;
            }

            const minutes = Math.floor(diff / 60000);
            const seconds = Math.floor((diff % 60000) / 1000);
            setTimeLeft(`${minutes} min ${seconds.toString().padStart(2, "0")} s`);
        };

        updateTimeLeft();
        const interval = setInterval(updateTimeLeft, 1000);

        return () => clearInterval(interval);
    }, [chatInfo]);

    //initialiser la connexion au moment de chargement du composant
    useEffect(() => {
        const socket = new SockJS("http://localhost:8088/ws");
        const client = new Client({
            webSocketFactory: () => socket,

            onConnect: () => {
                console.log("Connecté au WebSocket");
                // le user connecté recoit uniquement les messages de chat dans le quel il est connecté grace au chatId
                client.subscribe(`/topic/chat/${chatId}`, (msg) => {
                    const receivedMessage = JSON.parse(msg.body);

                    if (receivedMessage.type === "JOIN") {
                        setConnectedUsers((prev) => {
                            if (prev.includes(receivedMessage.sender)) {
                                return prev;
                            }
                            return [...prev, receivedMessage.sender];
                        });
                    }

                    if (receivedMessage.type === "LEAVE") {
                        setConnectedUsers((prev) =>
                            prev.filter((user) => user !== receivedMessage.sender)
                        );
                    }

                    setMessages((prev) => [
                        ...prev,
                        receivedMessage
                    ]);
                });
				// Dans ChatRoom.jsx, à l'intérieur de onConnect, juste après le premier subscribe

				client.subscribe(`/topic/chat/${chatId}/users`, (msg) => {
				    const userList = JSON.parse(msg.body);
				    setConnectedUsers(userList); // remplace toute la liste, pas un ajout
				});
                const joinMessage = {
                    chatId: Number(chatId),
                    sender: username,
                    content: "a rejoint le chat",
                    type: "JOIN"
                };

                client.publish({
                    destination: "/app/chat.sendMessage",
                    body: JSON.stringify(joinMessage)
                });
            },

            onStompError: (frame) => {
                console.error("Erreur STOMP :", frame);
            },

            onWebSocketError: (error) => {
                console.error("Erreur WebSocket :", error);
            }
        });

        client.activate();
        stompClientRef.current = client;
        return () => {
            if (client.connected) {
                const leaveMessage = {
                    chatId: Number(chatId),
                    sender: username,
                    content: "a quitté le chat",
                    type: "LEAVE"
                };

                client.publish({
                    destination: "/app/chat.sendMessage",
                    body: JSON.stringify(leaveMessage)
                });
            }

            client.deactivate();
        };

    }, []);

    //Envoie un message au serveur WebSocket
    const sendMessage = () => {
        if (!message.trim()) {
            return;
        }

        if (!stompClientRef.current || !stompClientRef.current.connected) {
            alert("WebSocket non connecté");
            return;
        }

        const chatMessage = {
            chatId: Number(chatId),
            sender: username,
            content: message,
            type: "CHAT"
        };

        stompClientRef.current.publish({
            destination: "/app/chat.sendMessage",
            body: JSON.stringify(chatMessage)
        });

        setMessage("");
    };

    return (
        <div className="chatroom-layout">

            {/* COLONNE PRINCIPALE — fil de discussion */}
            <div className="chatroom-main card">

                <div className="chatroom-header">
                    <h2>{chatInfo ? chatInfo.titre : "Chat temps réel"}</h2>

                    {chatInfo && (
                        <span className={`time-left ${timeLeft === "Expiré" ? "time-expired" : ""}`}>
                            {timeLeft === "Expiré" ? "Expiré" : `Expire dans : ${timeLeft}`}
                        </span>
                    )}
                </div>

                <div className="messages-box">
                    {messages.map((msg, index) => (
                        <div key={index} className="message-line">
                            <strong>{msg.sender}</strong> : {msg.content}
                        </div>
                    ))}
                </div>

                <div className="message-input-row">
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Écrire un message..."
                    />
                    <button onClick={sendMessage}>Envoyer</button>
                </div>

            </div>

            {/* COLONNE LATÉRALE — utilisateurs connectés */}
            <div className="connected-users card">
                <h3>Utilisateurs connectés</h3>

                {connectedUsers.length === 0 ? (
                    <p className="empty-state">Aucun utilisateur connecté</p>
                ) : (
                    <ul className="connected-users-list">
                        {connectedUsers.map((user, index) => (
                            <li key={index}>
                                <span className="status-dot"></span>
                                {user}
                            </li>
                        ))}
                    </ul>
                )}
            </div>

        </div>
    );
}

export default ChatRoom;