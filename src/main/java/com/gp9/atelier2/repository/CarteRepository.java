package com.gp9.atelier2.repository;

import com.gp9.atelier2.model.Carte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteRepository extends JpaRepository<Carte, Long> {

    @Query("SELECT c FROM Carte c WHERE c.proprietaire.id = :utilisateurId")
    List<Carte> findByProprietaireId(Long utilisateurId);

    @Query("SELECT c FROM Carte c WHERE c.proprietaire IS NULL")
    List<Carte> findByProprietaireIsNull();

}

