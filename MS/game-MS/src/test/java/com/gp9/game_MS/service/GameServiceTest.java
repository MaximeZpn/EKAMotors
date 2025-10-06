package com.gp9.game_MS.service;

import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.RoomRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Game Service Tests")
class GameServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private GameService gameService;

    private Room testRoom;
    private static final Long PLAYER_ID = 1L;
    private static final Long CARD_ID = 1L;

    @BeforeEach
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setStatus(RoomStatus.WAITING);
        testRoom.setPlayer1Id(PLAYER_ID);
    }

    @Nested
    @DisplayName("Room Management Tests")
    class RoomTests {
        @Test
        void createRoom_ShouldCreateSuccessfully() {
            when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

            Room result = gameService.createRoom(PLAYER_ID);

            assertNotNull(result);
            assertEquals(RoomStatus.WAITING, result.getStatus());
            assertEquals(PLAYER_ID, result.getPlayer1Id());
            verify(roomRepository).save(any(Room.class));
        }

        @Test
        void joinRoom_ShouldUpdateRoomStatus() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
            when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

            Room result = gameService.joinRoom(1L, 2L);

            assertEquals(RoomStatus.IN_PROGRESS, result.getStatus());
            assertEquals(2L, result.getPlayer2Id());
            verify(roomRepository).save(any(Room.class));
        }
    }

    @Nested
    @DisplayName("Game Logic Tests")
    class GameLogicTests {
        @Test
        void playCard_ShouldSucceed() {
            when(cardService.isCardPlayable(CARD_ID, PLAYER_ID)).thenReturn(true);

            boolean result = gameService.playCard(1L, PLAYER_ID, CARD_ID);

            assertTrue(result);
            verify(cardService).updateCardEnergy(CARD_ID, -20);
        }

        @Test
        void playCard_ShouldFailWithInvalidCard() {
            when(cardService.isCardPlayable(CARD_ID, PLAYER_ID)).thenReturn(false);

            boolean result = gameService.playCard(1L, PLAYER_ID, CARD_ID);

            assertFalse(result);
            verify(cardService, never()).updateCardEnergy(anyLong(), anyInt());
        }
    }
}
