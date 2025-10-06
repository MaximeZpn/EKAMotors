package com.gp9.atelier2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MarketTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur acheteur;

    @ManyToOne
    private Utilisateur vendeur;

    @ManyToOne
    private Carte carte;

    private double prix;
    private LocalDateTime dateTransaction;

    public MarketTransaction() {}

    public MarketTransaction(Utilisateur acheteur, Utilisateur vendeur, Carte carte, double prix) {
        this.acheteur = acheteur;
        this.vendeur = vendeur;
        this.carte = carte;
        this.prix = prix;
        this.dateTransaction = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(Utilisateur acheteur) {
        this.acheteur = acheteur;
    }

    public Utilisateur getVendeur() {
        return vendeur;
    }

    public void setVendeur(Utilisateur vendeur) {
        this.vendeur = vendeur;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
}
