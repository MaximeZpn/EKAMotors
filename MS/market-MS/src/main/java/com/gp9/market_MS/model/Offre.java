package com.gp9.market_MS.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "offres")
public class Offre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long carteId;
    
    @Column(nullable = false)
    private Long vendeurId;
    
    @Column(nullable = false)
    private double prix;
    
    @Column(nullable = false)
    private LocalDateTime datePublication;
    
    @Column(nullable = false)
    private boolean active;
    
    // Default constructor
    public Offre() {
        this.datePublication = LocalDateTime.now();
        this.active = true;
    }
    
    // Constructor with fields
    public Offre(Long carteId, Long vendeurId, double prix) {
        this.carteId = carteId;
        this.vendeurId = vendeurId;
        this.prix = prix;
        this.datePublication = LocalDateTime.now();
        this.active = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCarteId() {
        return carteId;
    }
    
    public void setCarteId(Long carteId) {
        this.carteId = carteId;
    }
    
    public Long getVendeurId() {
        return vendeurId;
    }
    
    public void setVendeurId(Long vendeurId) {
        this.vendeurId = vendeurId;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public LocalDateTime getDatePublication() {
        return datePublication;
    }
    
    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
