package com.gp9.game_MS.service;

import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import com.gp9.game_MS.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final RoomRepository roomRepository;
    private final CardService cardService;

    @Autowired
    public GameService(RoomRepository roomRepository, CardService cardService) {
        this.roomRepository = roomRepository;
        this.cardService = cardService;
    }

    @Transactional
    public Room createRoom(Long playerId) {
        Room room = new Room();
        room.setPlayer1Id(playerId);
        room.setStatus(RoomStatus.WAITING);
        return roomRepository.save(room);
    }

    @Transactional
    public Room joinRoom(Long roomId, Long playerId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));
            
        if (room.getStatus() != RoomStatus.WAITING) {
            throw new IllegalStateException("Room is not available for joining");
        }

        room.setPlayer2Id(playerId);
        room.setStatus(RoomStatus.IN_PROGRESS);
        return roomRepository.save(room);
    }

    public boolean playCard(Long roomId, Long playerId, Long cardId) {
        if (!cardService.isCardPlayable(cardId, playerId)) {
            return false;
        }
        cardService.updateCardEnergy(cardId, -20);
        return true;
    }
}
