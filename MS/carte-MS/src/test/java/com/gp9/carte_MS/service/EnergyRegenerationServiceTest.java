package com.gp9.carte_MS.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyRegenerationServiceTest {

    @Mock
    private CarteService carteService;
    
    private EnergyRegenerationService energyRegenerationService;
    
    @BeforeEach
    void setUp() {
        energyRegenerationService = new EnergyRegenerationService(carteService);
        ReflectionTestUtils.setField(energyRegenerationService, "smallRegen", 5);
        ReflectionTestUtils.setField(energyRegenerationService, "mediumRegen", 10);
        ReflectionTestUtils.setField(energyRegenerationService, "largeRegen", 15);
    }

    @Test
    void regenerateEnergy_ShouldCallCarteService() {
        // Given
        when(carteService.regenerateEnergyProgressive(5, 10, 15)).thenReturn(5);

        // When
        energyRegenerationService.regenerateEnergy();
        
        // Then
        verify(carteService).regenerateEnergyProgressive(5, 10, 15);
    }
}
