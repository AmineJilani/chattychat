# ChattyChat

Application de chat en salons de discussion temporaires, sans persistance des messages : les échanges ne sont diffusés qu'en temps réel entre les utilisateurs connectés à un même salon, sans jamais être enregistrés en base.

## Contexte

Ce projet a été réalisé dans le cadre du module **SR03** (Génie Informatique) de l'**Université de Technologie de Compiègne (UTC)**, durant l'année universitaire 2025-2026, sous l'encadrement de Mohammad Beyrouti. Il a été développé en binôme.

## Stack technique

**Backend**
- Java 21
- Spring Boot (MVC, WebSocket)
- Spring Data JPA / Hibernate
- WebSocket + protocole STOMP
- MySQL
- Thymeleaf (vues de l'interface d'administration)
- Spring Security (utilisé uniquement pour le hachage BCrypt des mots de passe)

**Frontend**
- React (Vite)
- React Router
- Axios
- SockJS + STOMP.js (client WebSocket)

## Architecture

L'application repose sur une **unique application Spring Boot** qui expose trois façades distinctes selon le type de client, toutes connectées au même socle de données (entités `User`, `Chat`, `Invitation` et repositories Spring Data JPA sur une base MySQL partagée) :

- une interface **d'administration** en MVC classique, avec rendu HTML côté serveur via Thymeleaf ;
- une **API REST** (JSON) consommée par l'application React, qui s'exécute de façon totalement indépendante ;
- un **service WebSocket/STOMP** dédié à la diffusion des messages en temps réel, activé dès qu'un utilisateur ouvre une fenêtre de discussion.

```
┌────────────────────┐        ┌────────────────────┐
│    Navigateur       │        │     Navigateur       │
│   Administrateur    │        │    Utilisateur       │
│   (pages HTML)       │        │   (SPA React)        │
└──────────┬──────────┘        └──────────┬──────────┘
           │ HTTP (Thymeleaf)              │ REST (JSON) + WebSocket/STOMP
           ▼                                ▼
┌─────────────────────────────────────────────────────┐
│                 Application Spring Boot               │
│  ┌───────────┐    ┌────────────┐    ┌─────────────┐   │
│  │    MVC     │    │    REST     │    │  WebSocket   │   │
│  │ Thymeleaf  │    │ Controllers │    │    /STOMP    │   │
│  └─────┬──────┘    └──────┬─────┘    └──────┬──────┘   │
│        └──────────────────┴─────────────────┘          │
│              Entités JPA + Repositories                │
└───────────────────────────┬───────────────────────────┘
                             ▼
                    Base de données MySQL
```

Les deux applications (Spring Boot et React) sont déployées et lancées indépendamment l'une de l'autre.

## Fonctionnalités

- Authentification utilisateur (React) et administrateur (interface MVC), avec hachage BCrypt des mots de passe et blocage du compte après 3 tentatives échouées côté admin.
- Gestion complète des utilisateurs côté administration (création, édition, activation/désactivation, suppression, recherche et pagination).
- Planification de salons de discussion (titre, description, date de début, durée de validité) avec sélection multiple d'invités.
- Gestion des salons et des invitations (création, modification, suppression) côté React et côté administration.
- Chat en temps réel via WebSocket/STOMP : diffusion instantanée des messages et liste des utilisateurs connectés par salon.
- Expiration automatique des salons, calculée à partir de la date de début et de la durée de validité définies.

## Installation et lancement

**Prérequis**
- Java 21
- Node.js (avec npm)
- Une instance MySQL locale

**Backend**

1. Copier le fichier d'exemple de configuration :
   ```
   cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
   ```
2. Renseigner dans ce fichier l'URL, l'utilisateur et le mot de passe de votre base MySQL locale.
3. Lancer l'application avec le profil `local` activé :
   ```
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
   Le serveur démarre sur `http://localhost:8088`.

**Frontend**

```
cd chat-frontend
npm install
npm run dev
```

L'application React démarre sur `http://localhost:5173`.

## Limites connues

- Absence d'authentification robuste sur l'API REST et sur le WebSocket après la connexion initiale : le serveur fait confiance à l'identifiant transmis par le client React, sans vérification de session ou de jeton.
- La protection des routes côté React (`ProtectedRoute`) n'est assurée que côté client et peut être contournée via les outils du navigateur.
- L'application React n'est pas packagée ni intégrée au serveur Spring Boot (pas de `npm run build` servi en statique) : les deux applications doivent être lancées séparément, ce qui ne correspond pas à un déploiement de production.
- La gestion des déconnexions WebSocket repose sur l'envoi explicite d'un message `LEAVE` : une fermeture brutale du navigateur ou une coupure réseau peut laisser un utilisateur affiché comme connecté à tort.
