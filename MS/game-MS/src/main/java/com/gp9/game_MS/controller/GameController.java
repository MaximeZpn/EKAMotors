package com.gp9.game_MS.controller;

import com.gp9.game_MS.dto.CreateRoomDTO;
import com.gp9.game_MS.dto.JoinRoomDTO;
import com.gp9.game_MS.dto.RoomDTO;
import com.gp9.game_MS.exception.EntityNotFoundException;
import com.gp9.game_MS.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final RoomService roomService;

    @Autowired
    public GameController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomDTO createRoomDTO) {
        try {
            System.out.println("=== CREATE ROOM REQUEST ===");
            System.out.println("Name: " + createRoomDTO.getRoom_name());
            System.out.println("Bet: " + createRoomDTO.getBet_amount());
            System.out.println("Created by: " + createRoomDTO.getCreated_by());
            System.out.println("Card ID: " + createRoomDTO.getCard_id());
            
            RoomDTO roomDTO = roomService.createRoom(createRoomDTO);
            return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la création de la room: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<?> joinRoom(@PathVariable Long roomId, @Valid @RequestBody JoinRoomDTO joinRoomDTO) {
        try {
            System.out.println("=== JOIN ROOM REQUEST ===");
            System.out.println("Room ID: " + roomId);
            System.out.println("Player ID: " + joinRoomDTO.getPlayer_id());
            System.out.println("Card ID: " + joinRoomDTO.getCard_id());
            
            // Check if player already joined the room
            boolean alreadyJoined = roomService.hasPlayerJoined(roomId, joinRoomDTO.getPlayer_id());
            if (alreadyJoined) {
                return ResponseEntity.badRequest().body("Vous avez déjà rejoint cette salle");
            }
            
            // Check if room exists
            boolean roomExists = roomService.roomExists(roomId);
            if (!roomExists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La salle n'existe pas");
            }
            
            // Check if room is available
            String roomStatus = roomService.getRoomStatus(roomId);
            if (!"WAITING".equals(roomStatus)) {
                return ResponseEntity.badRequest().body("Cette salle n'est pas disponible (statut: " + roomStatus + ")");
            }
            
            RoomDTO roomDTO = roomService.joinRoom(roomId, joinRoomDTO);
            return ResponseEntity.ok(roomDTO);
        } catch (IllegalArgumentException e) {
            System.err.println("Bad request error joining room: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            System.err.println("Room not found: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error joining room: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de l'accès à la room: " + e.getMessage(), 
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getAvailableRooms() {
        List<RoomDTO> rooms = roomService.getAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoomById(@PathVariable Long roomId) {
        try {
            RoomDTO roomDTO = roomService.getRoomById(roomId);
            return ResponseEntity.ok(roomDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la récupération de la room: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Add this new endpoint to retrieve player's games
    @GetMapping("/rooms/player/{playerId}")
    public ResponseEntity<List<RoomDTO>> getRoomsByPlayer(@PathVariable Long playerId) {
        try {
            List<RoomDTO> rooms = roomService.getRoomsByPlayer(playerId);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
