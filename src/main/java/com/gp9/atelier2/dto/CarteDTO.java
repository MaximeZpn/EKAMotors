package com.gp9.atelier2.dto;

import com.gp9.atelier2.model.Carte;

public class CarteDTO {
    private Long id;
    private String nom;
    private String description;
    private String imageUrl;
    private double prix;
    private int niveau;
    private String rarete;
    private boolean aVendre;
    private String proprietaire; // On ne renvoie que le surnom du propriétaire

    public CarteDTO(Carte carte) {
        this.id = carte.getId();
        this.nom = carte.getNom();
        this.description = carte.getDescription();
        this.imageUrl = carte.getImageUrl();
        this.prix = carte.getPrix();
        this.niveau = carte.getNiveau();
        this.rarete = carte.getRarete();
        this.aVendre = carte.isAVendre();
        this.proprietaire = (carte.getProprietaire() != null) ? carte.getProprietaire().getSurnom() : "Aucun";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }

    public String getRarete() { return rarete; }
    public void setRarete(String rarete) { this.rarete = rarete; }

    public boolean isAVendre() { return aVendre; }
    public void setAVendre(boolean aVendre) { this.aVendre = aVendre; }

    public String getProprietaire() { return proprietaire; }
    public void setProprietaire(String proprietaire) { this.proprietaire = proprietaire; }
}
