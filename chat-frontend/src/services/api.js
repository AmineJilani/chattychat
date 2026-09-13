import axios from "axios";

const API_URL = "http://localhost:8088/api";


/**
 * Authentifie un utilisateur avec email + mot de passe.
 */
export const login = (email, password) => {
    return axios.post(`${API_URL}/auth/login`, {
        email,
        password
    });
};
/**
 * Appelle l'API Spring Boot pour récupérer les informations d'un utilisateur.
 */
export const getUser = (id) => {
    return axios.get(`${API_URL}/users/${id}`);
};

/**
 * Récupère les chats créés par l'utilisateur.
 */
export const getOwnerChats = (userId) => {
    return axios.get(`${API_URL}/chats/owner/${userId}`);
};

/**
 * Récupère les chats où l'utilisateur est invité.
 */
export const getInvitedChats = (userId) => {
    return axios.get(`${API_URL}/chats/invited/${userId}`);
};

/**
 * Crée un nouveau chat.
 */
export const createChat = (chat) => {
    return axios.post(`${API_URL}/chats`, chat);
};

/**
 * Modifie un chat.
 */
export const updateChat = (id, chat) => {
    return axios.put(`${API_URL}/chats/${id}`, chat);
};

/**
 * Supprime un chat.
 */
export const deleteChat = (id) => {
    return axios.delete(`${API_URL}/chats/${id}`);
};
/**
 * Récupère les détails d'un chat précis (titre, expiration...).
 */
export const getChatById = (id) => {
    return axios.get(`${API_URL}/chats/${id}`);
};
/**
 * Invite une liste d'utilisateurs (par leurs ids) à un chat.
 */
export const inviteUsers = (chatId, userIds) => {
    return axios.post(`${API_URL}/chats/${chatId}/invite`, { userIds });
};
//récuéprer tous les users
export const getAllUsers = () => {
    return axios.get(`${API_URL}/users`);
};