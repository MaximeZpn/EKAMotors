package com.gp9.market_MS.repository;

import com.gp9.market_MS.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByVendeurId(Long vendeurId);
    List<Offre> findByCarteId(Long carteId);
    List<Offre> findByActiveTrue();
    List<Offre> findByVendeurIdAndActiveTrue(Long vendeurId);
    List<Offre> findByCarteIdAndActiveTrue(Long carteId);
}
