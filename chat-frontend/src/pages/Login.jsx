import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/api";

function Login() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const handleLogin = (e) => {
        e.preventDefault();

        login(email, password)
            .then((response) => {
                localStorage.setItem(
                    "user",
                    JSON.stringify(response.data)
                );

                navigate("/");
            })
			.catch((err) => {
			    if (err.response?.status === 403) {
			        setError("Votre compte est désactivé");
			    } else {
			        setError("Email ou mot de passe incorrect");
			    }
			});
    };

    return (
        <div className="card">
            <h2>Connexion utilisateur</h2>

            {error && <p style={{ color: "red" }}>{error}</p>}

            <form onSubmit={handleLogin}>
                <div>
                    <label>Email :</label>
                    <br />
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>

                <br />

                <div>
                    <label>Mot de passe :</label>
                    <br />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <br />

                <button type="submit">
                    Connexion
                </button>
            </form>
        </div>
    );
}

export default Login;