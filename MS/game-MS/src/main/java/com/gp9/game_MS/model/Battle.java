package com.gp9.game_MS.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "battles")
public class Battle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long roomId;
    private Long card1Id;
    private Long card2Id;
    private Long winnerCardId;
    
    @Column(columnDefinition = "TEXT")
    private String logsText;
    
    @Transient
    private List<String> logs = new ArrayList<>();
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // Default constructor
    public Battle() {
        this.logs = new ArrayList<>();
    }
    
    // Add a log entry
    public void addLog(String message) {
        if (logs == null) {
            logs = new ArrayList<>();
        }
        logs.add(message);
        // Update the logsText field for persistence
        updateLogsText();
    }
    
    // Update the logsText field for database storage
    private void updateLogsText() {
        if (logs != null && !logs.isEmpty()) {
            this.logsText = String.join("\n", logs);
        }
    }
    
    // Load logs from text when loaded from database
    @PostLoad
    private void loadLogs() {
        if (logsText != null && !logsText.isEmpty()) {
            logs = new ArrayList<>();
            String[] logEntries = logsText.split("\n");
            for (String entry : logEntries) {
                logs.add(entry);
            }
        }
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
    
    public List<String> getLogs() {
        if (logs == null) {
            loadLogs();
        }
        return logs;
    }
    
    public void setLogs(List<String> logs) {
        this.logs = logs;
        updateLogsText();
    }
    
    public String getLogsText() {
        return logsText;
    }
    
    public void setLogsText(String logsText) {
        this.logsText = logsText;
        loadLogs();
    }
}
