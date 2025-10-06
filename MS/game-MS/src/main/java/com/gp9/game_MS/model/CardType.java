package com.gp9.game_MS.model;

import java.util.HashMap;
import java.util.Map;

public enum CardType {
    FIRE, WATER, EARTH, AIR, NORMAL;
    
    private static final Map<String, Map<String, Double>> ADVANTAGE_MAP = new HashMap<>();
    
    static {
        // Initialize advantage multipliers
        Map<String, Double> fireAdvantages = new HashMap<>();
        fireAdvantages.put("EARTH", 1.5);  // Fire is strong against Earth
        fireAdvantages.put("AIR", 0.8);    // Fire is weak against Air
        ADVANTAGE_MAP.put("FIRE", fireAdvantages);
        
        Map<String, Double> waterAdvantages = new HashMap<>();
        waterAdvantages.put("FIRE", 1.5);   // Water is strong against Fire
        waterAdvantages.put("EARTH", 0.8);  // Water is weak against Earth
        ADVANTAGE_MAP.put("WATER", waterAdvantages);
        
        Map<String, Double> earthAdvantages = new HashMap<>();
        earthAdvantages.put("AIR", 1.5);    // Earth is strong against Air
        earthAdvantages.put("WATER", 1.5);  // Earth is strong against Water
        earthAdvantages.put("FIRE", 0.8);   // Earth is weak against Fire
        ADVANTAGE_MAP.put("EARTH", earthAdvantages);
        
        Map<String, Double> airAdvantages = new HashMap<>();
        airAdvantages.put("WATER", 1.5);   // Air is strong against Water
        airAdvantages.put("FIRE", 1.5);    // Air is strong against Fire
        airAdvantages.put("EARTH", 0.8);   // Air is weak against Earth
        ADVANTAGE_MAP.put("AIR", airAdvantages);
    }
    
    /**
     * Gets the advantage multiplier for this type against another type
     */
    public static double getAdvantageMultiplier(String attackerType, String defenderType) {
        // Default multiplier (no advantage or disadvantage)
        double defaultMultiplier = 1.0;
        
        if (attackerType == null || defenderType == null) {
            return defaultMultiplier;
        }
        
        Map<String, Double> advantages = ADVANTAGE_MAP.get(attackerType);
        if (advantages != null && advantages.containsKey(defenderType)) {
            return advantages.get(defenderType);
        }
        
        return defaultMultiplier;
    }

    public static CardType fromString(String type) {
        if (type == null) {
            return NORMAL;
        }
        
        try {
            return CardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }
}
