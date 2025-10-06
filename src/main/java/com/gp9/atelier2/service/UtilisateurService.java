package com.gp9.atelier2.service;

import com.gp9.atelier2.dto.UtilisateurInscriptionDTO;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscrire un nouvel utilisateur.
     */
    public Utilisateur inscrireUtilisateur(UtilisateurInscriptionDTO inscriptionDTO) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setSurnom(inscriptionDTO.getSurnom());
        utilisateur.setEmail(inscriptionDTO.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(inscriptionDTO.getMotDePasse()));
        utilisateur.setSolde(0.0);  // Solde initial à 0

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Récupérer un utilisateur par email.
     */
    public Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }
}
