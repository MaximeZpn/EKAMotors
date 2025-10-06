package com.gp9.auth_MS.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestDTOTest {

    @Test
    void testLoginRequestDTO() {
        LoginRequestDTO dto = new LoginRequestDTO();
        
        dto.setUsername("testUser");
        assertEquals("testUser", dto.getUsername());
        
        dto.setPassword("testPass");
        assertEquals("testPass", dto.getPassword());
    }
}
