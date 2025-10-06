package com.gp9.carte_MS.repository;

import com.gp9.carte_MS.model.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteRepository extends JpaRepository<Carte, Long> {
    List<Carte> findByProprietaireId(Long proprietaireId);
    
    // Find all cards with energy less than specified value
    List<Carte> findByEnergyLessThan(int energy);
}
