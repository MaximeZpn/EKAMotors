document.addEventListener('DOMContentLoaded', function() {
  const userId = localStorage.getItem('user_id');
  const token = localStorage.getItem('auth_token');
  const cardsContainer = document.getElementById('cards-container');
  
  if (!userId || !token) {
    window.location.href = '/connexion.html';
    return;
  }
  
  // Function to load user's cards
  async function loadUserCards() {
    try {
      const response = await fetch(`/api/cartes/utilisateur/${userId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (response.ok) {
        const cards = await response.json();
        displayCards(cards);
      } else {
        showError('Erreur lors du chargement des cartes');
      }
    } catch (error) {
      showError('Erreur de connexion au serveur');
    } finally {
      // Remove loading indicator
      const loadingEl = cardsContainer.querySelector('.loading');
      if (loadingEl) {
        loadingEl.remove();
      }
    }
  }
  
  // Function to display cards
  function displayCards(cards) {
    if (cards.length === 0) {
      cardsContainer.innerHTML = '<div class="no-cards">Vous n\'avez pas encore de cartes</div>';
      return;
    }
    
    cardsContainer.innerHTML = '';
    cards.forEach(card => {
      const cardElement = document.createElement('div');
      cardElement.className = 'card';
      cardElement.innerHTML = `
        <div class="card-title">${card.nom}</div>
        <div class="card-price">${card.prix} €</div>
        <div class="card-status ${card.aVendre ? 'for-sale' : 'not-for-sale'}">
          ${card.aVendre ? 'À vendre' : 'Non disponible à la vente'}
        </div>
        <div class="card-actions">
          ${!card.aVendre ? 
            `<button class="btn btn-primary" data-id="${card.id}" data-action="sell">Mettre en vente</button>` : 
            `<button class="btn btn-secondary" data-id="${card.id}" data-action="cancel">Annuler la vente</button>`
          }
        </div>
      `;
      cardsContainer.appendChild(cardElement);
    });
    
    // Add event listeners to buttons
    document.querySelectorAll('.card-actions button').forEach(button => {
      button.addEventListener('click', handleCardAction);
    });
  }
  
  // Handle card actions (sell/cancel)
  async function handleCardAction(e) {
    const cardId = e.target.getAttribute('data-id');
    const action = e.target.getAttribute('data-action');
    
    try {
      if (action === 'sell') {
        // Implementation would depend on your API
        // This is a placeholder for the sell action
        const response = await fetch(`/api/cartes/${cardId}/vendre`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ aVendre: true })
        });
        
        if (response.ok) {
          // Reload cards after action
          loadUserCards();
        } else {
          showError('Erreur lors de la mise en vente');
        }
      } else if (action === 'cancel') {
        // Placeholder for cancel action
        const response = await fetch(`/api/cartes/${cardId}/vendre`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ aVendre: false })
        });
        
        if (response.ok) {
          // Reload cards after action
          loadUserCards();
        } else {
          showError('Erreur lors de l\'annulation de la vente');
        }
      }
    } catch (error) {
      showError('Erreur de connexion au serveur');
    }
  }
  
  // Helper function to show error
  function showError(message) {
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    
    // Remove any existing error
    const existingError = document.querySelector('.error-message');
    if (existingError) {
      existingError.remove();
    }
    
    // Add to beginning of main
    const mainElement = document.querySelector('main');
    mainElement.prepend(errorElement);
    
    // Remove after 5 seconds
    setTimeout(() => {
      errorElement.remove();
    }, 5000);
  }
  
  // Load cards when page loads
  loadUserCards();
});
