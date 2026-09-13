import { useState, useEffect } from "react";
import { BrowserRouter, Routes, Route, Link, useLocation } from "react-router-dom";
import "./App.css";
import Login from "./pages/Login";
import Logout from "./pages/Logout";
import Home from "./pages/Home";
import ProtectedRoute from "./components/ProtectedRoute";
import PlanifierChat from "./pages/PlanifierChat";
import MesSalons from "./pages/MesSalons";
import MesInvitations from "./pages/MesInvitations";
import ChatRoom from "./pages/ChatRoom";


function App() {
    const [connectedUser, setConnectedUser] = useState(
        JSON.parse(localStorage.getItem("user"))
    );

    return (
        <BrowserRouter>
            <AppContent connectedUser={connectedUser} setConnectedUser={setConnectedUser} />
        </BrowserRouter>
    );
}

function AppContent({ connectedUser, setConnectedUser }) {
    const location = useLocation();

    // À chaque changement de route, on relit localStorage
    useEffect(() => {
        setConnectedUser(JSON.parse(localStorage.getItem("user")));
    }, [location]);

    return (
        <div className="app">
            <header className="header">
                <div className="logo">CHATTYCHAT</div>
                <nav className="nav">
                    {connectedUser ? (
                        <>
                            <Link to="/">Accueil</Link>
                            <Link to="/planifier">Planifier</Link>
                            <Link to="/mes-salons">Mes salons</Link>
                            <Link to="/mes-invitations">Mes invitations</Link>
                            <Link to="/logout">Déconnexion</Link>
                        </>
                    ) : (
                        <Link to="/login">Connexion</Link>
                    )}
                </nav>
            </header>

            <main className="container">
			<Routes>
					      <Route path="/login" element={<Login />} />
					      <Route path="/logout" element={<Logout />} />

					      <Route
					          path="/"
					          element={
					              <ProtectedRoute>
					                  <Home />
					              </ProtectedRoute>
					          }
					      />

					      <Route
					          path="/planifier"
					          element={
					              <ProtectedRoute>
					                  <PlanifierChat />
					              </ProtectedRoute>
					          }
					      />

					      <Route
					          path="/mes-salons"
					          element={
					              <ProtectedRoute>
					                  <MesSalons />
					              </ProtectedRoute>
					          }
					      />

					      <Route
					          path="/mes-invitations"
					          element={
					              <ProtectedRoute>
					                  <MesInvitations />
					              </ProtectedRoute>
					          }
					      />

					      <Route
					          path="/chat/:chatId"
					          element={
					              <ProtectedRoute>
					                  <ChatRoom />
					              </ProtectedRoute>
					          }
					      />
					  </Routes>
            </main>
        </div>
    );
}

export default App;

