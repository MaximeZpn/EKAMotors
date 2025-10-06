package com.gp9.game_MS.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateRoomDTO {
    
    @NotEmpty(message = "Le nom de la room est requis")
    private String room_name;
    
    @NotNull(message = "La mise est requise")
    @Min(value = 1, message = "La mise doit être d'au moins 1")
    private Double bet_amount;
    
    @NotNull(message = "L'ID du créateur est requis")
    private Long created_by;
    
    @NotNull(message = "L'ID de la carte est requis")
    private Long card_id;
    
    // Getters and setters
    public String getRoom_name() {
        return room_name;
    }
    
    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }
    
    public Double getBet_amount() {
        return bet_amount;
    }
    
    public void setBet_amount(Double bet_amount) {
        this.bet_amount = bet_amount;
    }
    
    public Long getCreated_by() {
        return created_by;
    }
    
    public void setCreated_by(Long created_by) {
        this.created_by = created_by;
    }
    
    public Long getCard_id() {
        return card_id;
    }
    
    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }
}
