package com.gp9.atelier2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carte")
public class Carte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String imageUrl;
    private double prix;
    private int niveau;
    private String rarete;
    private boolean aVendre = false;

    @ManyToOne
    @JoinColumn(name = "proprietaire_id")
    private Utilisateur proprietaire;

    public Carte() {}

    public Carte(String nom, String description, String imageUrl, double prix, int niveau, String rarete, Utilisateur proprietaire) {
        this.nom = nom;
        this.description = description;
        this.imageUrl = imageUrl;
        this.prix = prix;
        this.niveau = niveau;
        this.rarete = rarete;
        this.proprietaire = proprietaire;
        this.aVendre = false;
    }

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

    public Utilisateur getProprietaire() { return proprietaire; }
    public void setProprietaire(Utilisateur proprietaire) { this.proprietaire = proprietaire; }
}
