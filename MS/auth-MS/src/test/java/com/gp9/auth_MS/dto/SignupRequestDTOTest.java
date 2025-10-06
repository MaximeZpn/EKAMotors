package com.gp9.auth_MS.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SignupRequestDTOTest {

    @Test
    void testSignupRequestDTO() {
        SignupRequestDTO dto = new SignupRequestDTO();
        
        dto.setUsername("testUser");
        assertEquals("testUser", dto.getUsername());
        
        dto.setEmail("test@test.com");
        assertEquals("test@test.com", dto.getEmail());
        
        dto.setPassword("testPass");
        assertEquals("testPass", dto.getPassword());
    }
}
