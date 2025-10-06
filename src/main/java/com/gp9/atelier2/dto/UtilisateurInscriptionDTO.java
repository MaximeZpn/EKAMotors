package com.gp9.atelier2.dto;

public class UtilisateurInscriptionDTO {
    private String surnom;
    private String email;
    private String motDePasse;

    public UtilisateurInscriptionDTO() {}

    public UtilisateurInscriptionDTO(String surnom, String email, String motDePasse) {
        this.surnom = surnom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public String getSurnom() { return surnom; }
    public void setSurnom(String surnom) { this.surnom = surnom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
