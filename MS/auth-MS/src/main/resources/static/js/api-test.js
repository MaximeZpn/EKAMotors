/**
 * API Testing Utility
 * 
 * This script helps diagnose issues with API endpoints
 * by making direct requests and showing the results.
 * 
 * Usage:
 * 1. Include this script in any HTML page
 * 2. Open browser console and use the ApiTester object
 * 
 * Example:
 *   ApiTester.testCreateCard({nom: "Test Card", prix: 10, proprietaireId: 4})
 *   ApiTester.testGetCards(4)
 */

const ApiTester = {
  // Store test results
  results: [],
  
  // Create a card directly
  async testCreateCard(cardData) {
    console.log("🔍 Testing card creation with data:", cardData);
    const defaultData = {
      nom: "Test Card",
      description: "Created by API Tester",
      rarete: "COMMUN",
      prix: 10,
      aVendre: false,
      proprietaireId: parseInt(localStorage.getItem('user_id')) || 1,
      energy: 100,
      type: "NORMAL"
    };
    
    const fullCardData = {...defaultData, ...cardData};
    
    try {
      console.log("Calling POST /api/cartes with data:", fullCardData);
      const response = await fetch('/api/cartes', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(fullCardData)
      });
      
      console.log("Response status:", response.status);
      
      if (response.ok) {
        const card = await response.json();
        console.log("✅ Card created successfully:", card);
        this.results.push({
          type: 'create',
          success: true,
          data: card,
          timestamp: new Date()
        });
        return card;
      } else {
        const error = await response.text();
        console.error("❌ Card creation failed:", error);
        this.results.push({
          type: 'create',
          success: false,
          error: error,
          timestamp: new Date()
        });
        return null;
      }
    } catch (error) {
      console.error("❌ Network error:", error);
      this.results.push({
        type: 'create',
        success: false,
        error: error.message,
        timestamp: new Date()
      });
      return null;
    }
  },
  
  // Get cards for a user
  async testGetCards(userId = null) {
    if (userId === null) {
      userId = localStorage.getItem('user_id') || 1;
    }
    
    console.log(`🔍 Testing get cards for user ID: ${userId}`);
    
    try {
      const url = `/api/cartes/utilisateur/${userId}`;
      console.log(`Calling GET ${url}`);
      
      const response = await fetch(url);
      console.log("Response status:", response.status);
      
      if (response.ok) {
        const cards = await response.json();
        console.log(`✅ Found ${cards.length} cards:`, cards);
        this.results.push({
          type: 'getCards',
          success: true,
          data: cards,
          timestamp: new Date()
        });
        return cards;
      } else {
        const error = await response.text();
        console.error("❌ Get cards failed:", error);
        this.results.push({
          type: 'getCards',
          success: false,
          error: error,
          timestamp: new Date()
        });
        return null;
      }
    } catch (error) {
      console.error("❌ Network error:", error);
      this.results.push({
        type: 'getCards',
        success: false,
        error: error.message,
        timestamp: new Date()
      });
      return null;
    }
  },
  
  // Get specific card
  async testGetCard(cardId) {
    console.log(`🔍 Testing get card ID: ${cardId}`);
    
    try {
      const url = `/api/cartes/${cardId}`;
      console.log(`Calling GET ${url}`);
      
      const response = await fetch(url);
      console.log("Response status:", response.status);
      
      if (response.ok) {
        const card = await response.json();
        console.log("✅ Card found:", card);
        this.results.push({
          type: 'getCard',
          success: true,
          data: card,
          timestamp: new Date()
        });
        return card;
      } else {
        const error = await response.text();
        console.error("❌ Get card failed:", error);
        this.results.push({
          type: 'getCard',
          success: false,
          error: error,
          timestamp: new Date()
        });
        return null;
      }
    } catch (error) {
      console.error("❌ Network error:", error);
      this.results.push({
        type: 'getCard',
        success: false,
        error: error.message,
        timestamp: new Date()
      });
      return null;
    }
  },
  
  // Run a complete test sequence
  async runFullTest(userId = null) {
    if (userId === null) {
      userId = localStorage.getItem('user_id') || 1;
    }
    
    console.log(`🧪 Starting full API test sequence for user ${userId}`);
    
    // Step 1: Get current cards
    console.log("Step 1: Get current cards");
    const initialCards = await this.testGetCards(userId);
    const initialCount = initialCards ? initialCards.length : 0;
    
    // Step 2: Create a test card
    console.log("Step 2: Create a test card");
    const testCardName = "Test Card " + Date.now();
    const createdCard = await this.testCreateCard({
      nom: testCardName,
      description: "Created by API tester at " + new Date().toLocaleString(),
      proprietaireId: parseInt(userId)
    });
    
    if (!createdCard) {
      console.error("⛔ Cannot continue test: Card creation failed");
      return;
    }
    
    // Step 3: Verify the card exists
    console.log("Step 3: Verify the card exists");
    const verifiedCard = await this.testGetCard(createdCard.id);
    
    // Step 4: Get updated cards list
    console.log("Step 4: Get updated cards list");
    const updatedCards = await this.testGetCards(userId);
    const updatedCount = updatedCards ? updatedCards.length : 0;
    
    // Report results
    console.log("📊 Test Results:");
    console.log(`- Initial card count: ${initialCount}`);
    console.log(`- Updated card count: ${updatedCount}`);
    console.log(`- Difference: ${updatedCount - initialCount}`);
    
    if (updatedCount > initialCount) {
      console.log("✅ SUCCESS: Card was added successfully!");
    } else {
      console.log("❌ FAILURE: Card count didn't increase after creation");
    }
    
    if (verifiedCard) {
      console.log("✅ SUCCESS: Created card can be retrieved directly");
    } else {
      console.log("❌ FAILURE: Created card cannot be retrieved directly");
    }
    
    // Check if card appears in the list
    const foundInList = updatedCards && updatedCards.some(card => card.id === createdCard.id);
    if (foundInList) {
      console.log("✅ SUCCESS: Created card appears in the user's cards list");
    } else {
      console.log("❌ FAILURE: Created card is missing from user's cards list");
    }
    
    return {
      initialCount,
      updatedCount,
      createdCard,
      verifiedCard,
      foundInList,
      success: foundInList && verifiedCard && updatedCount > initialCount
    };
  },
  
  // Show all results
  showResults() {
    console.log("📝 Test History:");
    this.results.forEach((result, i) => {
      console.log(
        `${i+1}. [${result.timestamp.toLocaleTimeString()}] ` +
        `${result.type} - ${result.success ? '✅ Success' : '❌ Failed'}`
      );
    });
  }
};

console.log("🛠️ API Tester loaded. Type ApiTester.runFullTest() in console to run tests.");
