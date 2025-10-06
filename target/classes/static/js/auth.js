document.addEventListener("DOMContentLoaded", function () {
    const formInscription = document.getElementById("formInscription");
    const formConnexion = document.getElementById("formConnexion");

    // 🔹 Gestion de l'inscription
    if (formInscription) {
        formInscription.addEventListener("submit", function (event) {
            event.preventDefault();
            const surnom = document.getElementById("surnom").value;
            const email = document.getElementById("email").value;
            const motDePasse = document.getElementById("motDePasse").value;

            fetch("/api/auth/inscription", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ surnom, email, motDePasse })
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                window.location.href = "/connexion.html"; // Redirection après inscription
            })
            .catch(error => console.error("Erreur :", error));
        });
    }

    // 🔹 Gestion de la connexion
    if (formConnexion) {
            formConnexion.addEventListener("submit", function (event) {
                event.preventDefault();
                const email = document.getElementById("emailConnexion").value;
                const motDePasse = document.getElementById("motDePasseConnexion").value;

                fetch("/api/auth/connexion", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, motDePasse })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.token) {
                        sessionStorage.setItem("jwtToken", data.token); // 🔹 Stocke le token
                        alert(data.token)
                        alert("Connexion réussie !");
                        window.location.href = "/home.html";
                    } else {
                        alert("Identifiants incorrects !");
                    }
                })
                .catch(error => console.error("Erreur :", error));
            });
    }
});
