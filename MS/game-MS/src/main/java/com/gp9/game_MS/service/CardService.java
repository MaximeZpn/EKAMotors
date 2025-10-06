package com.gp9.game_MS.service;

import com.gp9.game_MS.dto.CardDTO;
import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.repository.RoomRepository;
import com.gp9.game_MS.client.CarteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private static final int MIN_ENERGY_REQUIRED = 20;
    
    private final RestTemplate restTemplate;
    private final RoomRepository roomRepository;
    private final CarteClient carteClient;
    private final Map<String, CardDTO> battleCardRegistry = new ConcurrentHashMap<>();

    @Autowired
    public CardService(RestTemplate restTemplate, RoomRepository roomRepository, CarteClient carteClient) {
        this.restTemplate = restTemplate;
        this.roomRepository = roomRepository;
        this.carteClient = carteClient;
    }
    
    /**
     * Checks if a card is playable for a battle
     */
    public boolean isCardPlayable(Long cardId, Long userId) {
        logger.info("Checking if card {} is playable for user {}", cardId, userId);
        
        try {
            // Use CarteClient instead of direct RestTemplate call
            CardDTO card = carteClient.getCardById(cardId);
            
            if (card == null) {
                logger.error("Failed to get card for ID: {}", cardId);
                return false;
            }
            
            // Check if card belongs to user
            if (!userId.equals(card.getProprietaireId())) {
                logger.error("Card {} belongs to user {}, not to requesting user {}", 
                    cardId, card.getProprietaireId(), userId);
                return false;
            }
            
            // Check card energy
            if (card.getEnergy() < MIN_ENERGY_REQUIRED) {
                logger.error("Card {} has insufficient energy: {}/{}", 
                    cardId, card.getEnergy(), MIN_ENERGY_REQUIRED);
                return false;
            }
            
            logger.info("Card {} is playable for user {}", cardId, userId);
            return true;
            
        } catch (Exception e) {
            logger.error("Unexpected error checking if card is playable: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Register a card for battle
     */
    public void registerCardForBattle(Long roomId, Long cardId, int playerPosition) {
        logger.info("Registering card {} for room {} as player {}", cardId, roomId, playerPosition);
        
        try {
            // Store card ID in the registry with a unique key
            String registryKey = roomId + "-player" + playerPosition;
            
            // Create a minimal card DTO to store in the registry
            CardDTO cardDTO = new CardDTO();
            cardDTO.setId(cardId);
            cardDTO.setPlayerPosition(playerPosition);
            
            // Store in registry
            battleCardRegistry.put(registryKey, cardDTO);
            
            // Update room entity with card ID
            Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
                
            if (playerPosition == 1) {
                room.setCard1Id(cardId);
                logger.info("Set card1Id={} for room {}", cardId, roomId);
            } else if (playerPosition == 2) {
                room.setCard2Id(cardId);
                logger.info("Set card2Id={} for room {}", cardId, roomId);
            }
            
            roomRepository.save(room);
        } catch (Exception e) {
            logger.error("Error registering card for battle: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get card data from carte-service by ID
     */
    public CardDTO getCardDTOForBattle(Long roomId, int playerPosition) {
        logger.info("Getting card for room {}, player {}", roomId, playerPosition);
        
        try {
            // First, try to get the card ID from the room entity
            Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
            
            Long cardId = null;
            if (playerPosition == 1) {
                cardId = room.getCard1Id();
                logger.info("Retrieved card1Id={} from room {}", cardId, roomId);
            } else if (playerPosition == 2) {
                cardId = room.getCard2Id();
                logger.info("Retrieved card2Id={} from room {}", cardId, roomId);
            }
            
            if (cardId == null) {
                throw new IllegalStateException("No card ID found for room " + roomId + ", player " + playerPosition);
            }
            
            // Now get the card data from carte-service
            return getCardDTOById(cardId, playerPosition);
        } catch (Exception e) {
            logger.error("Error getting card for battle: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get card: " + e.getMessage());
        }
    }
    
    /**
     * Reduce card energy after battle
     */
    public void reduceCardEnergy(Long cardId, int amount) {
        try {
            String energyUrl = "http://carte-service:8085/api/cartes/" + cardId + "/energy";
            Map<String, Integer> requestBody = Collections.singletonMap("amount", -Math.abs(amount));
            
            logger.info("Reducing energy for card {} by {} points", cardId, amount);
            restTemplate.postForEntity(energyUrl, requestBody, Void.class);
            logger.info("Successfully reduced energy for card {}", cardId);
        } catch (Exception e) {
            logger.error("Failed to reduce energy for card {}: {}", cardId, e.getMessage());
        }
    }
    
    /**
     * Update card energy using CarteClient
     */
    public boolean updateCardEnergy(Long cardId, int amount) {
        return carteClient.updateCardEnergy(cardId, amount);
    }
    
    /**
     * Get card data for battle
     */
    public Map<String, Object> getCardForBattle(Long cardId, int playerPosition) {
        try {
            String cardUrl = "http://carte-service:8085/api/cartes/" + cardId;
            ResponseEntity<Map> response = restTemplate.getForEntity(cardUrl, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> cardData = response.getBody();
                cardData.put("playerPosition", playerPosition);
                return cardData;
            }
        } catch (Exception e) {
            logger.error("Error getting card for battle: {}", e.getMessage());
        }
        
        // Return fallback data if we can't get the real card
        return Map.of(
            "id", cardId,
            "nom", "Unknown Card",
            "description", "Card data unavailable",
            "type", "NORMAL",
            "energy", 20,
            "playerPosition", playerPosition
        );
    }
    
    /**
     * Regenerates energy for all cards in the system
     */
    public int regenerateCardsEnergy() {
        logger.info("Starting energy regeneration for all cards");
        try {
            // Call the carte-service to regenerate energy for all cards
            String regenerateUrl = "http://carte-service:8085/api/cartes/regenerate-energy";
            ResponseEntity<Map> response = restTemplate.postForEntity(
                regenerateUrl, 
                Collections.singletonMap("amount", 5), 
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                int cardsUpdated = result.containsKey("cardsUpdated") ? 
                    convertToInt(result.get("cardsUpdated")) : 0;
                    
                logger.info("Successfully regenerated energy for {} cards", cardsUpdated);
                return cardsUpdated;
            } else {
                logger.warn("Energy regeneration request failed with status: {}", 
                    response.getStatusCodeValue());
                return 0;
            }
        } catch (Exception e) {
            logger.error("Failed to regenerate cards energy: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    /**
     * Get card data as CardDTO by ID
     */
    private CardDTO getCardDTOById(Long cardId, int playerPosition) {
        try {
            Map<String, Object> cardData = getCardForBattle(cardId, playerPosition);
            
            CardDTO cardDTO = new CardDTO();
            cardDTO.setId(cardId);
            cardDTO.setNom((String) cardData.get("nom"));
            cardDTO.setDescription((String) cardData.getOrDefault("description", ""));
            cardDTO.setType((String) cardData.getOrDefault("type", "NORMAL"));
            cardDTO.setRarete((String) cardData.getOrDefault("rarete", "COMMUN"));
            cardDTO.setEnergy(convertToInt(cardData.getOrDefault("energy", 20)));
            cardDTO.setPlayerPosition(playerPosition);
            
            // Add proprietaireId
            if (cardData.containsKey("proprietaireId")) {
                cardDTO.setProprietaireId(convertToLong(cardData.get("proprietaireId")));
            }
            
            // Set HP based on rarity and energy
            cardDTO.setHp(calculateCardHP(cardDTO.getRarete(), cardDTO.getEnergy()));
            
            // Set power based on rarity, energy and type
            cardDTO.setPower(calculateCardPower(cardDTO));
            
            logger.info("Card {} prepared for battle: {} - HP={}, Power={}, Type={}",
                cardDTO.getId(), cardDTO.getNom(), cardDTO.getHp(), cardDTO.getPower(), cardDTO.getType());
            
            return cardDTO;
        } catch (Exception e) {
            logger.error("Error converting card data to DTO: {}", e.getMessage(), e);
            
            // Return fallback DTO
            CardDTO fallback = new CardDTO();
            fallback.setId(cardId);
            fallback.setNom("Unknown Card");
            fallback.setType("NORMAL");
            fallback.setEnergy(20);
            fallback.setPlayerPosition(playerPosition);
            fallback.setHp(100);
            fallback.setPower(20);
            fallback.setProprietaireId(1L); // Default owner ID
            return fallback;
        }
    }
    
    /**
     * Calculate card HP based on rarity and energy
     */
    private int calculateCardHP(String rarity, int energy) {
        int baseHP = 100;
        
        // Adjust by rarity
        if (rarity != null) {
            switch (rarity.toUpperCase()) {
                case "RARE":
                    baseHP += 20;
                    break;
                case "EPIQUE":
                    baseHP += 40;
                    break;
                case "LEGENDAIRE":
                    baseHP += 60;
                    break;
            }
        }
        
        // Apply energy factor (higher energy = stronger)
        double energyFactor = energy / 100.0;
        return (int)(baseHP * (0.8 + energyFactor * 0.4));
    }
    
    /**
     * Calculate card power based on card attributes
     */
    private int calculateCardPower(CardDTO card) {
        int basePower = 20;
        
        // Adjust by rarity
        if (card.getRarete() != null) {
            switch (card.getRarete().toUpperCase()) {
                case "RARE":
                    basePower += 10;
                    break;
                case "EPIQUE":
                    basePower += 25;
                    break;
                case "LEGENDAIRE":
                    basePower += 40;
                    break;
            }
        }
        
        // Bonus based on energy level
        double energyFactor = card.getEnergy() / 100.0;
        basePower += (int)(basePower * energyFactor * 0.3);
        
        // Type specific bonuses
        if (card.getType() != null) {
            switch (card.getType().toUpperCase()) {
                case "FIRE":
                    basePower += 5;  // Fire cards get a small attack bonus
                    break;
                case "WATER":
                    basePower += 3;  // Water cards get a smaller attack bonus
                    break;
                case "EARTH":
                    basePower += 8;  // Earth cards get a larger attack bonus
                    break;
                case "AIR":
                    basePower += 4;  // Air cards get a medium attack bonus
                    break;
            }
        }
        
        return basePower;
    }
    
    /**
     * Helper method to convert various number types to Long
     */
    private Long convertToLong(Object obj) {
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        } else if (obj instanceof Long) {
            return (Long) obj;
        } else if (obj instanceof String) {
            return Long.parseLong((String) obj);
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        throw new IllegalArgumentException("Cannot convert to Long: " + obj);
    }
    
    /**
     * Helper method to convert various number types to int
     */
    private int convertToInt(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        throw new IllegalArgumentException("Cannot convert to int: " + obj);
    }
}
