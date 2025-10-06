package com.gp9.game_MS.controller;

import com.gp9.game_MS.dto.BattleResultDTO;
import com.gp9.game_MS.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game/battles")
public class BattleController {

    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getBattleResultByRoomId(@PathVariable Long roomId) {
        try {
            BattleResultDTO result = battleService.getBattleResultByRoomId(roomId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la récupération des résultats du combat: " + e.getMessage(), 
                                       HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
