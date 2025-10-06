package com.gp9.atelier2.controller;

import com.gp9.atelier2.dto.CarteDTO;
import com.gp9.atelier2.dto.MarketTransactionDTO;
import com.gp9.atelier2.model.MarketTransaction;
import com.gp9.atelier2.service.MarketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping("/cartes-en-vente")
    public ResponseEntity<List<CarteDTO>> getCartesEnVente() {
        return ResponseEntity.ok(marketService.getCartesEnVente());
    }

    @PostMapping("/achat")
    public ResponseEntity<String> acheterCarte(@RequestParam Long carteId, @RequestParam Long acheteurId, HttpSession session) {
        String message = marketService.acheterCarte(carteId, acheteurId, session);
        if (message.contains("succès") || message.contains("enregistré")) {
            return ResponseEntity.ok(message);
        }
        else{
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PostMapping("/vente")
    public ResponseEntity<String> mettreEnVente(@RequestParam Long carteId, @RequestParam Long utilisateurId, @RequestParam double prix) {
        String message = marketService.mettreEnVente(carteId, utilisateurId, prix);
        if (message.contains("succès")) {
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.badRequest().body(message);
    }


}
