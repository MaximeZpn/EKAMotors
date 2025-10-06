package com.gp9.atelier2.repository;

import com.gp9.atelier2.dto.CarteDTO;
import com.gp9.atelier2.dto.MarketTransactionDTO;
import com.gp9.atelier2.model.Carte;
import com.gp9.atelier2.model.MarketTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarketTransactionRepository extends JpaRepository<MarketTransaction, Long> {
    @Query("SELECT c FROM Carte c WHERE c.aVendre = true")
    List<Carte> trouverCartesEnVente();

    @Query("SELECT new com.gp9.atelier2.dto.MarketTransactionDTO(mt, c) FROM MarketTransaction mt JOIN mt.carte c WHERE mt.acheteur.id = :utilisateurId OR mt.vendeur.id = :utilisateurId")
    List<MarketTransactionDTO> findTransactionsAvecCartes(@Param("utilisateurId") Long utilisateurId);
}
