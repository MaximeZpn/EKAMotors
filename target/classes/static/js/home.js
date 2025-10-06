document.addEventListener("DOMContentLoaded", function() {
    const token = sessionStorage.getItem("jwtToken");

    if (!token) {
        alert("Vous devez être connecté !");
        window.location.href = "connexion.html";
        return;
    }

    fetch("/api/auth/profil", {
        method: "GET",
        headers: { "Authorization": "Bearer " + token }
    })
    .then(response => {
        if (response.status === 401) {
            throw new Error("Session expirée. Veuillez vous reconnecter.");
        }
        return response.json();
    })
    .then(utilisateur => {
        document.getElementById("username").innerText = utilisateur.surnom;
        document.getElementById("solde").innerText = utilisateur.solde + " $";
    })
    .catch(error => {
        alert(error.message);
        sessionStorage.removeItem("jwtToken"); // 🔹 Supprime le token si invalide
        window.location.href = "connexion.html";
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

document.getElementById("btnCompte").addEventListener("click", function() {
    // Rediriger vers la page intermédiaire au lieu de compte.html directement
    window.location.href = "admin.html";
});
