package com.gp9.carte_MS.config;

import com.gp9.carte_MS.model.Carte;
import com.gp9.carte_MS.repository.CarteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataInitializer {

    @Autowired
    private CarteRepository carteRepository;
    
    @Bean
    @Profile("dev")
    public CommandLineRunner initData() {
        return args -> {
            if (carteRepository.count() == 0) {
                System.out.println("Initializing database with sample data...");
                
                Carte carte1 = new Carte();
                carte1.setNom("Dragon Blanc aux Yeux Bleus");
                carte1.setPrix(50.0);
                carte1.setAVendre(true);
                carte1.setProprietaireId(1L);
                carteRepository.save(carte1);
                
                Carte carte2 = new Carte();
                carte2.setNom("Magicien Noir");
                carte2.setPrix(25.5);
                carte2.setAVendre(false);
                carte2.setProprietaireId(2L);
                carteRepository.save(carte2);
                
                Carte carte3 = new Carte();
                carte3.setNom("Exodia");
                carte3.setPrix(100.0);
                carte3.setAVendre(true);
                carte3.setProprietaireId(1L);
                carteRepository.save(carte3);
                
                System.out.println("Sample data initialization complete!");
            }
        };
    }
}
