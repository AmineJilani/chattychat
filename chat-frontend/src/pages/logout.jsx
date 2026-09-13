import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

/**
 * Déconnecte l'utilisateur React.
 * On supprime l'utilisateur du localStorage,
 * puis on retourne vers la page login.
 */
function Logout() {
    const navigate = useNavigate();

    useEffect(() => {
        localStorage.removeItem("user");
        navigate("/login");
    }, []);

    return <p>Déconnexion...</p>;
}

export default Logout;