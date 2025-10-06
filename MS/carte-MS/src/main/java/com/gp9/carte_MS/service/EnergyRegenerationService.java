package com.gp9.carte_MS.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EnergyRegenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnergyRegenerationService.class);
    
    private final CarteService carteService;
    
    @Value("${game.energy.regen.small:5}")
    private int smallRegen;
    
    @Value("${game.energy.regen.medium:10}")
    private int mediumRegen;
    
    @Value("${game.energy.regen.large:15}")
    private int largeRegen;
    
    @Autowired
    public EnergyRegenerationService(CarteService carteService) {
        this.carteService = carteService;
    }
    
    @Scheduled(fixedDelayString = "${game.energy.regen.interval:120000}") // 2 minutes
    public void regenerateEnergy() {
        logger.info("Starting progressive energy regeneration (2 minutes interval)");
        
        // Régénération progressive basée sur le niveau d'énergie actuel
        int updatedCards = carteService.regenerateEnergyProgressive(
            smallRegen,   // Pour les cartes > 66% énergie
            mediumRegen, // Pour les cartes entre 33% et 66% énergie
            largeRegen   // Pour les cartes < 33% énergie
        );
        
        logger.info("Energy regeneration complete. Updated {} cards", updatedCards);
    }
}
