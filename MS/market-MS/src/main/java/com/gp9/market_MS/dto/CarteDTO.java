package com.gp9.market_MS.dto;

public class CarteDTO {
    private Long id;
    private String nom;
    private double prix;
    private boolean aVendre;
    private Long proprietaireId;

    // Default constructor
    public CarteDTO() {
    }

    // Getters and Setters
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isAVendre() {
        return aVendre;
    }

    public void setAVendre(boolean aVendre) {
        this.aVendre = aVendre;
    }

    public Long getProprietaireId() {
        return proprietaireId;
    }

    public void setProprietaireId(Long proprietaireId) {
        this.proprietaireId = proprietaireId;
    }
}
