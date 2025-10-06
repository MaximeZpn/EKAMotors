package com.gp9.atelier2.controller;

import com.gp9.atelier2.dto.CarteDTO;
import com.gp9.atelier2.service.CarteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cartes")
public class CarteController {

    @Autowired
    private CarteService carteService;

    @GetMapping("/{id}")
    public ResponseEntity<CarteDTO> getCarteById(@PathVariable Long id) {
        Optional<CarteDTO> carte = carteService.getCarteById(id);
        return carte.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<CarteDTO>> getCartesByUtilisateur(@PathVariable Long utilisateurId) {
        List<CarteDTO> cartes = carteService.getCartesByUtilisateur(utilisateurId);
        return ResponseEntity.ok(cartes);
    }
}