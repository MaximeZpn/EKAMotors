package com.gp9.market_MS.client;

import com.gp9.market_MS.dto.CarteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class CarteClient {

    private final RestTemplate restTemplate;
    private final String carteServiceUrl;

    @Autowired
    public CarteClient(RestTemplate restTemplate, 
                      @Value("${services.carte-service.url:http://carte-service:8085}") String carteServiceUrl) {
        this.restTemplate = restTemplate;
        this.carteServiceUrl = carteServiceUrl;
    }

    public CarteDTO getCarteById(Long id) {
        try {
            String url = carteServiceUrl + "/api/cartes/" + id;
            return restTemplate.getForObject(url, CarteDTO.class);
        } catch (Exception e) {
            // Log error
            System.err.println("Error fetching carte with ID " + id + ": " + e.getMessage());
            return null;
        }
    }

    public List<CarteDTO> getCartesByUtilisateur(Long utilisateurId) {
        try {
            String url = carteServiceUrl + "/api/cartes/utilisateur/" + utilisateurId;
            ResponseEntity<CarteDTO[]> response = restTemplate.getForEntity(url, CarteDTO[].class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            // Log error
            System.err.println("Error fetching cartes for user " + utilisateurId + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean updateCarteOwnership(Long carteId, Long newOwnerId) {
        try {
            String url = carteServiceUrl + "/api/cartes/" + carteId + "/changeOwner";
            restTemplate.put(url, newOwnerId);
            return true;
        } catch (Exception e) {
            // Log error
            System.err.println("Error updating carte ownership: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateCarteSellStatus(Long carteId, boolean forSale) {
        try {
            String url = carteServiceUrl + "/api/cartes/" + carteId;
            
            // Get current card first
            CarteDTO carte = getCarteById(carteId);
            if (carte == null) {
                return false;
            }
            
            // Update sell status and send back
            carte.setAVendre(forSale);
            restTemplate.put(url, carte);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating carte sell status: " + e.getMessage());
            return false;
        }
    }
}
