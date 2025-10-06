package com.gp9.auth_MS.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void testAuthRequestConstructor() {
        // Given
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        // When
        AuthRequest request = new AuthRequest(username, password, email);

        // Then
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(email, request.getEmail());
    }

    @Test
    void testAuthRequestSettersAndGetters() {
        // Given
        AuthRequest request = new AuthRequest();
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";

        // When
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);

        // Then
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(email, request.getEmail());
    }

    @Test
    void testToString() {
        // Given
        AuthRequest request = new AuthRequest("testuser", "password", "test@example.com");

        // When
        String result = request.toString();

        // Then
        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("[PROTECTED]")); // Password should be hidden
        assertTrue(result.contains("test@example.com"));
    }
}
