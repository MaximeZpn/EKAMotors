package com.gp9.market_MS.dto;

import com.gp9.market_MS.model.Offre;
import java.time.LocalDateTime;

public class OffreDTO {
    private Long id;
    private Long carteId;
    private Long vendeurId;
    private double prix;
    private LocalDateTime datePublication;
    private boolean active;
    
    // Default constructor
    public OffreDTO() {
    }
    
    // Constructor from entity
    public OffreDTO(Offre offre) {
        this.id = offre.getId();
        this.carteId = offre.getCarteId();
        this.vendeurId = offre.getVendeurId();
        this.prix = offre.getPrix();
        this.datePublication = offre.getDatePublication();
        this.active = offre.isActive();
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
    
    @Override
    public String toString() {
        return "OffreDTO{" +
                "id=" + id +
                ", carteId=" + carteId +
                ", vendeurId=" + vendeurId +
                ", prix=" + prix +
                ", datePublication=" + datePublication +
                ", active=" + active +
                '}';
    }
}
