package com.gp9.carte_MS.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cartes")
public class Carte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nom;
    
    private String description;
    
    private String imageUrl; // Nouvel attribut pour l'URL de l'image
    
    private Long familleId;
    
    private String rarete;
    
    private boolean aVendre = false;
    
    private double prix;
    
    private Long proprietaireId;
    
    // Add energy attribute with a default value of 100
    private int energy = 100;
    
    // Add last energy regeneration timestamp
    private LocalDateTime lastEnergyRegen = LocalDateTime.now();
    
    // Add card type for battle advantages
    @Enumerated(EnumType.STRING)
    private CardType type = CardType.NORMAL;
    
    // Pre-persist hook to ensure new cards have energy
    @PrePersist
    public void prePersist() {
        if (energy <= 0) {
            energy = 100;
        }
        lastEnergyRegen = LocalDateTime.now();
    }
    
    // Getters and setters
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
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public Long getFamilleId() {
        return familleId;
    }
    
    public void setFamilleId(Long familleId) {
        this.familleId = familleId;
    }
    
    public String getRarete() {
        return rarete;
    }
    
    public void setRarete(String rarete) {
        this.rarete = rarete;
    }
    
    public boolean isAVendre() {
        return aVendre;
    }
    
    public void setAVendre(boolean aVendre) {
        this.aVendre = aVendre;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public Long getProprietaireId() {
        return proprietaireId;
    }
    
    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }
    
    public int getEnergy() {
        return energy;
    }
    
    public void setEnergy(int energy) {
        this.energy = energy;
    }
    
    public LocalDateTime getLastEnergyRegen() {
        return lastEnergyRegen;
    }
    
    public void setLastEnergyRegen(LocalDateTime lastEnergyRegen) {
        this.lastEnergyRegen = lastEnergyRegen;
    }
    
    public CardType getType() {
        return type;
    }
    
    public void setType(CardType type) {
        this.type = type;
    }
}
