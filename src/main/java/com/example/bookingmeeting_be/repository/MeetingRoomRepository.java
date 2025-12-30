package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
}
