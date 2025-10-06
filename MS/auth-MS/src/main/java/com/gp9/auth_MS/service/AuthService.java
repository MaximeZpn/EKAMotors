package com.gp9.auth_MS.service;

import com.gp9.auth_MS.model.AuthRequest;
import com.gp9.auth_MS.model.AuthResponse;
import com.gp9.auth_MS.model.UserCredential;
import com.gp9.auth_MS.repository.UserCredentialRepository;
import com.gp9.auth_MS.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserCredentialRepository userCredentialRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userCredentialRepository = userCredentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        Optional<UserCredential> user = userCredentialRepository.findByUsername(authRequest.getUsername());
        
        if (user.isPresent() && passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            String token = jwtUtil.generateToken(
                    user.get().getUsername(),
                    user.get().getId(),
                    user.get().getRole()
            );
            
            return new AuthResponse(token, user.get().getId(), user.get().getUsername());
        }
        
        throw new RuntimeException("Invalid username or password");
    }
    
    @Transactional
    public UserCredential register(String username, String password, String role) {
        // Add validation before proceeding
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        System.out.println("AuthService.register(String, String, String) called");
        System.out.println("Username: " + username);
        System.out.println("Password length: " + (password != null ? password.length() : 0));
        System.out.println("Role: " + role);
        
        try {
            if (userCredentialRepository.existsByUsername(username)) {
                System.out.println("Username already exists: " + username);
                throw new RuntimeException("Username already exists");
            }
            
            UserCredential userCredential = new UserCredential();
            userCredential.setUsername(username);
            userCredential.setPassword(passwordEncoder.encode(password));
            userCredential.setRole(role);
            
            UserCredential savedUser = userCredentialRepository.save(userCredential);
            System.out.println("User registered successfully with ID: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error in AuthService.register: " + e.getMessage());
            e.printStackTrace(System.err);
            throw e;
        }
    }
    
    /**
     * Alternative register method that takes an AuthRequest object
     */
    @Transactional
    public UserCredential register(AuthRequest authRequest) {
        System.out.println("AuthService.register(AuthRequest) called with: " + authRequest);
        try {
            return register(
                authRequest.getUsername(),
                authRequest.getPassword(),
                "USER" // Default role
            );
        } catch (Exception e) {
            System.err.println("Error in AuthService.register(AuthRequest): " + e.getMessage());
            e.printStackTrace(System.err);
            throw e;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            Optional<UserCredential> userOpt = userCredentialRepository.findByUsername(username);
            
            if (userOpt.isPresent()) {
                UserCredential user = userOpt.get();
                return jwtUtil.validateToken(token, user.getUsername());
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
