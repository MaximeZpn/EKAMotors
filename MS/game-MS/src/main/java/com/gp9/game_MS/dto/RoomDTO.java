package com.gp9.game_MS.dto;

import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RoomDTO {
    private Long id;
    private String name;
    private double betAmount;
    private Long player1Id;
    private Long player2Id;
    private RoomStatus status;
    private Long winnerId;
    private LocalDateTime createdAt;
    private Long createdBy;
    
    public RoomDTO(Room room) {
        this.id = room.getId();
        this.name = room.getName();
        this.betAmount = room.getBetAmount();
        this.player1Id = room.getPlayer1Id();
        this.player2Id = room.getPlayer2Id();
        this.status = room.getStatus();
        this.winnerId = room.getWinnerId();
        this.createdAt = room.getCreatedAt();
        this.createdBy = room.getCreatedBy();
    }
}
