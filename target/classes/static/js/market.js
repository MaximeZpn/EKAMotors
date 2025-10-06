document.addEventListener("DOMContentLoaded", function () {
    let selectedCardId = null;
    let utilisateurId = null;

    // Récupérer l'utilisateur connecté
    fetch("/api/auth/utilisateur")
        .then(response => response.json())
        .then(user => {
            if (user.id) {
                utilisateurId = user.id;
                updateUserInfo(user); // Met à jour l'affichage utilisateur
                loadCards(user); // Charge les cartes en fonction de l'utilisateur connecté
            } else {
                throw new Error("Utilisateur non connecté");
            }
        })
        .catch(error => {
            alert("Veuillez vous connecter !");
            window.location.href = "connexion.html";
        });

    // Charger les cartes en vente
    function loadCards(user) {
        fetch("/market/cartes-en-vente", { cache: "no-store" })
            .then(response => response.json())
            .then(cartes => {
                const tableBody = document.querySelector("#cartesTable tbody");
                tableBody.innerHTML = ""; // Nettoyer le tableau avant d'ajouter les cartes

                cartes.forEach(carte => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${carte.nom}</td>
                        <td>${carte.rarete}</td>
                        <td>${carte.niveau}</td>
                        <td>${carte.prix} $</td>
                        <td>${carte.proprietaire}</td>
                    `;
                    row.addEventListener("click", () => selectCard(carte, row, user));
                    tableBody.appendChild(row);
                });
            })
            .catch(error => console.error("Erreur de chargement des cartes :", error));
    }

    // Sélectionner une carte
    function selectCard(carte, row, user) {
        selectedCardId = carte.id;

        // Mettre en surbrillance la ligne sélectionnée
        document.querySelectorAll("#cartesTable tr").forEach(tr => tr.classList.remove("selected"));
        row.classList.add("selected");

        // Afficher les détails de la carte
        document.getElementById("imageCarte").src = carte.imageUrl || "/images/default-card.png";
        document.getElementById("nomCarte").innerText = carte.nom;
        document.getElementById("descriptionCarte").innerText = carte.description;
        document.getElementById("rareteCarte").innerText = carte.rarete;
        document.getElementById("niveauCarte").innerText = carte.niveau;
        document.getElementById("prixCarte").innerText = carte.prix + " $";
        document.getElementById("proprietaireCarte").innerText = carte.proprietaire;

        // Vérifier si l'utilisateur est le propriétaire
        const acheterButton = document.getElementById("acheterCarte");
        if (carte.proprietaire === user.surnom) {
            acheterButton.disabled = true;
        } else {
            acheterButton.disabled = false;
        }
    }

    // Achat de la carte sélectionnée
    document.getElementById("acheterCarte").addEventListener("click", function () {
        if (!selectedCardId || !utilisateurId) {
            alert("Veuillez sélectionner une carte et être connecté !");
            return;
        }

        fetch(`/market/achat?carteId=${selectedCardId}&acheteurId=${utilisateurId}`, { method: "POST" })
            .then(response => response.text())
            .then(message => {
                alert(message);
                return fetch("/api/auth/utilisateur", { cache: "no-store" }); // Récupérer les nouvelles infos utilisateur
            })
            .then(response => response.json())
            .then(user => {
                updateUserInfo(user); // Met à jour l'affichage utilisateur
                removePurchasedCard(selectedCardId); // Supprime la carte achetée de la liste
            })
            .catch(error => console.error("Erreur lors de l'achat :", error));
    });

    // Met à jour le solde et le nom utilisateur dans l'interface
    function updateUserInfo(user) {
        document.getElementById("username").innerText = user.surnom;
        document.getElementById("solde").innerText = user.solde + " $";
    }

    // Supprime la carte achetée de la liste affichée
    function removePurchasedCard(cardId) {
        const rows = document.querySelectorAll("#cartesTable tbody tr");
        rows.forEach(row => {
            if (row.classList.contains("selected")) {
                row.remove();
            }
        });
        selectedCardId = null;
        document.getElementById("acheterCarte").disabled = true; // Désactive le bouton acheter
    }

    // Bouton de retour à la page d'accueil
    document.getElementById("retourAccueil").addEventListener("click", function () {
        window.location.href = "home.html";
    });
});
