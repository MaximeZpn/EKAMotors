package com.gp9.game_MS.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game/debug")
public class DiagnosticController {
    
    private static final Logger logger = LoggerFactory.getLogger(DiagnosticController.class);
    private final RestTemplate restTemplate;
    
    @Autowired
    public DiagnosticController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "game-service");
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/check-services")
    public ResponseEntity<Map<String, Object>> checkServices() {
        Map<String, Object> status = new HashMap<>();
        
        // Check carte-service
        try {
            ResponseEntity<String> carteResponse = restTemplate.getForEntity(
                "http://carte-service:8085/api/debug/ping", String.class);
            status.put("carte-service", Map.of(
                "status", carteResponse.getStatusCode().is2xxSuccessful() ? "UP" : "ERROR",
                "statusCode", carteResponse.getStatusCodeValue()
            ));
        } catch (Exception e) {
            status.put("carte-service", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
            logger.error("Error connecting to carte-service: {}", e.getMessage());
        }
        
        // Check user-service
        try {
            ResponseEntity<String> userResponse = restTemplate.getForEntity(
                "http://user-service:8082/api/utilisateurs/health", String.class);
            status.put("user-service", Map.of(
                "status", userResponse.getStatusCode().is2xxSuccessful() ? "UP" : "ERROR",
                "statusCode", userResponse.getStatusCodeValue()
            ));
        } catch (Exception e) {
            status.put("user-service", Map.of(
                "status", "DOWN",
                "error", e.getMessage()
            ));
            logger.error("Error connecting to user-service: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/card/{cardId}/check")
    public ResponseEntity<Map<String, Object>> checkCard(@PathVariable Long cardId) {
        Map<String, Object> result = new HashMap<>();
        result.put("cardId", cardId);
        
        try {
            String cardUrl = "http://carte-service:8085/api/cartes/" + cardId;
            ResponseEntity<Map> cardResponse = restTemplate.getForEntity(cardUrl, Map.class);
            
            result.put("status", cardResponse.getStatusCode().value());
            result.put("exists", cardResponse.getStatusCode().is2xxSuccessful());
            
            if (cardResponse.getBody() != null) {
                result.put("cardData", cardResponse.getBody());
                
                // Check energy
                Object energyObj = cardResponse.getBody().get("energy");
                int energy = 0;
                if (energyObj instanceof Integer) {
                    energy = (Integer) energyObj;
                } else if (energyObj instanceof Double) {
                    energy = ((Double) energyObj).intValue();
                }
                result.put("hasEnoughEnergy", energy >= 20);
                result.put("energy", energy);
            }
            
        } catch (Exception e) {
            result.put("error", e.getMessage());
            result.put("exists", false);
        }
        
        return ResponseEntity.ok(result);
    }
}
