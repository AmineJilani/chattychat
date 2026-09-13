import { Navigate } from "react-router-dom";

/**
 * Protège les pages React.
 * Si aucun utilisateur n'est connecté,
 * on redirige vers la page de connexion login.
 */
function ProtectedRoute({ children }) {
    const connectedUser = JSON.parse(localStorage.getItem("user"));

    if (!connectedUser) {
        return <Navigate to="/login" replace />;
    }
    return children;
}

export default ProtectedRoute;