package com.gp9.atelier2.dto;

public class UtilisateurDTO {
    private Long id;
    private String surnom;
    private String email;
    private double solde;

    public UtilisateurDTO(Long id, String surnom, String email, double solde) {
        this.id = id;
        this.surnom = surnom;
        this.email = email;
        this.solde = solde;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSurnom() { return surnom; }
    public void setSurnom(String surnom) { this.surnom = surnom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }
}
