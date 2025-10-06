package com.gp9.carte_MS.service;

import com.gp9.carte_MS.dto.CarteDTO;
import com.gp9.carte_MS.model.Carte;
import com.gp9.carte_MS.repository.CarteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CarteService {

    private static final Logger logger = LoggerFactory.getLogger(CarteService.class);

    @Autowired
    private CarteRepository carteRepository;

    @Value("${game.energy.max:100}")
    private int maxEnergy;

    public Optional<CarteDTO> getCarteById(Long id) {
        return carteRepository.findById(id).map(CarteDTO::new);
    }

    public List<CarteDTO> getCartesByUtilisateur(Long utilisateurId) {
        List<Carte> cartes = carteRepository.findByProprietaireId(utilisateurId);
        return cartes.stream().map(CarteDTO::new).collect(Collectors.toList());
    }
    
    public List<CarteDTO> getAllCartes() {
        List<Carte> cartes = carteRepository.findAll();
        return cartes.stream().map(CarteDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public CarteDTO ajouterCarte(CarteDTO carteDTO) {
        Carte carte = new Carte();
        carte.setNom(carteDTO.getNom());
        carte.setPrix(carteDTO.getPrix());
        carte.setAVendre(carteDTO.isAVendre());
        carte.setProprietaireId(carteDTO.getProprietaireId());
        carte = carteRepository.save(carte);
        return new CarteDTO(carte);
    }
    
    @Transactional
    public CarteDTO updateCarte(Long id, CarteDTO carteDTO) {
        Optional<Carte> optionalCarte = carteRepository.findById(id);
        if (optionalCarte.isPresent()) {
            Carte carte = optionalCarte.get();
            carte.setNom(carteDTO.getNom());
            carte.setPrix(carteDTO.getPrix());
            carte.setAVendre(carteDTO.isAVendre());
            carte.setProprietaireId(carteDTO.getProprietaireId());
            carte = carteRepository.save(carte);
            return new CarteDTO(carte);
        }
        return null;
    }
    
    @Transactional
    public boolean deleteCarte(Long id) {
        if (carteRepository.existsById(id)) {
            carteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<CarteDTO> changeProprietaire(Long id, Long newProprietaireId) {
        Optional<Carte> optCarte = carteRepository.findById(id);
        if (optCarte.isPresent()) {
            Carte carte = optCarte.get();
            carte.setProprietaireId(newProprietaireId);
            // When ownership changes, card is no longer for sale
            carte.setAVendre(false);
            carte = carteRepository.save(carte);
            return Optional.of(new CarteDTO(carte));
        }
        return Optional.empty();
    }
    
    /**
     * Update the energy of a specific card
     */
    @Transactional
    public CarteDTO updateCardEnergy(Long id, int amount) {
        Carte carte = carteRepository.findById(id).orElse(null);
        if (carte == null) {
            logger.warn("Card {} not found for energy update", id);
            return null;
        }

        // Get current energy
        int currentEnergy = carte.getEnergy();
        
        // Calculate new energy with bounds checking (0-100)
        int newEnergy = Math.max(0, Math.min(100, currentEnergy + amount));
        carte.setEnergy(newEnergy);
        
        // Update last regeneration time if adding energy
        if (amount > 0) {
            carte.setLastEnergyRegen(LocalDateTime.now());
        }

        // Save and log the change
        carte = carteRepository.save(carte);
        logger.info("Updated energy for card {}: {} -> {} (change: {})", 
            id, currentEnergy, newEnergy, amount);

        return new CarteDTO(carte);
    }
    
    /**
     * Regenerate energy for all cards
     */
    @Transactional
    public int regenerateAllCardsEnergy(int amount) {
        // Change from findByEnergyLessThan(maxEnergy)
        List<Carte> cardsToUpdate = carteRepository.findByEnergyLessThan(100);
        int updatedCount = 0;
        
        for (Carte carte : cardsToUpdate) {
            int newEnergy = Math.min(maxEnergy, carte.getEnergy() + amount);
            if (newEnergy != carte.getEnergy()) {
                carte.setEnergy(newEnergy);
                carte.setLastEnergyRegen(LocalDateTime.now());
                carteRepository.save(carte);
                updatedCount++;
            }
        }
        
        logger.info("Regenerated energy for {} cards with amount {}", updatedCount, amount);
        return updatedCount;
    }

    @Transactional
    public int regenerateEnergyProgressive(int smallRegen, int mediumRegen, int largeRegen) {
        int updatedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        
        // Fetch only cards that need regeneration
        List<Carte> cardsToUpdate = carteRepository.findByEnergyLessThan(100);
        
        for (Carte carte : cardsToUpdate) {
            int currentEnergy = carte.getEnergy();
            int energyToAdd;
            
            // Determine regeneration amount based on current energy level
            if (currentEnergy >= 66) {
                energyToAdd = smallRegen;
            } else if (currentEnergy >= 33) {
                energyToAdd = mediumRegen;
            } else {
                energyToAdd = largeRegen;
            }
            
            int newEnergy = Math.min(100, currentEnergy + energyToAdd);
            if (newEnergy != currentEnergy) {
                carte.setEnergy(newEnergy);
                carte.setLastEnergyRegen(now);
                carteRepository.save(carte);
                updatedCount++;
            }
        }
        
        return updatedCount;
    }
    
    /**
     * Get energy level of a specific card
     */
    public Integer getCardEnergy(Long id) {
        Carte carte = carteRepository.findById(id).orElse(null);
        return carte != null ? carte.getEnergy() : null;
    }

    @Transactional
    public CarteDTO createCarte(CarteDTO carteDTO) {
        System.out.println("Creating card: " + carteDTO);
        
        // Create a new Carte entity
        Carte carte = new Carte();
        
        // Set the card properties
        carte.setNom(carteDTO.getNom());
        carte.setDescription(carteDTO.getDescription());
        carte.setRarete(carteDTO.getRarete());
        carte.setPrix(carteDTO.getPrix());
        carte.setAVendre(carteDTO.isAVendre());
        carte.setProprietaireId(carteDTO.getProprietaireId());
        
        // Set energy and type with defaults if not provided
        carte.setEnergy(carteDTO.getEnergy() > 0 ? carteDTO.getEnergy() : maxEnergy);
        carte.setType(carteDTO.getType() != null ? carteDTO.getType() : null);
        
        // Save the card
        System.out.println("Saving card to database");
        carte = carteRepository.save(carte);
        System.out.println("Card saved with ID: " + carte.getId());
        
        // Return the created card
        return new CarteDTO(carte);
    }
}
