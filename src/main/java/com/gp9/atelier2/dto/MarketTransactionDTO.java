package com.gp9.atelier2.dto;

import com.gp9.atelier2.model.MarketTransaction;
import com.gp9.atelier2.model.Carte;

import java.time.LocalDateTime;

public class MarketTransactionDTO {
    private String acheteur;
    private String vendeur;
    private String carte;
    private double prix;
    private LocalDateTime dateTransaction;

    // Constructeur qui initialise les données du DTO avec les informations de la transaction et de la carte
    public MarketTransactionDTO(MarketTransaction marketTransaction, Carte carte) {
        this.acheteur = marketTransaction.getAcheteur().getSurnom(); // Surnom de l'acheteur
        this.vendeur = marketTransaction.getVendeur().getSurnom();   // Surnom du vendeur
        this.carte = carte.getNom();                                  // Nom de la carte
        this.prix = marketTransaction.getPrix();                      // Prix de la transaction
        this.dateTransaction = marketTransaction.getDateTransaction(); // Date de la transaction
    }

    // Getters et Setters pour accéder aux données
    public String getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(String acheteur) {
        this.acheteur = acheteur;
    }

    public String getVendeur() {
        return vendeur;
    }

    public void setVendeur(String vendeur) {
        this.vendeur = vendeur;
    }

    public String getCarte() {
        return carte;
    }

    public void setCarte(String carte) {
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
