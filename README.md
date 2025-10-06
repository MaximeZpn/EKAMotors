# EKA Motors - Site e-commerce de véhicules (Microservices)

## Description

Site e-commerce pour EKA Motors permettant de lister, vendre et acheter des voitures, basé sur une architecture microservices.

## Installation

1. Cloner le projet
```bash
git clone <repository-url>
cd atelier-3-et-4
```

2. Lancer les services
```bash
docker-compose up -d
```

## Accès à l'application

L'application est accessible sur `http://localhost`

### Points d'entrée Web (Frontend)

- **Page d'accueil**: `http://localhost/`
- **Connexion**: `http://localhost/connexion.html`
- **Inscription**: `http://localhost/inscription.html`
- **Catalogue**: `http://localhost/catalogue.html`
- **Vendre une voiture**: `http://localhost/vendre.html`
- **Fiche véhicule**: `http://localhost/voiture-details.html?id={vehiculeId}`

### API REST (Backend)

#### Auth Service (8081)
- POST `/api/auth/register` - Inscription
- POST `/api/auth/login` - Connexion
- GET `/api/auth/validate` - Validation du token
- GET `/api/auth/health` - État du service
- GET `/api/auth/debug` - Informations de débogage

#### User Service (8082)
- GET `/api/utilisateurs/{id}` - Détails utilisateur
- GET `/api/utilisateurs` - Liste tous les utilisateurs
- GET `/api/utilisateurs/username/{username}` - Recherche par nom d'utilisateur
- GET `/api/utilisateurs/exists/{username}` - Vérifie si un nom d'utilisateur existe
- PUT `/api/utilisateurs/{id}/solde` - Modifier le solde
- POST `/api/utilisateurs` - Créer un utilisateur
- GET `/api/utilisateurs/health` - État du service

#### Market Service (8083)
- GET `/api/market/offres` - Liste toutes les offres
- GET `/api/market/offres/actives` - Liste les offres actives
- GET `/api/market/offres/{id}` - Détails d'une offre
- GET `/api/market/offres/vendeur/{vendeurId}` - Offres d'un vendeur
- GET `/api/market/offres/vendeur/{vendeurId}/actives` - Offres actives d'un vendeur
- POST `/api/market/offres` - Créer une offre
- DELETE `/api/market/offres/{id}` - Retirer une offre
- POST `/api/market/acheter/{offreId}` - Acheter une carte
- GET `/api/market/transactions` - Liste toutes les transactions
- GET `/api/market/transactions/vendeur/{vendeurId}` - Transactions d'un vendeur
- GET `/api/market/transactions/acheteur/{acheteurId}` - Transactions d'un acheteur
- GET `/api/market/transactions/{id}` - Détails d'une transaction
- GET `/api/market/health` - État du service

#### Véhicule Service (8085)
- GET `/api/cartes` - Liste tous les véhicules
- GET `/api/cartes/{id}` - Détails d'un véhicule
- POST `/api/cartes` - Créer un véhicule (annonce)
- PUT `/api/cartes/{id}` - Modifier un véhicule
- DELETE `/api/cartes/{id}` - Supprimer un véhicule
- GET `/api/cartes/utilisateur/{id}` - Véhicules d'un utilisateur
- PUT `/api/cartes/{id}/proprietaire` - Changer le propriétaire
- GET `/api/cartes/{id}/energy` - Voir l'autonomie d'un véhicule
- PUT `/api/cartes/{id}/energy` - Modifier l'autonomie
- POST `/api/cartes/regenerate-energy` - Régénérer l'autonomie de tous les véhicules
- GET `/api/cartes/ping` - Test de connexion
- GET `/api/cartes/health` - État du service
- GET `/api/debug/info` - Informations système
- GET `/api/debug/echo` - Test d'écho
- POST `/api/debug/echo` - Test d'écho avec payload

#### Game Service (8086)
- GET `/api/game/rooms` - Liste des salles disponibles
- GET `/api/game/rooms/{id}` - Détails d'une salle
- POST `/api/game/rooms` - Créer une salle
- POST `/api/game/rooms/{id}/join` - Rejoindre une salle
- GET `/api/game/rooms/player/{playerId}` - Salles d'un joueur
- GET `/api/game/battles/room/{roomId}` - Résultats d'une bataille
- GET `/api/game/debug/ping` - Test de connexion
- GET `/api/game/debug/check-services` - Vérification des services
- GET `/api/game/debug/card/{cardId}/check` - Vérification d'une carte
- GET `/api/game/health` - État du service

### Outils de développement

- **Adminer (DB Manager)**: `http://localhost:8888`
  - System: MySQL
  - Serveurs disponibles:
    - mysql-auth (auth_db)
    - mysql-user (user_db)
    - mysql-vehicule (vehicule_db)
    - mysql-market (market_db)
    - mysql-game (game_db)

## Guide d'utilisation

1. **Création de compte**
   - Accédez à la page d'inscription
   - Créez un compte avec email et mot de passe

2. **Obtenir des cartes**
   - Connectez-vous
   - Vous recevrez des cartes de départ automatiquement

3. **Marché des cartes**
   - Achetez/Vendez des cartes via le catalogue
   - Gérez vos offres

4. **Mode Jeu**
   - Créez ou rejoignez une salle
   - Choisissez une carte pour combattre
   - Consultez les résultats

## Développement

Pour arrêter les services :
```bash
docker-compose down
```

Pour reconstruire les services après modifications :
```bash
docker-compose up -d --build
```

Pour voir les logs :
```bash
docker-compose logs -f
```

## Architecture

- Frontend servi par Nginx
- 5 microservices Spring Boot
- Base de données MySQL dédiée par service
- Communication inter-services via REST
- Authentification JWT