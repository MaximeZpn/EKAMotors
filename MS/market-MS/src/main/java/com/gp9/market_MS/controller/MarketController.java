package com.gp9.market_MS.controller;

import com.gp9.market_MS.dto.OffreDTO;
import com.gp9.market_MS.dto.TransactionDTO;
import com.gp9.market_MS.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    @Autowired
    private MarketService marketService;
    
    // Offres endpoints
    @GetMapping("/offres")
    public List<OffreDTO> getAllOffres() {
        return marketService.getAllOffres();
    }
    
    @GetMapping("/offres/actives")
    public List<OffreDTO> getOffresActives() {
        return marketService.getOffresActives();
    }
    
    @GetMapping("/offres/vendeur/{vendeurId}")
    public List<OffreDTO> getOffresByVendeur(@PathVariable Long vendeurId) {
        return marketService.getOffresByVendeur(vendeurId);
    }
    
    @GetMapping("/offres/vendeur/{vendeurId}/actives")
    public List<OffreDTO> getOffresByVendeurActives(@PathVariable Long vendeurId) {
        return marketService.getOffresByVendeurActives(vendeurId);
    }
    
    @GetMapping("/offres/{id}")
    public ResponseEntity<OffreDTO> getOffreById(@PathVariable Long id) {
        return marketService.getOffreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/offres")
    public ResponseEntity<?> creerOffre(@RequestBody OffreDTO offreDTO) {
        System.out.println("========== DEBUG: CREATING OFFER ==========");
        System.out.println("Received offer data: " + offreDTO);
        
        try {
            if (offreDTO.getCarteId() == null) {
                System.err.println("Error: carteId is null");
                return ResponseEntity.badRequest().body("carteId is required");
            }
            
            if (offreDTO.getVendeurId() == null) {
                System.err.println("Error: vendeurId is null");
                return ResponseEntity.badRequest().body("vendeurId is required");
            }
            
            System.out.println("Calling marketService.creerOffre()");
            OffreDTO nouvelleCarte = marketService.creerOffre(offreDTO);
            System.out.println("Offer created successfully: " + nouvelleCarte);
            return new ResponseEntity<>(nouvelleCarte, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating offer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            System.out.println("========== END DEBUG: CREATING OFFER ==========");
        }
    }
    
    @DeleteMapping("/offres/{id}")
    public ResponseEntity<Void> retirerOffre(@PathVariable Long id) {
        if (marketService.retirerOffre(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Transactions endpoints
    @GetMapping("/transactions")
    public List<TransactionDTO> getAllTransactions() {
        return marketService.getAllTransactions();
    }
    
    @GetMapping("/transactions/vendeur/{vendeurId}")
    public List<TransactionDTO> getTransactionsByVendeur(@PathVariable Long vendeurId) {
        return marketService.getTransactionsByVendeur(vendeurId);
    }
    
    @GetMapping("/transactions/acheteur/{acheteurId}")
    public List<TransactionDTO> getTransactionsByAcheteur(@PathVariable Long acheteurId) {
        return marketService.getTransactionsByAcheteur(acheteurId);
    }
    
    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return marketService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/acheter/{offreId}")
    public ResponseEntity<?> acheterCarte(
            @PathVariable Long offreId, 
            @RequestParam Long acheteurId) {
        try {
            System.out.println("Controller: Processing purchase: Offer=" + offreId + ", Buyer=" + acheteurId);
            TransactionDTO transaction = marketService.acheterCarte(offreId, acheteurId);
            
            // Return success with transaction details
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the full stack trace for debugging
            System.err.println("Purchase error: " + e.getMessage());
            e.printStackTrace();
            
            // Return user-friendly error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage().replace("java.lang.RuntimeException: ", ""));
        }
    }
    
    @GetMapping("/health")
    public String health() {
        return "Market service is up and running!";
    }
}
