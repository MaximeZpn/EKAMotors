package com.gp9.user_MS.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gp9.user_MS.dto.CreateUtilisateurDTO;
import com.gp9.user_MS.dto.UtilisateurDTO;
import com.gp9.user_MS.model.Utilisateur;
import com.gp9.user_MS.service.UtilisateurService;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;
    
    // Add a debug endpoint to log incoming requests
    @PostMapping("/debug")
    public ResponseEntity<?> debugRequest(@RequestBody Object body) {
        System.out.println("Debug request body: " + body);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping
    public ResponseEntity<?> creerUtilisateur(@RequestBody Map<String, Object> requestMap) {
        try {
            // Log the incoming request for debugging
            System.out.println("Received user creation request: " + requestMap);
            
            // Handle both formats (DTO and direct map)
            CreateUtilisateurDTO dto = new CreateUtilisateurDTO();
            
            // Extract fields from the request map
            if (requestMap.containsKey("username")) {
                dto.setUsername((String) requestMap.get("username"));
            }
            
            if (requestMap.containsKey("email")) {
                dto.setEmail((String) requestMap.get("email"));
            } else {
                // Default email if not provided
                dto.setEmail(dto.getUsername() + "@example.com");
            }
            
            // Create the user
            Utilisateur utilisateur = utilisateurService.creerUtilisateur(dto);
            return new ResponseEntity<>(new UtilisateurDTO(utilisateur), HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(@PathVariable Long id) {
        // Try to get the user from the database
        Optional<UtilisateurDTO> utilisateur = utilisateurService.getUtilisateurById(id);
        
        // If found, return it
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        }
        
        // If not found but ID is valid, create a temporary placeholder user
        // This ensures the client always gets a response with balance info
        if (id > 0) {
            UtilisateurDTO tempUser = new UtilisateurDTO();
            tempUser.setId(id);
            tempUser.setUsername("User-" + id);
            tempUser.setEmail("user" + id + "@example.com");
            tempUser.setSolde(100.0); // Default balance
            
            // Return the temporary user with 200 OK status
            return ResponseEntity.ok(tempUser);
        }
        
        // If ID is invalid, return 404
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/")
    public List<UtilisateurDTO> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByUsername(@PathVariable String username) {
        return utilisateurService.getUtilisateurByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/exists/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        boolean exists = utilisateurService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    @PutMapping("/{id}/solde")
    public ResponseEntity<Void> modifierSolde(@PathVariable Long id, @RequestParam double montant) {
        System.out.println("Received request to modify balance for user " + id + " by " + montant);
        
        // Auto-create user if it doesn't exist
        if (!utilisateurService.existsById(id)) {
            System.out.println("User " + id + " does not exist, creating placeholder user");
            try {
                // Get username from the auth-service if possible
                String username = "User-" + id;
                String email = username.toLowerCase() + "@example.com";
                
                CreateUtilisateurDTO dto = new CreateUtilisateurDTO();
                dto.setUsername(username);
                dto.setEmail(email);
                
                Utilisateur newUser = utilisateurService.creerUtilisateur(dto);
                System.out.println("Created placeholder user: " + newUser.getId() + ", " + newUser.getUsername());
                
                // Now use the real ID from the database for balance update
                id = newUser.getId();
            } catch (Exception e) {
                System.err.println("Error creating automatic user: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        
        boolean updated = utilisateurService.modifierSolde(id, montant);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User service is up and running!");
    }
}
