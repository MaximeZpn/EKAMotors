package com.gp9.carte_MS.dto;

import com.gp9.carte_MS.model.CardType;
import com.gp9.carte_MS.model.Carte;
import java.time.LocalDateTime;

public class CarteDTO {
    private Long id;
    private String nom;
    private String description;
    private String imageUrl;
    private Long familleId;
    private String rarete;
    private boolean aVendre;
    private double prix;
    private Long proprietaireId;
    private int energy;
    private LocalDateTime lastEnergyRegen;
    private CardType type;

    // Default constructor
    public CarteDTO() {
    }

    // Constructor from entity
    public CarteDTO(Carte carte) {
        this.id = carte.getId();
        this.nom = carte.getNom();
        this.description = carte.getDescription();
        this.imageUrl = carte.getImageUrl();
        this.familleId = carte.getFamilleId();
        this.rarete = carte.getRarete();
        this.aVendre = carte.isAVendre();
        this.prix = carte.getPrix();
        this.proprietaireId = carte.getProprietaireId();
        this.energy = carte.getEnergy();
        this.lastEnergyRegen = carte.getLastEnergyRegen();
        this.type = carte.getType();
    }

    // Convert to entity
    public Carte toEntity() {
        Carte carte = new Carte();
        carte.setId(this.id);
        carte.setNom(this.nom);
        carte.setDescription(this.description);
        carte.setFamilleId(this.familleId);
        carte.setRarete(this.rarete);
        carte.setAVendre(this.aVendre);
        carte.setPrix(this.prix);
        carte.setProprietaireId(this.proprietaireId);
        carte.setEnergy(this.energy);
        carte.setLastEnergyRegen(this.lastEnergyRegen);
        carte.setType(this.type);
        return carte;
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
