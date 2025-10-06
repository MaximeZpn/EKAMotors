package com.gp9.market_MS.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long carteId;
    
    @Column(nullable = false)
    private Long vendeurId;
    
    @Column(nullable = false)
    private Long acheteurId;
    
    @Column(nullable = false)
    private double prix;
    
    @Column(nullable = false)
    private LocalDateTime dateTransaction;
    
    // Default constructor
    public Transaction() {
        this.dateTransaction = LocalDateTime.now();
    }
    
    // Constructor with fields
    public Transaction(Long carteId, Long vendeurId, Long acheteurId, double prix) {
        this.carteId = carteId;
        this.vendeurId = vendeurId;
        this.acheteurId = acheteurId;
        this.prix = prix;
        this.dateTransaction = LocalDateTime.now();
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
    
    public Long getAcheteurId() {
        return acheteurId;
    }
    
    public void setAcheteurId(Long acheteurId) {
        this.acheteurId = acheteurId;
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
