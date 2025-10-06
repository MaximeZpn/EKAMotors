package com.gp9.auth_MS.model;

public class AuthRequest {
    private String username;
    private String password;
    private String email;
    
    // Default constructor
    public AuthRequest() {
    }
    
    // Constructor with fields
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public AuthRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "AuthRequest{" +
                "username='" + username + '\'' +
                ", email='" + (email != null ? email : "null") + '\'' +
                ", password='" + (password != null ? "[PROTECTED]" : "null") + '\'' +
                '}';
    }
}
