package com.gp9.game_MS.dto;

import jakarta.validation.constraints.NotNull;

public class JoinRoomDTO {
    
    @NotNull(message = "Player ID cannot be null")
    private Long player_id;
    
    @NotNull(message = "Card ID cannot be null")
    private Long card_id;
    
    // Default constructor
    public JoinRoomDTO() {
    }
    
    // Constructor with fields
    public JoinRoomDTO(Long player_id, Long card_id) {
        this.player_id = player_id;
        this.card_id = card_id;
    }
    
    // Getters and setters
    public Long getPlayer_id() {
        return player_id;
    }
    
    public void setPlayer_id(Long player_id) {
        this.player_id = player_id;
    }
    
    public Long getCard_id() {
        return card_id;
    }
    
    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }
    
    @Override
    public String toString() {
        return "JoinRoomDTO{" +
                "player_id=" + player_id +
                ", card_id=" + card_id +
                '}';
    }
}
