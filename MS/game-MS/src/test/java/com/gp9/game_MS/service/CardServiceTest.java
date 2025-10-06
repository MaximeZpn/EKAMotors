package com.gp9.game_MS.service;

import com.gp9.game_MS.client.CarteClient;
import com.gp9.game_MS.dto.CardDTO;
import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private CarteClient carteClient;

    @InjectMocks
    private CardService cardService;

    private CardDTO testCard;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testCard = new CardDTO();
        testCard.setId(1L);
        testCard.setNom("Test Card");
        testCard.setEnergy(100);
        testCard.setType("NORMAL");
        testCard.setProprietaireId(1L);

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setStatus(RoomStatus.WAITING);  // Use the enum properly
    }

    @Test
    void registerCardForBattle_ShouldWork() {
        Room room = new Room();
        room.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        
        cardService.registerCardForBattle(1L, 1L, 1);
        
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void isCardPlayable_ShouldReturnTrue() {
        // Given
        when(carteClient.getCardById(1L)).thenReturn(testCard);
        
        // When
        boolean result = cardService.isCardPlayable(1L, 1L);
        
        // Then
        verify(carteClient).getCardById(1L);
        assertTrue(result);
    }

    @Test
    void isCardPlayable_ShouldReturnFalse_WhenInsufficientEnergy() {
        // Given
        testCard.setEnergy(10); // Below MIN_ENERGY_REQUIRED
        when(carteClient.getCardById(1L)).thenReturn(testCard);
        
        // When
        boolean result = cardService.isCardPlayable(1L, 1L);
        
        // Then
        verify(carteClient).getCardById(1L);
        assertFalse(result);
    }

    @Test
    void isCardPlayable_ShouldReturnFalse_WhenCardNotFound() {
        when(carteClient.getCardById(1L)).thenReturn(null);
        
        boolean result = cardService.isCardPlayable(1L, 1L);
        
        assertFalse(result);
        verify(carteClient).getCardById(1L);
    }

    @Test
    void isCardPlayable_ShouldReturnFalse_WhenWrongOwner() {
        testCard.setProprietaireId(2L); // Different owner
        when(carteClient.getCardById(1L)).thenReturn(testCard);
        
        boolean result = cardService.isCardPlayable(1L, 1L);
        
        assertFalse(result);
        verify(carteClient).getCardById(1L);
    }
}
