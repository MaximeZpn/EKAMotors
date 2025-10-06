package com.gp9.game_MS.service;

import com.gp9.game_MS.dto.CreateRoomDTO;
import com.gp9.game_MS.dto.RoomDTO;
import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    
    @Mock
    private CardService cardService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private BattleService battleService;

    @InjectMocks
    private RoomService roomService;

    private CreateRoomDTO createRoomDTO;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        createRoomDTO = new CreateRoomDTO();
        createRoomDTO.setRoom_name("Test Room");
        createRoomDTO.setBet_amount(100.0);
        createRoomDTO.setCreated_by(1L);
        createRoomDTO.setCard_id(1L);

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setName("Test Room");
        testRoom.setBetAmount(100.0);
        testRoom.setCreatedBy(1L);
        testRoom.setStatus(RoomStatus.WAITING);
    }

    @Test
    void createRoom_ShouldCreateSuccessfully() {
        when(userService.hasEnoughBalance(eq(1L), eq(100.0))).thenReturn(true);
        when(userService.deductBalance(eq(1L), eq(100.0))).thenReturn(true);
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);
        
        RoomDTO result = roomService.createRoom(createRoomDTO);
        
        assertNotNull(result);
        assertEquals("Test Room", result.getName());
        assertEquals(RoomStatus.WAITING, result.getStatus());
    }

    @Test
    void createRoom_ShouldThrowException_WhenInsufficientBalance() {
        when(userService.hasEnoughBalance(eq(1L), eq(100.0))).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            roomService.createRoom(createRoomDTO);
        });
    }
}
