package com.gp9.game_MS.dto;

/**
 * DTO representing a card in battles
 */
public class CardDTO {
    private Long id;
    private String nom;
    private String description;
    private String type;
    private String rarete;
    private int energy;
    private int playerPosition;
    private Long proprietaireId;
    private int hp;          // Added for battle
    private int power;       // Added for battle
    
    public CardDTO() {
        // Default constructor
        this.hp = 100;       // Default HP
        this.power = 20;     // Default power
    }
    
    // Existing getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRarete() {
        return rarete;
    }
    
    public void setRarete(String rarete) {
        this.rarete = rarete;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    
    public int getPlayerPosition() {
        return playerPosition;
    }
    
    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = playerPosition;
    }
    
    // New getters and setters for battle properties
    public Long getProprietaireId() {
        return proprietaireId;
    }
    
    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public int getPower() {
        return power;
    }
    
    public void setPower(int power) {
        this.power = power;
    }
    
    @Override
    public String toString() {
        return "CardDTO{" +
            "id=" + id +
            ", nom='" + nom + '\'' +
            ", type='" + type + '\'' +
            ", rarete='" + rarete + '\'' +
            ", energy=" + energy +
            ", hp=" + hp +
            ", power=" + power +
            ", playerPosition=" + playerPosition +
            '}';
    }
}
