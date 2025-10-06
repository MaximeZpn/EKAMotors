document.addEventListener("DOMContentLoaded", function () {
    let selectedCardId = null;
    let utilisateurId = null;

    // Récupérer l'utilisateur connecté
    fetch("/api/auth/utilisateur")
        .then(response => response.json())
        .then(user => {
            if (user.id) {
                utilisateurId = user.id;
                document.getElementById("username").innerText = user.surnom;
                document.getElementById("solde").innerText = user.solde + " $";
                chargerCartesUtilisateur(utilisateurId);
            } else {
                throw new Error("Utilisateur non connecté");
            }
        })
        .catch(error => {
            alert("Veuillez vous connecter !");
            window.location.href = "connexion.html";
        });

    // Charger les véhicules de l'utilisateur
    function chargerCartesUtilisateur(userId) {
        fetch(`/cartes/utilisateur/${userId}`)
            .then(response => response.json())
            .then(cartes => {
                const tableBody = document.querySelector("#cartesTable tbody");
                tableBody.innerHTML = ""; // Nettoyage avant ajout des véhicules

                cartes.forEach(carte => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${carte.nom}</td>
                        <td>${carte.rarete}</td>
                        <td>${carte.niveau}</td>
                        <td class="statut">${carte.aVendre ? "🟢 En vente" : "🔴 Disponible"}</td>
                    `;
                    if (carte.avendre) {
                        row.classList.add("disabled"); // Griser la ligne
                    } else {
                        row.addEventListener("click", () => selectCard(carte, row));
                    }

                    tableBody.appendChild(row);
                });
            })
            .catch(error => console.error("Erreur de chargement des véhicules :", error));
    }

    // Sélectionner un véhicule
    function selectCard(carte, row) {
        if (carte.aVendre) return; // Empêcher la sélection si le véhicule est déjà en vente

        selectedCardId = carte.id;

        // Mettre en surbrillance la ligne sélectionnée
        document.querySelectorAll("#cartesTable tr").forEach(tr => tr.classList.remove("selected"));
        row.classList.add("selected");

        // Afficher les détails du véhicule
        document.getElementById("imageCarte").src = carte.imageUrl || "/images/default-card.png";
        document.getElementById("nomCarte").innerText = carte.nom;
        document.getElementById("descriptionCarte").innerText = carte.description;
        document.getElementById("rareteCarte").innerText = carte.rarete;
        document.getElementById("niveauCarte").innerText = carte.niveau;

        // Activer le bouton "Vendre"
        document.getElementById("vendreCarte").disabled = false;
    }

    // Mettre un véhicule en vente
    document.getElementById("vendreCarte").addEventListener("click", function () {
        if (!selectedCardId || !utilisateurId) {
            alert("Veuillez sélectionner un véhicule !");
            return;
        }

        const prix = prompt("Entrez le prix de vente du véhicule :");
        if (prix === null || prix.trim() === "" || isNaN(prix) || parseFloat(prix) <= 0) {
            alert("Veuillez entrer un prix valide !");
            return;
        }

        // Créer l'offre dans le microservice market
        console.log("🚗 VENTE: Création d'une offre pour la carte", selectedCardId, "utilisateur", utilisateurId, "prix", prix);
        
        const offerData = {
            carteId: selectedCardId,
            vendeurId: utilisateurId,
            prix: parseFloat(prix),
            active: true
        };
        
        console.log("📤 VENTE: Données envoyées:", offerData);
        
        fetch('/api/market/offres', {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(offerData)
        })
        .then(response => {
            console.log("📥 VENTE: Réponse du serveur:", response.status, response.statusText);
            if (response.ok) {
                return response.text();
            } else {
                return response.text().then(errorText => {
                    throw new Error(`Erreur ${response.status}: ${errorText}`);
                });
            }
        })
        .then(message => {
            console.log("✅ VENTE: Succès -", message);
            alert("Véhicule mis en vente avec succès!\n\nVérifiez le catalogue pour voir votre véhicule.");
            chargerCartesUtilisateur(utilisateurId); // 🔄 Rafraîchir la liste après mise en vente

            // Ajouter la classe disabled pour griser le véhicule dans le tableau
            const row = document.querySelector(`#cartesTable tbody tr.selected`);
            if (row) {
                row.classList.add("disabled"); // Griser la ligne du véhicule en vente
            }

            document.getElementById("vendreCarte").disabled = true; // Désactiver le bouton après la vente
            
            // Rediriger vers le catalogue au lieu de "mes voitures"
            setTimeout(() => {
                window.location.href = "catalogue.html";
            }, 2000); // Attendre 2 secondes pour laisser le temps de voir le message
        })
        .catch(error => {
            console.error("❌ VENTE: Erreur lors de la mise en vente:", error);
            alert("Erreur lors de la mise en vente: " + error.message);
        });
    });

    // Bouton de retour à la page d'accueil
    document.getElementById("retourAccueil").addEventListener("click", function () {
        window.location.href = "home.html";
    });
});
