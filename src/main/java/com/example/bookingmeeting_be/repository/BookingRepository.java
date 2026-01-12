package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByHostUserIdOrderByStartTimeDesc(Integer hostUserId);
}
