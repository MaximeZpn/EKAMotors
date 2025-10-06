package com.gp9.game_MS.service;

import com.gp9.game_MS.dto.BattleResultDTO;
import com.gp9.game_MS.dto.CardDTO;
import com.gp9.game_MS.model.Battle;
import com.gp9.game_MS.model.CardType;
import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.BattleRepository;
import com.gp9.game_MS.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BattleService {

    private static final Logger logger = LoggerFactory.getLogger(BattleService.class);

    private final BattleRepository battleRepository;
    private final RoomRepository roomRepository;
    private final CardService cardService;
    private final UserService userService;

    @Autowired
    public BattleService(BattleRepository battleRepository, 
                        RoomRepository roomRepository,
                        CardService cardService,
                        UserService userService) {
        this.battleRepository = battleRepository;
        this.roomRepository = roomRepository;
        this.cardService = cardService;
        this.userService = userService;
    }

    private int calculateEnergyLossForBattle(int myScore, int opponentScore, boolean isWinner) {
        int scoreDifference = Math.abs(myScore - opponentScore);
        
        // Base energy loss depends on if the card won or lost
        int baseEnergyLoss = isWinner ? 15 : 25;
        
        // Adjust based on score difference
        double multiplier;
        if (scoreDifference > 100) {
            multiplier = 0.5; // Easy battle
        } else if (scoreDifference > 50) {
            multiplier = 1.0; // Normal battle
        } else if (scoreDifference > 25) {
            multiplier = 1.5; // Hard battle
        } else {
            multiplier = 2.0; // Very close battle
        }
        
        int finalEnergyLoss = (int)(baseEnergyLoss * multiplier);
        return Math.min(100, finalEnergyLoss); // Cap at 100
    }

    @Async
    @Transactional
    public void startBattle(Long roomId) {
        logger.info("Starting battle for room: {}", roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room non trouvée"));

        // Get cards from registry
        CardDTO card1 = cardService.getCardDTOForBattle(roomId, 1);
        CardDTO card2 = cardService.getCardDTOForBattle(roomId, 2);

        if (card1 == null || card2 == null) {
            throw new IllegalStateException("Les cartes ne sont pas prêtes pour le combat");
        }

        // Create battle record
        Battle battle = new Battle();
        battle.setRoomId(roomId);
        battle.setCard1Id(card1.getId());
        battle.setCard2Id(card2.getId());
        battle.setStartTime(LocalDateTime.now());
        battle.setEndTime(LocalDateTime.now()); // Immediate completion
        
        // Simple battle logs
        battle.addLog("Début du combat: " + card1.getNom() + " contre " + card2.getNom());
        battle.addLog("Carte 1: " + card1.getNom() + " (Type: " + card1.getType() + ", Force: " + card1.getPower() + ")");
        battle.addLog("Carte 2: " + card2.getNom() + " (Type: " + card2.getType() + ", Force: " + card2.getPower() + ")");

        // Calculate scores
        int card1Score = calculateCardScore(card1, card2, battle);
        int card2Score = calculateCardScore(card2, card1, battle);
        
        battle.addLog("Score de " + card1.getNom() + ": " + card1Score);
        battle.addLog("Score de " + card2.getNom() + ": " + card2Score);

        // Determine winner and handle rewards
        Long winnerId = null;
        if (card1Score > card2Score) {
            winnerId = card1.getProprietaireId();
            battle.setWinnerCardId(card1.getId());

            // Calculate and update energy loss based on battle difficulty
            int card1EnergyLoss = calculateEnergyLossForBattle(card1Score, card2Score, true);
            int card2EnergyLoss = calculateEnergyLossForBattle(card2Score, card1Score, false);
            
            if (!cardService.updateCardEnergy(card1.getId(), -card1EnergyLoss)) {
                logger.warn("Failed to update energy for winner card {}, energy loss: {}", card1.getId(), card1EnergyLoss);
            }
            if (!cardService.updateCardEnergy(card2.getId(), -card2EnergyLoss)) {
                logger.warn("Failed to update energy for loser card {}, energy loss: {}", card2.getId(), card2EnergyLoss);
            }
            
            battle.addLog(String.format("Perte d'énergie - %s: %d points", card1.getNom(), card1EnergyLoss));
            battle.addLog(String.format("Perte d'énergie - %s: %d points", card2.getNom(), card2EnergyLoss));
            
            // Give rewards to winner
            double totalPrize = room.getBetAmount() * 2;
            userService.addBalance(winnerId, totalPrize);
            battle.addLog(String.format("Le joueur %d reçoit %.2f € !", winnerId, totalPrize));
            
        } else if (card2Score > card1Score) {
            winnerId = card2.getProprietaireId();
            battle.setWinnerCardId(card2.getId());

            // Calculate and update energy loss based on battle difficulty
            int card2EnergyLoss = calculateEnergyLossForBattle(card2Score, card1Score, true);
            int card1EnergyLoss = calculateEnergyLossForBattle(card1Score, card2Score, false);
            
            if (!cardService.updateCardEnergy(card2.getId(), -card2EnergyLoss)) {
                logger.warn("Failed to update energy for winner card {}, energy loss: {}", card2.getId(), card2EnergyLoss);
            }
            if (!cardService.updateCardEnergy(card1.getId(), -card1EnergyLoss)) {
                logger.warn("Failed to update energy for loser card {}, energy loss: {}", card1.getId(), card1EnergyLoss);
            }
            
            battle.addLog(String.format("Perte d'énergie - %s: %d points", card2.getNom(), card2EnergyLoss));
            battle.addLog(String.format("Perte d'énergie - %s: %d points", card1.getNom(), card1EnergyLoss));
            
            // Give rewards to winner
            double totalPrize = room.getBetAmount() * 2;
            userService.addBalance(winnerId, totalPrize);
            battle.addLog(String.format("Le joueur %d reçoit %.2f € !", winnerId, totalPrize));
            
        } else {
            // Match nul - Les deux perdent un peu d'énergie et récupèrent leur mise
            int drawEnergyLoss = calculateEnergyLossForBattle(card1Score, card2Score, false);
            
            if (!cardService.updateCardEnergy(card1.getId(), -drawEnergyLoss)) {
                logger.warn("Failed to update energy for card {}, energy loss: {}", card1.getId(), drawEnergyLoss);
            }
            if (!cardService.updateCardEnergy(card2.getId(), -drawEnergyLoss)) {
                logger.warn("Failed to update energy for card {}, energy loss: {}", card2.getId(), drawEnergyLoss);
            }
            
            battle.addLog(String.format("Match nul - Perte d'énergie pour les deux cartes: %d points", drawEnergyLoss));
            
            userService.addBalance(card1.getProprietaireId(), room.getBetAmount());
            userService.addBalance(card2.getProprietaireId(), room.getBetAmount());
            battle.addLog("Match nul! Les joueurs récupèrent leur mise.");
        }

        // Save battle record
        battleRepository.save(battle);
        
        // Update room status and distribute rewards
        if (winnerId != null) {
            finishRoom(roomId, winnerId);
            battle.addLog("Le joueur " + winnerId + " remporte la victoire et la mise!");
        } else {
            finishRoom(roomId, null);
            battle.addLog("Match nul! Les joueurs récupèrent leur mise.");
        }
        
        // Save updated battle logs
        battleRepository.save(battle);
        
        logger.info("Battle completed for room: {}, winner: {}", roomId, winnerId);
    }

    private int calculateCardScore(CardDTO card, CardDTO opponent, Battle battle) {
        int baseScore = card.getPower();
        double totalMultiplier = 1.0;
        Map<String, Double> multipliers = new HashMap<>();
        
        // 1. Multiplicateur de rareté
        double rarityMultiplier = getRarityMultiplier(card.getRarete());
        multipliers.put("rareté", rarityMultiplier);
        
        // 2. Multiplicateur d'énergie
        double energyMultiplier = 0.5 + (card.getEnergy() / 100.0);
        multipliers.put("énergie", energyMultiplier);
        
        // 3. Avantage/Désavantage de type
        double typeMultiplier = getTypeAdvantage(card.getType(), opponent.getType());
        multipliers.put("type", typeMultiplier);
        
        // Calculer le multiplicateur total
        for (double multiplier : multipliers.values()) {
            totalMultiplier *= multiplier;
        }
        
        int finalScore = (int)(baseScore * totalMultiplier);
        
        // Bonus de combo
        int comboBonus = calculateComboBonus(card.getType(), opponent.getType());
        finalScore += comboBonus;
        
        // Logs détaillés
        battle.addLog(String.format("\nDétails du score pour %s:", card.getNom()));
        battle.addLog(String.format("- Score de base (power): %d", baseScore));
        battle.addLog(String.format("- Multiplicateur de rareté (%s): x%.2f", card.getRarete(), rarityMultiplier));
        battle.addLog(String.format("- Multiplicateur d'énergie (%d%%): x%.2f", card.getEnergy(), energyMultiplier));
        
        // Log spécial pour l'avantage/désavantage de type
        String typeEffect = getTypeEffectDescription(card.getType(), opponent.getType(), typeMultiplier);
        battle.addLog(typeEffect);
        
        if (comboBonus > 0) {
            battle.addLog(String.format("- Bonus de combo %s vs %s: +%d", card.getType(), opponent.getType(), comboBonus));
        }
        
        battle.addLog(String.format("Score final: %d", finalScore));
        return finalScore;
    }
    
    private String getTypeEffectDescription(String attackerType, String defenderType, double multiplier) {
        if (multiplier > 1.0) {
            return String.format("- Avantage de type! %s est super efficace contre %s (x%.1f)", 
                attackerType, defenderType, multiplier);
        } else if (multiplier < 1.0) {
            return String.format("- Désavantage de type! %s est peu efficace contre %s (x%.1f)", 
                attackerType, defenderType, multiplier);
        }
        return String.format("- Type %s vs %s: Pas d'effet particulier (x%.1f)", 
            attackerType, defenderType, multiplier);
    }
    
    private double getRarityMultiplier(String rarity) {
        if (rarity == null) return 1.0;
        return switch (rarity.toUpperCase()) {
            case "COMMUN" -> 1.0;
            case "RARE" -> 1.2;
            case "EPIQUE" -> 1.5;
            case "LEGENDAIRE" -> 2.0;
            default -> 1.0;
        };
    }
    
    private double getTypeAdvantage(String attackerType, String defenderType) {
        if (attackerType == null || defenderType == null) return 1.0;
        
        Map<String, Map<String, Double>> typeChart = Map.of(
            "FEU", Map.of(
                "TERRE", 2.0,  // Feu super efficace contre Terre
                "EAU", 0.5,    // Feu faible contre Eau
                "AIR", 1.0     // Feu neutre contre Air
            ),
            "EAU", Map.of(
                "FEU", 2.0,    // Eau super efficace contre Feu
                "TERRE", 0.5,  // Eau faible contre Terre
                "AIR", 1.0     // Eau neutre contre Air
            ),
            "TERRE", Map.of(
                "EAU", 2.0,    // Terre super efficace contre Eau
                "AIR", 0.5,    // Terre faible contre Air
                "FEU", 1.0     // Terre neutre contre Feu
            ),
            "AIR", Map.of(
                "TERRE", 2.0,  // Air super efficace contre Terre
                "FEU", 0.5,    // Air faible contre Feu
                "EAU", 1.0     // Air neutre contre Eau
            )
        );

        return typeChart
            .getOrDefault(attackerType.toUpperCase(), Map.of())
            .getOrDefault(defenderType.toUpperCase(), 1.0);
    }
    
    private int calculateComboBonus(String type1, String type2) {
        // Bonus spéciaux pour certaines combinaisons de types
        if (type1 == null || type2 == null) return 0;
        
        return switch (type1.toUpperCase() + "_" + type2.toUpperCase()) {
            case "FEU_AIR", "AIR_FEU" -> 15;        // Tempête de feu
            case "EAU_TERRE", "TERRE_EAU" -> 10;    // Tsunami
            case "AIR_EAU", "EAU_AIR" -> 12;        // Tempête de glace
            case "FEU_TERRE", "TERRE_FEU" -> 8;     // Volcan
            default -> 0;
        };
    }

    private String getDifficultyDescription(int scoreDifference) {
        if (scoreDifference > 100) {
            return "Combat facile";
        } else if (scoreDifference > 50) {
            return "Combat normal";
        } else if (scoreDifference > 25) {
            return "Combat difficile";
        } else {
            return "Combat très serré";
        }
    }

    @Transactional
    public void finishRoom(Long roomId, Long winnerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room non trouvée"));
        
        room.setStatus(RoomStatus.FINISHED);
        room.setWinnerId(winnerId);
        room.setFinishedAt(LocalDateTime.now());
        roomRepository.save(room);
        
        // Reward logic will be handled by external UserService
    }

    public BattleResultDTO getBattleResultByRoomId(Long roomId) {
        Battle battle = battleRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Aucun combat trouvé pour cette room"));
        return new BattleResultDTO(battle);
    }
}
