package com.gp9.carte_MS.service;

import com.gp9.carte_MS.dto.CarteDTO;
import com.gp9.carte_MS.model.Carte;
import com.gp9.carte_MS.model.CardType;
import com.gp9.carte_MS.repository.CarteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Carte Service Tests")
class CarteServiceTest {

    private static final Long TEST_CARD_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private static final int MAX_ENERGY = 100;
    private static final int MIN_ENERGY = 0;

    @Mock
    private CarteRepository carteRepository;

    @InjectMocks
    private CarteService carteService;

    private Carte testCarte;
    private CarteDTO testCarteDTO;

    @BeforeEach
    void setUp() {
        testCarte = new Carte();
        testCarte.setId(TEST_CARD_ID);
        testCarte.setNom("Test Card");
        testCarte.setDescription("Test Description");
        testCarte.setEnergy(100);
        testCarte.setType(CardType.NORMAL); // Use enum instead of String
        testCarte.setRarete("COMMUN");
        testCarte.setProprietaireId(TEST_USER_ID);

        testCarteDTO = new CarteDTO(testCarte);
    }

    @Nested
    @DisplayName("Card Creation Tests")
    class CardCreationTests {
        @Test
        @DisplayName("Should create new card successfully")
        void createCarte_ShouldCreateSuccessfully() {
            when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);
            
            CarteDTO result = carteService.createCarte(testCarteDTO);
            
            assertNotNull(result);
            assertEquals(testCarteDTO.getNom(), result.getNom());
            verify(carteRepository).save(any(Carte.class));
        }
    }

    @Nested
    @DisplayName("Card Retrieval Tests")
    class CardRetrievalTests {
        @Test
        @DisplayName("Should retrieve card by ID")
        void getCarteById_ShouldReturnCard() {
            when(carteRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCarte));
            
            Optional<CarteDTO> result = carteService.getCarteById(TEST_CARD_ID);
            
            assertTrue(result.isPresent());
            assertEquals(testCarte.getNom(), result.get().getNom());
        }

        @Test
        @DisplayName("Should return empty when card not found")
        void getCarteById_ShouldReturnEmpty_WhenNotFound() {
            when(carteRepository.findById(TEST_CARD_ID)).thenReturn(Optional.empty());
            
            Optional<CarteDTO> result = carteService.getCarteById(TEST_CARD_ID);
            
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Card Energy Management Tests")
    class CardEnergyTests {
        @Test
        @DisplayName("Should update card energy within bounds")
        void updateCardEnergy_ShouldUpdateSuccessfully() {
            testCarte.setEnergy(50);
            when(carteRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCarte));
            when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);
            
            CarteDTO result = carteService.updateCardEnergy(TEST_CARD_ID, 20);
            
            assertNotNull(result);
            assertEquals(70, result.getEnergy());
        }

        @Test
        @DisplayName("Should not exceed maximum energy")
        void updateCardEnergy_ShouldNotExceedMax() {
            testCarte.setEnergy(90);
            when(carteRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCarte));
            when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);
            
            CarteDTO result = carteService.updateCardEnergy(TEST_CARD_ID, 20);
            
            assertNotNull(result);
            assertEquals(MAX_ENERGY, result.getEnergy());
        }

        @Test
        @DisplayName("Should not go below minimum energy")
        void updateCardEnergy_ShouldNotGoBelowMin() {
            testCarte.setEnergy(10);
            when(carteRepository.findById(TEST_CARD_ID)).thenReturn(Optional.of(testCarte));
            when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);
            
            CarteDTO result = carteService.updateCardEnergy(TEST_CARD_ID, -20);
            
            assertNotNull(result);
            assertEquals(MIN_ENERGY, result.getEnergy());
        }
    }

    @Test
    void regenerateEnergyProgressive_ShouldRegenerateCorrectly() {
        testCarte.setEnergy(30); // Low energy
        List<Carte> lowEnergyCards = Arrays.asList(testCarte);
        when(carteRepository.findByEnergyLessThan(100)).thenReturn(lowEnergyCards);
        when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);
        
        int updatedCount = carteService.regenerateEnergyProgressive(5, 10, 15);
        
        assertEquals(1, updatedCount);
        verify(carteRepository).save(any(Carte.class));
    }

    @Test
    void getAllCartes_ShouldReturnList() {
        when(carteRepository.findAll()).thenReturn(Arrays.asList(testCarte));
        
        List<CarteDTO> results = carteService.getAllCartes();
        
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void updateCarte_WhenExists_ShouldReturnUpdatedDTO() {
        // Given
        Long id = 1L;
        CarteDTO carteDTO = new CarteDTO();
        Carte existingCarte = new Carte();
        when(carteRepository.findById(id)).thenReturn(Optional.of(existingCarte));
        when(carteRepository.save(any(Carte.class))).thenReturn(existingCarte);

        // When
        CarteDTO result = carteService.updateCarte(id, carteDTO);

        // Then
        assertNotNull(result);
        verify(carteRepository).save(any(Carte.class));
    }

    @Test
    void regenerateAllCardsEnergy_ShouldUpdateAllCards() {
        // Given
        List<Carte> cards = Arrays.asList(new Carte(), new Carte());
        when(carteRepository.findByEnergyLessThan(anyInt())).thenReturn(cards);
        when(carteRepository.save(any(Carte.class))).thenReturn(new Carte());

        // When
        int result = carteService.regenerateAllCardsEnergy(10);

        // Then
        assertEquals(2, result);
        verify(carteRepository, times(2)).save(any(Carte.class));
    }

    @Test
    void regenerateEnergyProgressive_ShouldHandleAllCases() {
        // Given 
        testCarte.setEnergy(50);
        when(carteRepository.findByEnergyLessThan(anyInt())).thenReturn(Arrays.asList(testCarte));
        when(carteRepository.save(any(Carte.class))).thenReturn(testCarte);

        // When/Then
        assertEquals(1, carteService.regenerateEnergyProgressive(50, 10, 100));
    }

    @Test
    void updateCardEnergy_ShouldHandleValidations() {
        // Given
        Long cardId = 1L;
        Carte carte = new Carte();
        carte.setEnergy(50);
        when(carteRepository.findById(cardId)).thenReturn(Optional.of(carte));
        when(carteRepository.save(any(Carte.class))).thenReturn(carte);

        // When/Then
        CarteDTO result = carteService.updateCardEnergy(cardId, 20);
        assertNotNull(result);
        assertEquals(70, carte.getEnergy());
    }
}
