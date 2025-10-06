package com.gp9.game_MS.service;

import com.gp9.game_MS.dto.CreateRoomDTO;
import com.gp9.game_MS.dto.JoinRoomDTO;
import com.gp9.game_MS.dto.RoomDTO;
import com.gp9.game_MS.exception.EntityNotFoundException;
import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    
    private final RoomRepository roomRepository;
    private final CardService cardService;
    private final UserService userService;
    private final BattleService battleService;

    @Autowired
    public RoomService(RoomRepository roomRepository, 
                      CardService cardService,
                      UserService userService,
                      BattleService battleService) {
        this.roomRepository = roomRepository;
        this.cardService = cardService;
        this.userService = userService;
        this.battleService = battleService;
    }

    @Transactional
    public RoomDTO createRoom(CreateRoomDTO dto) {
        // Check if user has enough balance for the bet
        if (!userService.hasEnoughBalance(dto.getCreated_by(), dto.getBet_amount())) {
            throw new IllegalArgumentException("Solde insuffisant pour créer cette room");
        }

        // Deduct bet amount from user's balance
        if (!userService.deductBalance(dto.getCreated_by(), dto.getBet_amount())) {
            throw new IllegalArgumentException("Erreur lors du retrait de la mise");
        }

        Room room = new Room();
        room.setName(dto.getRoom_name());
        room.setBetAmount(dto.getBet_amount());
        room.setCreatedBy(dto.getCreated_by());
        room.setPlayer1Id(dto.getCreated_by());
        room.setStatus(RoomStatus.WAITING);
        
        // Add the card ID from the DTO for player 1
        if (dto.getCard_id() != null) {
            room.setCard1Id(dto.getCard_id());
            
            // Log card assignment
            logger.info("Room creation: assigning card {} to player 1 ({})", 
                dto.getCard_id(), dto.getCreated_by());
        }

        Room savedRoom = roomRepository.save(room);
        
        // Register the card for battle
        if (dto.getCard_id() != null) {
            try {
                cardService.registerCardForBattle(savedRoom.getId(), dto.getCard_id(), 1);
            } catch (Exception e) {
                logger.error("Failed to register card during room creation: {}", e.getMessage());
            }
        }
        
        return new RoomDTO(savedRoom);
    }

    @Transactional
    public RoomDTO joinRoom(Long roomId, JoinRoomDTO dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room", roomId));

        // Check if room is joinable
        if (!room.canJoin()) {
            throw new IllegalArgumentException("La room n'est pas disponible");
        }

        // Vérifier que le joueur a assez d'argent pour la mise
        if (!userService.hasEnoughBalance(dto.getPlayer_id(), room.getBetAmount())) {
            throw new IllegalArgumentException("Solde insuffisant pour rejoindre cette room");
        }

        // Déduire la mise du solde du joueur
        if (!userService.deductBalance(dto.getPlayer_id(), room.getBetAmount())) {
            throw new IllegalArgumentException("Erreur lors du retrait de la mise");
        }

        // Check if card exists, belongs to the player, and has enough energy
        if (!cardService.isCardPlayable(dto.getCard_id(), dto.getPlayer_id())) {
            throw new IllegalArgumentException("La carte n'est pas jouable");
        }

        // Add player to room
        room.setPlayer2Id(dto.getPlayer_id());
        
        // Add the card ID from the DTO for player 2
        if (dto.getCard_id() != null) {
            room.setCard2Id(dto.getCard_id());
            
            // Log card assignment
            logger.info("Room join: assigning card {} to player 2 ({})", 
                dto.getCard_id(), dto.getPlayer_id());
        }

        // Register the card for this player
        if (dto.getCard_id() != null) {
            try {
                cardService.registerCardForBattle(roomId, dto.getCard_id(), 2);
            } catch (Exception e) {
                logger.error("Failed to register card when joining room: {}", e.getMessage());
            }
        }

        // Check if the room is ready to start a battle
        if (room.isReadyToStart()) {
            room.setStatus(RoomStatus.IN_PROGRESS);
            room.setStartedAt(LocalDateTime.now());
            Room savedRoom = roomRepository.save(room);
            
            // Start the battle asynchronously
            battleService.startBattle(roomId);
            
            return new RoomDTO(savedRoom);
        }

        Room savedRoom = roomRepository.save(room);
        return new RoomDTO(savedRoom);
    }

    public List<RoomDTO> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.WAITING).stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room", id));
        return new RoomDTO(room);
    }

    @Transactional
    public void finishRoom(Long roomId, Long winnerId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room", roomId));
        
        room.setStatus(RoomStatus.FINISHED);
        room.setWinnerId(winnerId);
        room.setFinishedAt(LocalDateTime.now());
        
        // Distribute rewards
        if (winnerId != null) {
            userService.addBalance(winnerId, room.getBetAmount()); // Winner gets the bet amount
            double prize = room.getBetAmount() * 2; // Double the bet amount
            userService.addBalance(winnerId, prize);
        }
        
        roomRepository.save(room);
    }

    public List<RoomDTO> getRoomsByPlayer(Long playerId) {
        return roomRepository.findByPlayer1IdOrPlayer2Id(playerId, playerId).stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Check if a player has already joined a room
     */
    public boolean hasPlayerJoined(Long roomId, Long playerId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        
        if (room == null) {
            return false;
        }
        
        // Check if player is creator
        if (room.getCreatedBy() != null && room.getCreatedBy().equals(playerId)) {
            return true;
        }
        
        // Check if player is second player
        if (room.getPlayer2Id() != null && room.getPlayer2Id().equals(playerId)) {
            return true;
        }
        
        return false;
    }

    /**
     * Check if a room exists
     */
    public boolean roomExists(Long roomId) {
        return roomRepository.existsById(roomId);
    }

    /**
     * Get room status
     */
    public String getRoomStatus(Long roomId) {
        return roomRepository.findById(roomId)
            .map(room -> room.getStatus().toString())
            .orElse("NOT_FOUND");
    }
}
