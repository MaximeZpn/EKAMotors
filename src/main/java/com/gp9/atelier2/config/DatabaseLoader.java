package com.gp9.atelier2.config;

import com.gp9.atelier2.model.Carte;
import com.gp9.atelier2.model.Utilisateur;
import com.gp9.atelier2.model.enums.Role;
import com.gp9.atelier2.repository.CarteRepository;
import com.gp9.atelier2.repository.RoleRepository;
import com.gp9.atelier2.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(UtilisateurRepository utilisateurRepository, 
                                  CarteRepository carteRepository,
                                  RoleRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Création des rôles
            Role roleUser = new Role("ROLE_USER");
            Role roleAdmin = new Role("ROLE_ADMIN");
            Role roleVendeur = new Role("ROLE_VENDEUR");
            
            roleRepository.saveAll(List.of(roleUser, roleAdmin, roleVendeur));
            
            // 🔹 Création des utilisateurs
            Utilisateur jean = new Utilisateur("JeanGamer", "jean@gmail.com", passwordEncoder.encode("a"), 100.0);
            jean.ajouterRole(roleUser);
            
            Utilisateur marie = new Utilisateur("MarieTop", "marie@gmail.com", passwordEncoder.encode("securepass"), 100.0);
            marie.ajouterRole(roleUser);
            marie.ajouterRole(roleVendeur);
            
            // Utilisateur admin
            Utilisateur admin = new Utilisateur("Admin", "admin@admin.com", passwordEncoder.encode("admin123"), 9999.0);
            admin.ajouterRole(roleUser);
            admin.ajouterRole(roleAdmin);

            utilisateurRepository.saveAll(List.of(jean, marie, admin));

            // 🔹 Création des cartes existantes
            List<Carte> cartes = List.of(
                    new Carte("Dragon de Feu", "Un puissant dragon cracheur de feu", "https://example.com/dragon.jpg", 20.0, 5, "Légendaire", jean),
                    new Carte("Elfe Magique", "Un elfe agile et puissant en magie", "https://example.com/elfe.jpg", 15.0, 3, "Rare", marie),
                    new Carte("Orc Brutal", "Un orc puissant mais lent", "https://example.com/orc.jpg", 18.0, 4, "Commune", jean),
                    new Carte("Mage des Glaces", "Un sorcier maîtrisant la glace", "https://example.com/mage.jpg", 25.0, 4, "Épique", marie),
                    new Carte("Chevalier Noir", "Un guerrier d'élite en armure", "https://example.com/chevalier.jpg", 30.0, 5, "Légendaire", jean),
                    new Carte("Golem de Pierre", "Un monstre de roche inébranlable", "https://example.com/golem.jpg", 22.0, 4, "Rare", marie),
                    new Carte("Fée Lumineuse", "Une petite fée dotée de pouvoirs curatifs", "https://example.com/fee.jpg", 10.0, 2, "Commune", jean),
                    new Carte("Démon Infernal", "Une créature des enfers", "https://example.com/demon.jpg", 35.0, 6, "Légendaire", marie),
                    new Carte("Vampire Sanguinaire", "Un buveur de sang immortel", "https://example.com/vampire.jpg", 28.0, 5, "Épique", jean),
                    new Carte("Loup-Garou", "Un guerrier bestial sous la pleine lune", "https://example.com/loup.jpg", 27.0, 4, "Rare", marie)
            );

            // 🔹 Ajout de 10 nouvelles cartes sans propriétaire
            List<Carte> nouvellesCartes = List.of(
                    new Carte("Phénix Éternel", "Un oiseau de feu renaissant de ses cendres", "https://example.com/phenix.jpg", 40.0, 6, "Légendaire", null),
                    new Carte("Serpent Venimeux", "Un serpent rapide et mortel", "https://example.com/serpent.jpg", 12.0, 3, "Rare", null),
                    new Carte("Titan de Fer", "Un colosse en armure impénétrable", "https://example.com/titan.jpg", 50.0, 7, "Épique", null),
                    new Carte("Archer Fantôme", "Un esprit qui tire des flèches spectrales", "https://example.com/archer.jpg", 18.0, 4, "Commune", null),
                    new Carte("Nymphe des Bois", "Une créature bienveillante protégeant la nature", "https://example.com/nymphe.jpg", 22.0, 3, "Rare", null),
                    new Carte("Minotaure Furieux", "Un monstre à la puissance brute", "https://example.com/minotaure.jpg", 32.0, 5, "Épique", null),
                    new Carte("Spectre de l'Ombre", "Une entité insaisissable hantant les ténèbres", "https://example.com/spectre.jpg", 28.0, 5, "Légendaire", null),
                    new Carte("Gargouille de Pierre", "Un gardien silencieux posé sur les toits", "https://example.com/gargouille.jpg", 20.0, 4, "Rare", null),
                    new Carte("Berserker Viking", "Un guerrier intrépide au combat", "https://example.com/berserker.jpg", 26.0, 4, "Commune", null),
                    new Carte("Chaman Ancien", "Un sage maîtrisant les arts mystiques", "https://example.com/chaman.jpg", 30.0, 5, "Épique", null)
            );

            // 🔹 Mettre certaines cartes en vente
            cartes.get(1).setAVendre(true); // Elfe Magique
            cartes.get(4).setAVendre(true); // Chevalier Noir
            cartes.get(7).setAVendre(true); // Démon Infernal
            cartes.get(9).setAVendre(true); // Loup-Garou

            // 🔹 Sauvegarde en base
            carteRepository.saveAll(cartes);
            carteRepository.saveAll(nouvellesCartes); // Ajout des nouvelles cartes

            System.out.println("🚀 Base de données initialisée avec 3 utilisateurs et 20 cartes (dont 10 sans propriétaire) !");
        };
    }
}
