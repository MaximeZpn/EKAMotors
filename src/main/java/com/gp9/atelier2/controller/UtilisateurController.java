package com.gp9.atelier2.controller;

import com.gp9.atelier2.dto.MarketTransactionDTO;
import com.gp9.atelier2.dto.ModifMotDePasseDTO;
import com.gp9.atelier2.model.Carte;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.repository.CarteRepository;
import com.gp9.atelier2.repository.UtilisateurRepository;
import com.gp9.atelier2.service.MarketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CarteRepository carteRepository;

    private final MarketService marketService;

    public UtilisateurController(MarketService marketService) {
        this.marketService = marketService;
    }

    @PostMapping("/inscription")
    public ResponseEntity<String> inscrire(@RequestBody Utilisateur utilisateur) {
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isEmpty()) {
            return ResponseEntity.badRequest().body("Le mot de passe est requis !");
        }

        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email déjà utilisé !");
        }

        utilisateur.setSolde(1000);
        utilisateur = utilisateurRepository.save(utilisateur); // Sauvegarde du nouvel utilisateur

        // 🔹 Récupérer les cartes sans propriétaire
        List<Carte> cartesDisponibles = carteRepository.findByProprietaireIsNull();

        if (cartesDisponibles.size() < 5) {
            return ResponseEntity.badRequest().body("Pas assez de cartes disponibles pour attribution !");
        }

        // 🔹 Mélanger la liste et prendre les 5 premières cartes
        Collections.shuffle(cartesDisponibles);
        List<Carte> cartesAttribuees = cartesDisponibles.subList(0, 5);

        // 🔹 Assigner les cartes à l'utilisateur et sauvegarder
        for (Carte carte : cartesAttribuees) {
            carte.setProprietaire(utilisateur);
        }
        carteRepository.saveAll(cartesAttribuees);

        return ResponseEntity.ok("Inscription réussie ! 5 cartes ont été attribuées.");
    }

    @PostMapping("/modifier-mdp")
    public ResponseEntity<String> modifierMotDePasse(@RequestBody ModifMotDePasseDTO modifMotDePasseDTO, HttpSession session) {
        // Récupérer l'utilisateur connecté depuis la session
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            return ResponseEntity.status(401).body("Utilisateur non connecté.");
        }

        // Validation de la modification du mot de passe
        if (modifMotDePasseDTO.getMotDePasse() == null || modifMotDePasseDTO.getMotDePasse().isEmpty()) {
            return ResponseEntity.badRequest().body("Le nouveau mot de passe ne peut pas être vide.");
        }

        // Mise à jour du mot de passe
        utilisateur.setMotDePasse(modifMotDePasseDTO.getMotDePasse());
        utilisateurRepository.save(utilisateur);

        // Invalider la session de l'utilisateur après modification du mot de passe
        session.invalidate();

        return ResponseEntity.ok("Mot de passe modifié avec succès, vous allez être redirigé vers la page de connexion.");
    }

    // Route pour récupérer l'historique des transactions d'un utilisateur
    @GetMapping("/{utilisateurId}/transactions")
    public ResponseEntity<List<MarketTransactionDTO>> getHistoriqueTransactions(@PathVariable Long utilisateurId) {
        List<MarketTransactionDTO> transactions = marketService.historiqueTransactions(utilisateurId);
        return ResponseEntity.ok(transactions);
    }

}
