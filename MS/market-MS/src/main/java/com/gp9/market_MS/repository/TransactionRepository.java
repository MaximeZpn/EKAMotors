package com.gp9.market_MS.repository;

import com.gp9.market_MS.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByVendeurId(Long vendeurId);
    List<Transaction> findByAcheteurId(Long acheteurId);
    List<Transaction> findByCarteId(Long carteId);
}
