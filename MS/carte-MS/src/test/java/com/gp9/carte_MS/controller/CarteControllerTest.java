package com.gp9.carte_MS.controller;

import com.gp9.carte_MS.dto.CarteDTO;
import com.gp9.carte_MS.service.CarteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class CarteControllerTest {
    
    private CarteController carteController;
    
    @Mock
    private CarteService carteService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        carteController = new CarteController(carteService); // Update constructor if needed
    }

    @Test
    void ping_ShouldReturnPong() {
        ResponseEntity<?> response = carteController.ping();
        assertEquals("Carte service is responding", response.getBody());
    }
    
    @Test
    void createCarte_ShouldReturnCreated() {
        // Given
        CarteDTO carteDTO = new CarteDTO();
        carteDTO.setNom("Test Card");
        when(carteService.createCarte(any(CarteDTO.class))).thenReturn(carteDTO);
        
        // When
        ResponseEntity<?> response = carteController.createCarte(carteDTO);
        
        // Then
        assertEquals(201, response.getStatusCodeValue());
        verify(carteService).createCarte(any(CarteDTO.class));
    }

    @Test
    void getCarteById_WhenExists_ShouldReturnCarte() {
        // Given
        Long id = 1L;
        CarteDTO carteDTO = new CarteDTO();
        when(carteService.getCarteById(id)).thenReturn(java.util.Optional.of(carteDTO));
        
        // When
        ResponseEntity<?> response = carteController.getCarteById(id);
        
        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void getAllCartes_ShouldReturnList() {
        // Given
        List<CarteDTO> cartes = Arrays.asList(new CarteDTO(), new CarteDTO());
        when(carteService.getAllCartes()).thenReturn(cartes);

        // When
        ResponseEntity<?> response = carteController.getAllCartes();

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void updateCardEnergy_ShouldUpdateEnergy() {
        // Given
        Long cardId = 1L;
        Map<String, Integer> energyUpdate = new HashMap<>();
        energyUpdate.put("amount", 20);
        CarteDTO updatedCard = new CarteDTO();
        when(carteService.updateCardEnergy(eq(cardId), eq(20))).thenReturn(updatedCard);

        // When
        ResponseEntity<?> response = carteController.updateCardEnergy(cardId, energyUpdate);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(carteService).updateCardEnergy(eq(cardId), eq(20));
    }

    @Test
    void regenerateAllCardsEnergy_ShouldRegenerateEnergy() {
        // Given
        when(carteService.regenerateAllCardsEnergy(anyInt())).thenReturn(5);

        // When
        ResponseEntity<?> response = carteController.regenerateAllCardsEnergy(15);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(carteService).regenerateAllCardsEnergy(15);
    }

    @Test
    void changeProprietaire_ShouldChangeOwner() {
        // Given
        Long cardId = 1L;
        Long newOwnerId = 2L;
        when(carteService.changeProprietaire(cardId, newOwnerId))
            .thenReturn(Optional.of(new CarteDTO()));

        // When
        ResponseEntity<?> response = carteController.changeProprietaire(cardId, newOwnerId);

        // Then
        assertEquals(200, response.getStatusCodeValue());
    }
}
