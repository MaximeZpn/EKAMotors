package com.gp9.user_MS.dto;

import com.gp9.user_MS.model.Utilisateur;

public class UtilisateurDTO {
    private Long id;
    private String username;
    private String email;
    private double solde;
    
    // Default constructor
    public UtilisateurDTO() {
    }
    
    // Constructor from entity
    public UtilisateurDTO(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.username = utilisateur.getUsername();
        this.email = utilisateur.getEmail();
        this.solde = utilisateur.getSolde();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public double getSolde() {
        return solde;
    }
    
    public void setSolde(double solde) {
        this.solde = solde;
    }
    
    @Override
    public String toString() {
        return "UtilisateurDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", solde=" + solde +
                '}';
    }
}
