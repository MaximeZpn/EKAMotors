package com.gp9.market_MS.dto;

import com.gp9.market_MS.model.Transaction;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long carteId;
    private Long vendeurId;
    private Long acheteurId;
    private double prix;
    private LocalDateTime dateTransaction;
    
    // Default constructor
    public TransactionDTO() {
    }
    
    // Constructor from entity
    public TransactionDTO(Transaction transaction) {
        if (transaction != null) {
            this.id = transaction.getId();
            this.carteId = transaction.getCarteId();
            this.vendeurId = transaction.getVendeurId();
            this.acheteurId = transaction.getAcheteurId();
            this.prix = transaction.getPrix();
            this.dateTransaction = transaction.getDateTransaction();
        } else {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
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
