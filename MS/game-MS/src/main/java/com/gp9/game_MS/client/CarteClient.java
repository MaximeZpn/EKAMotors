package com.gp9.game_MS.client;

import com.gp9.game_MS.dto.CardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class CarteClient {

    private static final Logger logger = LoggerFactory.getLogger(CarteClient.class);
    
    private final RestTemplate restTemplate;
    private final String carteServiceUrl;
    
    @Autowired
    public CarteClient(RestTemplate restTemplate, 
                      @Value("${services.carte-service.url:http://carte-service:8085}") String carteServiceUrl) {
        this.restTemplate = restTemplate;
        this.carteServiceUrl = carteServiceUrl;
    }
    
    public CardDTO getCardById(Long cardId) {
        try {
            String url = String.format("%s/api/cartes/%d", carteServiceUrl, cardId);
            ResponseEntity<CardDTO> response = restTemplate.getForEntity(url, CardDTO.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error fetching card: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateCardEnergy(Long cardId, int energyChange) {
        try {
            Map<String, Integer> requestBody = new HashMap<>();
            requestBody.put("amount", energyChange);

            ResponseEntity<Void> response = restTemplate.exchange(
                carteServiceUrl + "/api/cartes/" + cardId + "/energy",
                HttpMethod.PUT,
                new HttpEntity<>(requestBody),
                Void.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Failed to update card energy: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean regenerateAllCardsEnergy(int amount) {
        try {
            String url = String.format("%s/api/cartes/regenerate-energy?amount=%d", carteServiceUrl, amount);
            ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error regenerating cards energy: " + e.getMessage());
            return false;
        }
    }
}
