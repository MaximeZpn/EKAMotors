document.addEventListener("DOMContentLoaded", function() {
    const token = sessionStorage.getItem("jwtToken");

    if (!token) {
        alert("Vous devez être connecté !");
        window.location.href = "connexion.html";
        return;
    }

    // Vérifier d'abord si l'utilisateur a le rôle ADMIN
    fetch("/api/auth/check-role", {
        method: "GET",
        headers: { 
            "Authorization": "Bearer " + token,
            "Accept": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) {
            if (response.status === 403) {
                throw new Error("Accès non autorisé. Vous n'êtes pas administrateur.");
            }
            throw new Error("Erreur lors de la vérification des droits");
        }
        return response.json();
    })
    .then(roleInfo => {
        if (!roleInfo.hasAdminRole) {
            throw new Error("Vous n'avez pas les droits d'administrateur nécessaires.");
        }
        
        // Si l'utilisateur est admin, continuer avec le chargement des infos
        return fetch("/api/auth/profil", {
            method: "GET",
            headers: { 
                "Authorization": "Bearer " + token,
                "Accept": "application/json"
            }
        });
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Erreur " + response.status);
        }
        return response.json();
    })
    .then(utilisateur => {
        document.getElementById("surnom").innerText = utilisateur.surnom;
        document.getElementById("email").innerText = utilisateur.email;
        document.getElementById("solde").innerText = utilisateur.solde + " $";
    })
    .catch(error => {
        alert(error.message);
        window.location.href = "home.html";
    });

    // Modification du mot de passe
    document.getElementById("formModifierMdp").addEventListener("submit", function(event) {
        event.preventDefault();
        const nouveauMotDePasse = document.getElementById("nouveauMotDePasse").value;

        fetch("/api/auth/modifier-mdp", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token,
                "Accept": "application/json"  // Explicitly request JSON
            },
            body: JSON.stringify({ motDePasse: nouveauMotDePasse })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la modification du mot de passe");
            }
            return response.json();
        })
        .then(data => {
            alert(data.message);
            window.location.href = "connexion.html";
        })
        .catch(error => {
            alert("Erreur: " + error.message);
        });
    });
});

function deconnecter() {
    const token = sessionStorage.getItem("jwtToken");

    fetch("/api/auth/deconnexion", {
        method: "POST",
        headers: { "Authorization": "Bearer " + token }
    })
    .then(() => {
        sessionStorage.removeItem("jwtToken");
        window.location.href = "connexion.html";
    });
}
