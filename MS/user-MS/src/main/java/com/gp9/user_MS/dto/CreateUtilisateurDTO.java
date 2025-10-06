package com.gp9.user_MS.dto;

public class CreateUtilisateurDTO {
    private String username;
    private String email;
    
    // Default constructor
    public CreateUtilisateurDTO() {
    }
    
    // Constructor with fields
    public CreateUtilisateurDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "CreateUtilisateurDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
