#!/bin/bash
# Test script for Carte API

BASE_URL="http://localhost:8085/api/cartes"

# Color for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "=== Testing Carte API ==="

# 1. Get all cards (should be empty initially)
echo -e "\n${GREEN}1. Getting all cards:${NC}"
curl -s $BASE_URL/

# 2. Add new cards
echo -e "\n\n${GREEN}2. Creating new cards:${NC}"
CARD1=$(curl -s -X POST $BASE_URL/ \
  -H "Content-Type: application/json" \
  -d '{"nom":"Dragon Rouge", "prix":15.99, "aVendre":true, "proprietaireId":1}')
echo "Created card 1: $CARD1"

CARD2=$(curl -s -X POST $BASE_URL/ \
  -H "Content-Type: application/json" \
  -d '{"nom":"Magicien Noir", "prix":25.50, "aVendre":false, "proprietaireId":2}')
echo "Created card 2: $CARD2"

# Extract IDs for later use
CARD1_ID=$(echo $CARD1 | sed -n 's/.*"id":\([0-9]*\).*/\1/p')
CARD2_ID=$(echo $CARD2 | sed -n 's/.*"id":\([0-9]*\).*/\1/p')

# 3. Get all cards again (should have 2 cards)
echo -e "\n${GREEN}3. Getting all cards (should have 2 cards now):${NC}"
curl -s $BASE_URL/

# 4. Get card by ID
echo -e "\n\n${GREEN}4. Getting card by ID $CARD1_ID:${NC}"
curl -s $BASE_URL/$CARD1_ID

# 5. Get cards by proprietaire ID
echo -e "\n\n${GREEN}5. Getting cards for user ID 1:${NC}"
curl -s $BASE_URL/utilisateur/1

# 6. Update card
echo -e "\n\n${GREEN}6. Updating card $CARD1_ID:${NC}"
UPDATED_CARD=$(curl -s -X PUT $BASE_URL/$CARD1_ID \
  -H "Content-Type: application/json" \
  -d '{"nom":"Dragon Rouge Ultra", "prix":29.99, "aVendre":true, "proprietaireId":1}')
echo "Updated card: $UPDATED_CARD"

# 7. Delete card
echo -e "\n\n${GREEN}7. Deleting card $CARD2_ID:${NC}"
curl -s -X DELETE $BASE_URL/$CARD2_ID

# 8. Verify deletion
echo -e "\n\n${GREEN}8. Getting all cards (should have 1 card now):${NC}"
curl -s $BASE_URL/

echo -e "\n\n${GREEN}=== Test completed ====${NC}"
