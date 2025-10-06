package com.gp9.game_MS.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {
    
    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    
    @Autowired
    public UserClient(RestTemplate restTemplate, 
                     @Value("${services.user-service.url:http://user-service:8082}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }
    
    public boolean checkBalance(Long userId, double requiredAmount) {
        try {
            String url = String.format("%s/api/utilisateurs/%d", userServiceUrl, userId);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object soldeObj = response.getBody().get("solde");
                if (soldeObj != null) {
                    double solde = 0;
                    if (soldeObj instanceof Number) {
                        solde = ((Number) soldeObj).doubleValue();
                    } else {
                        solde = Double.parseDouble(soldeObj.toString());
                    }
                    return solde >= requiredAmount;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking user balance: " + e.getMessage());
            return false;
        }
    }
    
    public boolean addBalance(Long userId, double amount) {
        try {
            String url = String.format("%s/api/utilisateurs/%d/solde?montant=%.2f", userServiceUrl, userId, amount);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error updating user balance: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deductBalance(Long userId, double amount) {
        try {
            String url = String.format("%s/api/utilisateurs/%d/solde?montant=%.2f", userServiceUrl, userId, -amount);
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error deducting user balance: " + e.getMessage());
            return false;
        }
    }
}
