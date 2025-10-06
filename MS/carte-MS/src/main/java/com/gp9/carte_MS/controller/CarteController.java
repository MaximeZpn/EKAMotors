package com.gp9.carte_MS.controller;

import com.gp9.carte_MS.dto.CarteDTO;
import com.gp9.carte_MS.service.CarteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/vehicules", "/api/vehicules/", "/api/cartes", "/api/cartes/"})
@CrossOrigin(origins = "*") // Add CORS support for debugging
public class CarteController {

    private final CarteService carteService;

    @Autowired
    public CarteController(CarteService carteService) {
        this.carteService = carteService;
    }

    // Debug endpoint to verify the controller is accessible
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        System.out.println("Ping received at VehiculeController (ex-CarteController)");
        return ResponseEntity.ok("Vehicule service is responding");
    }

    // Get card by ID
    @GetMapping("/{id}")
    public ResponseEntity<CarteDTO> getCarteById(@PathVariable Long id) {
        Optional<CarteDTO> carte = carteService.getCarteById(id);
        return carte.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get all cards
    @GetMapping
    public ResponseEntity<List<CarteDTO>> getAllCartes() {
        return ResponseEntity.ok(carteService.getAllCartes());
    }

    // Get cards by user
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<CarteDTO>> getCartesByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(carteService.getCartesByUtilisateur(utilisateurId));
    }

    // Create a new card - Maps to both /api/cartes and /api/cartes/ paths
    @PostMapping({"", "/"})
    public ResponseEntity<CarteDTO> createCarte(@RequestBody CarteDTO carteDTO) {
        try {
            System.out.println("==== CREATE CARD REQUEST RECEIVED ====");
            System.out.println("Received card data: " + carteDTO);
            
            // Validate the data
            if (carteDTO.getNom() == null || carteDTO.getNom().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Use the createCarte method in service
            CarteDTO newCard = carteService.createCarte(carteDTO);
            
            System.out.println("Card created successfully with ID: " + 
                              (newCard != null ? newCard.getId() : "null"));
            
            return new ResponseEntity<>(newCard, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating card: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update card
    @PutMapping("/{id}")
    public ResponseEntity<CarteDTO> updateCarte(@PathVariable Long id, @RequestBody CarteDTO carteDTO) {
        CarteDTO updatedCarte = carteService.updateCarte(id, carteDTO);
        if (updatedCarte != null) {
            return ResponseEntity.ok(updatedCarte);
        }
        return ResponseEntity.notFound().build();
    }

    // Delete card
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarte(@PathVariable Long id) {
        boolean deleted = carteService.deleteCarte(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Change card owner
    @PutMapping("/{id}/proprietaire")
    public ResponseEntity<CarteDTO> changeProprietaire(@PathVariable Long id, @RequestBody Long newProprietaireId) {
        Optional<CarteDTO> changedCarte = carteService.changeProprietaire(id, newProprietaireId);
        return changedCarte.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Energy management endpoints
    @PutMapping("/{id}/energy")
    public ResponseEntity<?> updateCardEnergy(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Integer energyChange = body.get("amount");
            if (energyChange == null) {
                return ResponseEntity.badRequest().body("Le paramètre 'amount' est requis");
            }

            CarteDTO carteDTO = carteService.updateCardEnergy(id, energyChange);
            if (carteDTO != null) {
                return ResponseEntity.ok(carteDTO);
            }
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la mise à jour de l'énergie: " + e.getMessage());
        }
    }
    
    @PostMapping("/regenerate-energy")
    public ResponseEntity<Map<String, Object>> regenerateAllCardsEnergy(@RequestParam(defaultValue = "5") int amount) {
        try {
            int updatedCount = carteService.regenerateAllCardsEnergy(amount);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Énergie régénérée avec succès");
            response.put("cardsUpdated", updatedCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la régénération de l'énergie");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/{id}/energy")
    public ResponseEntity<?> getCardEnergy(@PathVariable Long id) {
        try {
            Integer energy = carteService.getCardEnergy(id);
            if (energy != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", id);
                response.put("energy", energy);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la récupération de l'énergie: " + e.getMessage());
        }
    }
}
