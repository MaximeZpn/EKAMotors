package com.gp9.atelier2.service;

import com.gp9.atelier2.dto.CarteDTO;
import com.gp9.atelier2.dto.MarketTransactionDTO;
import com.gp9.atelier2.model.Carte;
import com.gp9.atelier2.model.MarketTransaction;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.repository.CarteRepository;
import com.gp9.atelier2.repository.MarketTransactionRepository;
import com.gp9.atelier2.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarketService {
    private final CarteRepository carteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MarketTransactionRepository marketTransactionRepository;

    public MarketService(CarteRepository carteRepository, UtilisateurRepository utilisateurRepository, MarketTransactionRepository marketTransactionRepository) {
        this.carteRepository = carteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.marketTransactionRepository = marketTransactionRepository;
    }

    @Transactional
    public String acheterCarte(Long carteId, Long acheteurId, HttpSession session) {
        Optional<Carte> optionalCarte = carteRepository.findById(carteId);
        Optional<Utilisateur> optionalAcheteur = utilisateurRepository.findById(acheteurId);

        if (optionalCarte.isEmpty() || optionalAcheteur.isEmpty()) {
            return "Véhicule ou acheteur introuvable.";
        }

        Carte carte = optionalCarte.get();
        Utilisateur acheteur = optionalAcheteur.get();

        if (!carte.isAVendre()) {
            return "Ce véhicule n'est pas en vente.";
        }

        Utilisateur vendeur = carte.getProprietaire();
        double prix = carte.getPrix();

        if (acheteur.equals(vendeur)) {
            return "Vous ne pouvez pas acheter votre propre véhicule.";
        }

        if (acheteur.getSolde() < prix) {
            return "Solde insuffisant.";
        }

        // Mise à jour des soldes
        acheteur.setSolde(acheteur.getSolde() - prix);
        vendeur.setSolde(vendeur.getSolde() + prix);

        // Transfert de propriété
        carte.setProprietaire(acheteur);
        carte.setAVendre(false);

        // Sauvegarde des modifications
        utilisateurRepository.save(acheteur);
        utilisateurRepository.save(vendeur);
        carteRepository.save(carte);

        // Enregistrer la transaction
        MarketTransaction transaction = new MarketTransaction(acheteur, vendeur, carte, prix);
        marketTransactionRepository.save(transaction);

        // ✅ Mise à jour de la session avec le nouvel utilisateur
        session.setAttribute("utilisateur", utilisateurRepository.findById(acheteur.getId()).orElse(null));

        return "Achat réussi et enregistré !";
    }



    @Transactional
    public String mettreEnVente(Long carteId, Long utilisateurId, double prix) {
        if (prix <= 0) {
            return "Le prix doit être supérieur à 0.";
        }

        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        Optional<Carte> carteOpt = carteRepository.findById(carteId);

        if (utilisateurOpt.isEmpty() || carteOpt.isEmpty()) {
            return "Utilisateur ou véhicule introuvable.";
        }

        Carte carte = carteOpt.get();
        if (!carte.getProprietaire().getId().equals(utilisateurId)) {
            return "Erreur : Ce véhicule ne vous appartient pas.";
        }

        carte.setPrix(prix);
        carte.setAVendre(true);
        carteRepository.save(carte);
        return "Véhicule mis en vente avec succès.";
    }

    // Méthode pour récupérer l'historique des transactions d'un utilisateur
    public List<MarketTransactionDTO> historiqueTransactions(Long utilisateurId) {
        return marketTransactionRepository.findTransactionsAvecCartes(utilisateurId);
    }


    public List<CarteDTO> getCartesEnVente() {
        List<Carte> cartes = marketTransactionRepository.trouverCartesEnVente();
        return cartes.stream().map(CarteDTO::new).collect(Collectors.toList());
    }
}
