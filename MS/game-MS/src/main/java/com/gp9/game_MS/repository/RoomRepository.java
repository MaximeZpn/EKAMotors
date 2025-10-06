package com.gp9.game_MS.repository;

import com.gp9.game_MS.model.Room;
import com.gp9.game_MS.model.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);
}
