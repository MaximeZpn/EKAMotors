package com.gp9.game_MS.repository;

import com.gp9.game_MS.model.Battle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BattleRepository extends JpaRepository<Battle, Long> {
    Optional<Battle> findByRoomId(Long roomId);
}
