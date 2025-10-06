package com.gp9.auth_MS.controller;

import java.security.SecureRandom;
import java.util.*;

import com.gp9.auth_MS.model.AuthRequest;
import com.gp9.auth_MS.model.AuthResponse;
import com.gp9.auth_MS.model.UserCredential;
import com.gp9.auth_MS.service.AuthService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate;
    private final SecureRandom secureRandom;

    public AuthController(AuthService authService, RestTemplate restTemplate) {
        this.authService = authService;
        this.restTemplate = restTemplate;
        // Initialize SecureRandom once
        SecureRandom tempRandom;
        try {
            tempRandom = SecureRandom.getInstanceStrong();
        } catch (Exception e) {
            tempRandom = new SecureRandom();
        }
        this.secureRandom = tempRandom;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/connexion.html";
    }
    
    @GetMapping("/inscription.html")
    public String inscriptionPage() {
        return "inscription";
    }
    
    @GetMapping("/connexion.html")
    public String connexionPage() {
        return "connexion";
    }
    
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.authenticate(authRequest);
            
            // Set cookie for frontend
            Cookie tokenCookie = new Cookie("auth_token", authResponse.getToken());
            tokenCookie.setPath("/");
            tokenCookie.setHttpOnly(true);
            tokenCookie.setMaxAge(86400); // 1 day
            response.addCookie(tokenCookie);
            
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        try {
            // Enhanced debug logging
            System.out.println("========= REGISTRATION REQUEST =========");
            System.out.println("Received authentication request: " + authRequest);
            System.out.println("Username: " + authRequest.getUsername());
            System.out.println("Password length: " + (authRequest.getPassword() != null ? authRequest.getPassword().length() : 0));
            System.out.println("Email: " + authRequest.getEmail());
            System.out.println("======================================");
            
            // Validate the request data
            if (authRequest.getUsername() == null || authRequest.getUsername().trim().isEmpty()) {
                System.out.println("Registration rejected: Username is empty");
                return ResponseEntity.badRequest().body("Username is required");
            }
            
            if (authRequest.getPassword() == null || authRequest.getPassword().trim().isEmpty()) {
                System.out.println("Registration rejected: Password is empty");
                return ResponseEntity.badRequest().body("Password is required");
            }
            
            // Create user with USER role
            UserCredential user = authService.register(authRequest);
            
            // Also create a corresponding user in the user-service
            try {
                System.out.println("========= CREATING USER IN USER SERVICE =========");
                
                // Create a simple request with just the required fields
                Map<String, String> userRequest = new HashMap<>();
                userRequest.put("username", authRequest.getUsername());
                
                // Use default email format if none provided
                String email = (authRequest.getEmail() != null && !authRequest.getEmail().isEmpty()) 
                    ? authRequest.getEmail() 
                    : authRequest.getUsername() + "@example.com";
                userRequest.put("email", email);
                
                // Log what we're sending
                System.out.println("Request to user-service: " + userRequest);
                
                // Make the request
                HttpHeaders userHeaders = new HttpHeaders();
                userHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(userRequest, userHeaders);
                
                System.out.println("Making request to: http://user-service:8082/api/utilisateurs");
                
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                        "http://user-service:8082/api/utilisateurs",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                    );
                    
                    System.out.println("User service response status: " + response.getStatusCode());
                    System.out.println("User service response body: " + response.getBody());
                } catch (Exception e) {
                    System.err.println("REST CALL ERROR: " + e.getMessage());
                    if (e.getCause() != null) {
                        System.err.println("Caused by: " + e.getCause().getMessage());
                    }
                }
                
                System.out.println("============================================");
                
                // Liste des 15 cartes possibles avec leurs caractéristiques [nom, type]
                String[][] possibleCards = {
                    {"Ifrit, Seigneur des Flammes", "FIRE"},      // Feu - Mythologie
                    {"Neptune, Roi des Océans", "WATER"},         // Eau - Mythologie romaine
                    {"Gaia, Mère Nature", "EARTH"},              // Terre - Mythologie grecque
                    {"Fujin, Maître des Vents", "AIR"},          // Air - Mythologie japonaise
                    {"Excalibur, Lame Sacrée", "NORMAL"},        // Normal - Légende arthurienne
                    {"Phoenix Immortel", "FIRE"},                // Feu - Mythologie universelle
                    {"Poséidon, Fureur des Mers", "WATER"},     // Eau - Mythologie grecque
                    {"Atlas, Porteur du Monde", "EARTH"},        // Terre - Mythologie grecque
                    {"Garuda, Aigle Divin", "AIR"},             // Air - Mythologie hindoue
                    {"Merlin l'Enchanteur", "NORMAL"},          // Normal - Légende arthurienne
                    {"Dragon Ancestral", "FIRE"},               // Feu - Fantasy
                    {"Léviathan des Profondeurs", "WATER"},     // Eau - Mythologie hébraïque
                    {"Béhémoth Titanesque", "EARTH"},           // Terre - Mythologie hébraïque
                    {"Sylphe des Tempêtes", "AIR"},            // Air - Mythologie européenne
                    {"Héros Légendaire", "NORMAL"}             // Normal - Fantasy
                };

                // Mélanger et sélectionner 5 cartes aléatoires de façon sécurisée
                List<Integer> selectedIndices = new ArrayList<>();
                while (selectedIndices.size() < 5) {
                    int index = this.secureRandom.nextInt(possibleCards.length);
                    if (!selectedIndices.contains(index)) {
                        selectedIndices.add(index);
                    }
                }

                // Définir les raretés correspondantes aux cartes
                String[] rarities = {"COMMUN", "RARE", "EPIQUE", "RARE", "COMMUN"};
                int[] powers = {50, 75, 100, 75, 50}; // Puissance basée sur la rareté
                
                for (int i = 0; i < 5; i++) {
                    String[] selectedCard = possibleCards[selectedIndices.get(i)];
                    Map<String, Object> cardData = new HashMap<>();
                    cardData.put("nom", selectedCard[0]);
                    cardData.put("description", String.format(
                        "Une puissante carte %s maniée par %s. Cette entité légendaire maîtrise les pouvoirs %s.",
                        selectedCard[1].toLowerCase(),
                        authRequest.getUsername(),
                        getElementDescription(selectedCard[1])
                    ));
                    cardData.put("rarete", rarities[i]);
                    cardData.put("prix", 10.0 + (i * 5));
                    cardData.put("aVendre", false);
                    cardData.put("proprietaireId", user.getId());
                    cardData.put("energy", 100);
                    cardData.put("power", powers[i]);
                    cardData.put("type", selectedCard[1]);

                    try {
                        HttpHeaders cardHeaders = new HttpHeaders();
                        cardHeaders.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<Map<String, Object>> cardRequest = new HttpEntity<>(cardData, cardHeaders);
                        
                        ResponseEntity<String> cardResponse = restTemplate.exchange(
                            "http://carte-service:8085/api/cartes",
                            HttpMethod.POST,
                            cardRequest,
                            String.class
                        );
                        
                        System.out.println("Card " + (i+1) + " created with response: " + cardResponse.getBody());
                    } catch (Exception e) {
                        System.err.println("Error creating card " + (i+1) + ": " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error in user/card creation: " + e.getMessage());
            }
            
            return ResponseEntity.ok(user.getId());
        } catch (Exception e) {
            System.err.println("========= REGISTRATION ERROR =========");
            System.err.println("Error in registration: " + e.getMessage());
            e.printStackTrace(System.err);
            System.err.println("======================================");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/api/auth/validate")
    @ResponseBody
    public ResponseEntity<Boolean> validateToken(@RequestParam("token") String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
    
    @GetMapping("/api/auth/health")
    @ResponseBody
    public String health() {
        return "Auth service is up and running!";
    }

    protected String getElementDescription(String elementType) {
        switch (elementType) {
            case "FIRE": return "des flammes éternelles";
            case "WATER": return "des océans profonds";
            case "EARTH": return "de la terre ancestrale";
            case "AIR": return "des vents célestes";
            default: return "des forces primordiales";
        }
    }
}
