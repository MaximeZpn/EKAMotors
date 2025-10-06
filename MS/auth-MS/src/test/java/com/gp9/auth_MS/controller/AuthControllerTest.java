package com.gp9.auth_MS.controller;

import com.gp9.auth_MS.model.AuthRequest;
import com.gp9.auth_MS.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private RestTemplate restTemplate;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService, restTemplate);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void index_ShouldRedirectToConnexion() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/connexion.html"));
    }

    @Test
    void inscriptionPage_ShouldReturnInscription() throws Exception {
        mockMvc.perform(get("/inscription.html"))
               .andExpect(status().isOk())
               .andExpect(view().name("inscription"));
    }

    @Test
    void connexionPage_ShouldReturnConnexion() throws Exception {
        mockMvc.perform(get("/connexion.html"))
               .andExpect(status().isOk())
               .andExpect(view().name("connexion"));
    }

    @Test
    void health_ShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
               .andExpect(status().isOk())
               .andExpect(content().string("Auth service is up and running!"));
    }

    @Test
    void getElementDescription_ShouldReturnCorrectDescriptions() {
        assertEquals("des flammes éternelles", authController.getElementDescription("FIRE"));
        assertEquals("des océans profonds", authController.getElementDescription("WATER"));
        assertEquals("de la terre ancestrale", authController.getElementDescription("EARTH"));
        assertEquals("des vents célestes", authController.getElementDescription("AIR"));
        assertEquals("des forces primordiales", authController.getElementDescription("UNKNOWN"));
    }
}