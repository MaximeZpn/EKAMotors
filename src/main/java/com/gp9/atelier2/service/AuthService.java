package com.gp9.atelier2.service;

import com.gp9.atelier2.dto.UtilisateurInscriptionDTO;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.model.enums.Role;
import com.gp9.atelier2.repository.RoleRepository;
import com.gp9.atelier2.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UtilisateurRepository utilisateurRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
    }

    public Utilisateur inscrireUtilisateur(UtilisateurInscriptionDTO utilisateurDTO) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setSurnom(utilisateurDTO.getSurnom());
        utilisateur.setEmail(utilisateurDTO.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDTO.getMotDePasse()));
        utilisateur.setSolde(1000);
        
        // Ajout du rôle USER par défaut
        Role roleUser = roleRepository.findByRoleName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_USER");
                    return roleRepository.save(newRole);
                });
                
        utilisateur.ajouterRole(roleUser);
        
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur inscrireAdmin(UtilisateurInscriptionDTO utilisateurDTO) {
        Utilisateur utilisateur = inscrireUtilisateur(utilisateurDTO);
        
        Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_ADMIN");
                    return roleRepository.save(newRole);
                });
                
        utilisateur.ajouterRole(roleAdmin);
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur inscrireVendeur(UtilisateurInscriptionDTO utilisateurDTO) {
        Utilisateur utilisateur = inscrireUtilisateur(utilisateurDTO);
        
        Role roleVendeur = roleRepository.findByRoleName("ROLE_VENDEUR")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_VENDEUR");
                    return roleRepository.save(newRole);
                });
                
        utilisateur.ajouterRole(roleVendeur);
        return utilisateurRepository.save(utilisateur);
    }
    
    public Utilisateur ajouterRole(Long utilisateurId, String roleName) {
        Optional<Utilisateur> optUtilisateur = utilisateurRepository.findById(utilisateurId);
        if(optUtilisateur.isPresent()) {
            Utilisateur utilisateur = optUtilisateur.get();
            
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> {
                        Role newRole = new Role(roleName);
                        return roleRepository.save(newRole);
                    });
                    
            utilisateur.ajouterRole(role);
            return utilisateurRepository.save(utilisateur);
        }
        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + utilisateurId);
    }

    public Utilisateur trouverParEmail(String email) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        return utilisateur.orElse(null);
    }
}
