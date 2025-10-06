package com.gp9.auth_MS.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtTokenProvider.generateToken("testUser");
        assertNotNull(token);
        assertTrue(token.contains(".")); // JWT tokens contain at least one dot
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Given - Generate a valid token
        String token = jwtTokenProvider.generateToken("testUser");
        
        // When - Validate the token
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        // Then - Token should be valid
        assertTrue(isValid, "A freshly generated token should be valid");
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.format"));
        assertFalse(jwtTokenProvider.validateToken(null));
        assertFalse(jwtTokenProvider.validateToken(""));
    }
}
