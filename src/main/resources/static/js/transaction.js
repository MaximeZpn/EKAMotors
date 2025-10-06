document.addEventListener("DOMContentLoaded", function() {
    const utilisateurId = 1; // Remplace ceci par l'ID de l'utilisateur connecté

    fetch(`/api/utilisateurs/${utilisateurId}/transactions`)
        .then(response => response.json())
        .then(transactions => {
            afficherTransactions(transactions);
        })
        .catch(error => {
            console.error("Erreur lors de la récupération des transactions :", error);
        });
});

function afficherTransactions(transactions) {
    const container = document.getElementById("transactionsContainer");
    container.innerHTML = '';

    if (transactions.length === 0) {
        container.innerHTML = "<p>Aucune transaction trouvée.</p>";
        return;
    }

    transactions.forEach(transaction => {
        const transactionElement = document.createElement("div");
        transactionElement.classList.add("transaction");

        transactionElement.innerHTML = `
            <h3>Transaction : ${transaction.carte}</h3>
            <p><strong>Acheteur :</strong> ${transaction.acheteur}</p>
            <p><strong>Vendeur :</strong> ${transaction.vendeur}</p>
            <p><strong>Prix :</strong> ${transaction.prix} $</p>
            <p><strong>Date :</strong> ${new Date(transaction.dateTransaction).toLocaleString()}</p>
        `;

        container.appendChild(transactionElement);
    });
}
