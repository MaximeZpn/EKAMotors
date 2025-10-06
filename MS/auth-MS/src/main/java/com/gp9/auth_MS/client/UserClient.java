package com.gp9.auth_MS.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    @Autowired
    public UserClient(RestTemplate restTemplate,
                      @Value("${services.user-service.url:http://user-service:8082}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public boolean verifyUserExists(String username) {
        try {
            String url = userServiceUrl + "/api/utilisateurs/exists/" + username;
            return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
        } catch (Exception e) {
            // Log error
            System.err.println("Error verifying user: " + e.getMessage());
            return false;
        }
    }

    public Long createUser(String username, String email) {
        try {
            String url = userServiceUrl + "/api/utilisateurs/";
            
            // Create a simple DTO to send user info
            UserCreateDTO userDTO = new UserCreateDTO(username, email);
            
            Long userId = restTemplate.postForObject(url, userDTO, Long.class);
            return userId;
        } catch (Exception e) {
            // Log error
            System.err.println("Error creating user: " + e.getMessage());
            return null;
        }
    }
    
    // Inner class for user creation
    public static class UserCreateDTO {
        private String username;
        private String email;
        
        public UserCreateDTO() {}
        
        public UserCreateDTO(String username, String email) {
            this.username = username;
            this.email = email;
        }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
