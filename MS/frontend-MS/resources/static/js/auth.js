document.addEventListener('DOMContentLoaded', function() {
  // Handle Registration Form
  const registrationForm = document.getElementById('formInscription');
  if (registrationForm) {
    registrationForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const username = document.getElementById('username').value;
      const email = document.getElementById('email').value;
      const password = document.getElementById('password').value;
      
      try {
        const response = await fetch('/api/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: username,
            password: password,
            email: email
          })
        });
        
        if (response.ok) {
          // Redirect to login page on successful registration
          window.location.href = '/connexion.html?registered=true';
        } else {
          const error = await response.text();
          showError(error || 'Erreur lors de l\'inscription');
        }
      } catch (error) {
        showError('Erreur de connexion au serveur');
      }
    });
  }
  
  // Handle Login Form
  const loginForm = document.getElementById('formConnexion');
  if (loginForm) {
    // Show success message if coming from registration
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('registered') === 'true') {
      showSuccess('Inscription réussie! Vous pouvez maintenant vous connecter.');
    }
    
    loginForm.addEventListener('submit', async function(e) {
      e.preventDefault();
      
      const username = document.getElementById('username').value;
      const password = document.getElementById('password').value;
      
      try {
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: username,
            password: password
          })
        });
        
        if (response.ok) {
          const data = await response.json();
          // Store token in localStorage
          localStorage.setItem('auth_token', data.token);
          localStorage.setItem('user_id', data.userId);
          localStorage.setItem('username', data.username);
          
          // Redirect to main page
          window.location.href = '/index.html';
        } else {
          showError('Nom d\'utilisateur ou mot de passe incorrect');
        }
      } catch (error) {
        showError('Erreur de connexion au serveur');
      }
    });
  }
  
  // Helper functions
  function showError(message) {
    // Remove existing error message if any
    const existingError = document.querySelector('.error-message');
    if (existingError) {
      existingError.remove();
    }
    
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    
    const container = document.querySelector('.container');
    container.appendChild(errorElement);
  }
  
  function showSuccess(message) {
    const successElement = document.createElement('div');
    successElement.className = 'success-message';
    successElement.textContent = message;
    
    const container = document.querySelector('.container');
    container.insertBefore(successElement, container.firstChild);
  }
});
