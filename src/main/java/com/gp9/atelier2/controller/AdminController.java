package com.gp9.atelier2.controller;

import com.gp9.atelier2.dto.UtilisateurDTO;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.model.enums.Role;
import com.gp9.atelier2.repository.RoleRepository;
import com.gp9.atelier2.repository.UtilisateurRepository;
import com.gp9.atelier2.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        List<UtilisateurDTO> utilisateurDTOs = utilisateurs.stream()
                .map(u -> new UtilisateurDTO(u.getId(), u.getSurnom(), u.getEmail(), u.getSolde()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(utilisateurDTOs);
    }
    
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }
    
    @PostMapping("/utilisateurs/{id}/roles")
    public ResponseEntity<?> ajouterRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String role = payload.get("role");
        if (role == null || role.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Le rôle ne peut pas être vide"));
        }
        
        try {
            Utilisateur utilisateur = authService.ajouterRole(id, role);
            return ResponseEntity.ok(Map.of(
                "message", "Rôle ajouté avec succès",
                "utilisateur", utilisateur.getSurnom(),
                "roles", utilisateur.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
