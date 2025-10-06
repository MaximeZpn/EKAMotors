package com.gp9.game_MS.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EnergyRegenerationService {

    private static final Logger logger = LoggerFactory.getLogger(EnergyRegenerationService.class);
    
    private final CardService cardService;
    
    @Autowired
    public EnergyRegenerationService(CardService cardService) {
        this.cardService = cardService;
    }
    
    @Scheduled(fixedRate = 3600000) // Every hour
    public void regenerateEnergy() {
        logger.info("Starting scheduled energy regeneration");
        int cardsUpdated = cardService.regenerateCardsEnergy();
        logger.info("Energy regeneration complete. Updated {} cards", cardsUpdated);
    }
}
