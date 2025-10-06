// Check if user is authenticated
(function() {
  const token = localStorage.getItem('auth_token');
  const username = localStorage.getItem('username');
  const currentPage = window.location.pathname;
  
  // Pages that don't require authentication
  const publicPages = ['/index.html', '/connexion.html', '/inscription.html', '/', '/catalogue.html'];
  
  // Only redirect to login if not authenticated AND trying to access a protected page
  if (!token && !publicPages.includes(currentPage)) {
    console.log("Not authenticated, redirecting to login");
    window.location.href = '/connexion.html';
    return;
  }
  
  // If authenticated, update username in the header if element exists
  if (token && username) {
    const usernameDisplay = document.getElementById('username-display');
    if (usernameDisplay) {
      usernameDisplay.textContent = username;
    }
  }
  
  // Setup logout button if it exists
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function() {
      localStorage.removeItem('auth_token');
      localStorage.removeItem('user_id');
      localStorage.removeItem('username');
      window.location.href = '/index.html';
    });
  }
})();
