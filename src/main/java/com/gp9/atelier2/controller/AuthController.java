package com.gp9.atelier2.controller;

import com.gp9.atelier2.dto.UtilisateurDTO;
import com.gp9.atelier2.dto.UtilisateurInscriptionDTO;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.service.AuthService;
import com.gp9.atelier2.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Inscription d'un nouvel utilisateur.
     *
     * @param utilisateurDTO Informations d'inscription (surnom, email, motDePasse)
     * @return Message de confirmation
     */
    @PostMapping("/inscription")
    public ResponseEntity<?> inscrire(@RequestBody UtilisateurInscriptionDTO utilisateurDTO) {
        Utilisateur utilisateur = authService.inscrireUtilisateur(utilisateurDTO);
        return ResponseEntity.ok(Map.of("message", "Utilisateur inscrit avec succès", "utilisateurId", utilisateur.getId()));
    }

    /**
     * Connexion d'un utilisateur (Génération du token JWT).
     *
     * @param credentials JSON contenant email et motDePasse
     * @return Token JWT si authentification réussie
     */
    @PostMapping("/connexion")
    public ResponseEntity<?> connecter(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String motDePasse = credentials.get("motDePasse");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, motDePasse)
        );

        String token = jwtService.generateToken(authentication);

        return ResponseEntity.ok(Map.of("token", token));
    }

    /**
     * Récupération des informations de l'utilisateur connecté.
     *
     * @param authHeader Header contenant le token JWT
     * @return Informations de l'utilisateur
     */
    @GetMapping("/profil")
    public ResponseEntity<?> getProfil(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Utilisateur utilisateur = authService.trouverParEmail(email);

            if (utilisateur == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Utilisateur non trouvé"));
            }

            UtilisateurDTO utilisateurDTO = new UtilisateurDTO(
                utilisateur.getId(), 
                utilisateur.getSurnom(), 
                utilisateur.getEmail(), 
                utilisateur.getSolde()
            );
            return ResponseEntity.ok(utilisateurDTO);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expirée ou non valide"));
        }
    }

    /**
     * Vérifier si l'utilisateur a le rôle ADMIN
     */
    @GetMapping("/check-role")
    public ResponseEntity<?> checkAdminRole(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Utilisateur utilisateur = authService.trouverParEmail(email);

            if (utilisateur == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Utilisateur non trouvé"));
            }

            boolean hasAdminRole = utilisateur.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            return ResponseEntity.ok(Map.of(
                "hasAdminRole", hasAdminRole,
                "email", utilisateur.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Session expirée ou non valide"));
        }
    }

    /**
     * Vérifier les rôles de l'utilisateur
     */
    @GetMapping("/check-roles")
    public ResponseEntity<?> checkRoles(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String email = jwtService.extractEmail(token);
            Utilisateur utilisateur = authService.trouverParEmail(email);

            if (utilisateur == null) {
                return ResponseEntity.status(404).body(Map.of("message", "Utilisateur non trouvé"));
            }

            return ResponseEntity.ok(Map.of(
                "email", utilisateur.getEmail(),
                "roles", utilisateur.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList()),
                "hasAdminRole", utilisateur.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
        }
    }
}
