package com.gp9.atelier2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test-roles")
public class TestRolesController {

    @GetMapping("/public")
    public ResponseEntity<?> testPublic() {
        return ResponseEntity.ok(Map.of("message", "Cet endpoint est accessible à tous"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> testUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint USER accessible");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint ADMIN accessible");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vendeur")
    @PreAuthorize("hasRole('VENDEUR')")
    public ResponseEntity<?> testVendeur() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint VENDEUR accessible");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-or-admin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> testUserOrAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint USER ou ADMIN accessible");
        response.put("username", auth.getName());
        response.put("roles", auth.getAuthorities());
        return ResponseEntity.ok(response);
    }
}
