package com.gp9.game_MS.dto;

import com.gp9.game_MS.model.Battle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BattleResultDTO {
    private Long id;
    private Long roomId;
    private Long card1Id;
    private Long card2Id;
    private Long winnerCardId;
    private List<String> logs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    public BattleResultDTO() {
    }
    
    public BattleResultDTO(Battle battle) {
        this.id = battle.getId();
        this.roomId = battle.getRoomId();
        this.card1Id = battle.getCard1Id();
        this.card2Id = battle.getCard2Id();
        this.winnerCardId = battle.getWinnerCardId();
        this.logs = battle.getLogs() != null ? new ArrayList<>(battle.getLogs()) : new ArrayList<>();
        this.startTime = battle.getStartTime();
        this.endTime = battle.getEndTime();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public Long getCard1Id() {
        return card1Id;
    }
    
    public void setCard1Id(Long card1Id) {
        this.card1Id = card1Id;
    }
    
    public Long getCard2Id() {
        return card2Id;
    }
    
    public void setCard2Id(Long card2Id) {
        this.card2Id = card2Id;
    }
    
    public Long getWinnerCardId() {
        return winnerCardId;
    }
    
    public void setWinnerCardId(Long winnerCardId) {
        this.winnerCardId = winnerCardId;
    }
    
    public List<String> getLogs() {
        return logs;
    }
    
    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
