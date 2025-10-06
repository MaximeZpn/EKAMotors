package com.gp9.user_MS.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gp9.user_MS.dto.CreateUtilisateurDTO;
import com.gp9.user_MS.dto.UtilisateurDTO;
import com.gp9.user_MS.model.Utilisateur;
import com.gp9.user_MS.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Transactional
    public Utilisateur creerUtilisateur(CreateUtilisateurDTO dto) {
        // Check if username or email already exists
        if (utilisateurRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà pris");
        }
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Create new user with an initial balance of 100
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(dto.getUsername());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setSolde(100.0); // Set an initial balance of 100 instead of 0
        
        return utilisateurRepository.save(utilisateur);
    }
    
    public Optional<UtilisateurDTO> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .map(UtilisateurDTO::new);
    }
    
    public List<UtilisateurDTO> getAllUtilisateurs() {
        return utilisateurRepository.findAll()
                .stream()
                .map(UtilisateurDTO::new)
                .collect(Collectors.toList());
    }
    
    public Optional<UtilisateurDTO> getUtilisateurByUsername(String username) {
        return utilisateurRepository.findByUsername(username)
                .map(UtilisateurDTO::new);
    }
    
    public boolean existsByUsername(String username) {
        return utilisateurRepository.existsByUsername(username);
    }

    public boolean existsById(Long id) {
        return utilisateurRepository.existsById(id);
    }
    
    @Transactional
    public boolean modifierSolde(Long id, double montant) {
        Optional<Utilisateur> optUtilisateur = utilisateurRepository.findById(id);
        if (optUtilisateur.isPresent()) {
            Utilisateur utilisateur = optUtilisateur.get();
            double nouveauSolde = utilisateur.getSolde() + montant;
            if (nouveauSolde >= 0) {
                utilisateur.setSolde(nouveauSolde);
                utilisateurRepository.save(utilisateur);
                return true;
            }
        }
        return false;
    }
}
