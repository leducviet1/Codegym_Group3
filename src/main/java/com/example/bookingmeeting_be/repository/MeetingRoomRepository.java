package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {
    @Query("""
        SELECT DISTINCT r
        FROM MeetingRoom r
        LEFT JOIN FETCH r.roomAssets ra
        LEFT JOIN FETCH ra.device
    """)
    List<MeetingRoom> findAllWithAssets();
}
