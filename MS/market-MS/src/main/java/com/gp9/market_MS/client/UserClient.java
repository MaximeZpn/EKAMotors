package com.gp9.market_MS.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
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
    
    /**
     * Modifies the user's balance
     * @param userId User ID
     * @param amount Amount to add (positive) or subtract (negative)
     * @return true if successful, false otherwise
     */
    public boolean modifierSolde(Long userId, double amount) {
        try {
            System.out.println("UserClient: Modifying balance for user " + userId + " by " + amount);
            String url = String.format("%s/api/utilisateurs/%d/solde?montant=%.2f", userServiceUrl, userId, amount);
            
            // Enhanced error handling
            try {
                ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    null,
                    Void.class
                );
                System.out.println("Balance modification response: " + response.getStatusCode());
                return response.getStatusCode().is2xxSuccessful();
            } catch (Exception e) {
                System.err.println("Error during balance update request: " + e.getMessage());
                // Try to parse response body if available
                if (e instanceof RestClientResponseException) {
                    RestClientResponseException responseException = (RestClientResponseException) e;
                    System.err.println("Response body: " + responseException.getResponseBodyAsString());
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error in UserClient.modifierSolde: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean ensureUserExists(Long userId) {
        try {
            System.out.println("Ensuring user exists: " + userId);
            
            // First check if user exists
            try {
                ResponseEntity<Object> checkResponse = restTemplate.getForEntity(
                    userServiceUrl + "/api/utilisateurs/" + userId, 
                    Object.class
                );
                
                if (checkResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.println("User " + userId + " already exists");
                    return true;
                }
            } catch (Exception e) {
                System.out.println("User does not exist, will attempt to create: " + e.getMessage());
            }
            
            // User doesn't exist, initialize with balance
            return modifierSolde(userId, 100.0); // Initialize with 100
        } catch (Exception e) {
            System.err.println("Error ensuring user exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
