package com.gp9.auth_MS.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET = "testsecrettestsecrettestsecrettestsecret";
    private static final Long EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", EXPIRATION);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String username = "testuser";
        Long userId = 1L;
        String role = "USER";

        // When
        String token = jwtUtil.generateToken(username, userId, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username, 1L, "USER");

        // When
        boolean isValid = jwtUtil.validateToken(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidUsername() {
        // Given
        String token = jwtUtil.generateToken("testuser", 1L, "USER");

        // When
        boolean isValid = jwtUtil.validateToken(token, "wronguser");

        // Then
        assertFalse(isValid);
    }
}
