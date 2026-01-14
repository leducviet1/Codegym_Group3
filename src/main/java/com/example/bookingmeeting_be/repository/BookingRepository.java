package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByHostUserIdOrderByStartTimeDesc(Integer hostUserId);
    @Query ("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.roomId = :roomId
          AND b.status <> 'CANCELLED'
          AND (
              :startTime < b.endTime
              AND :endTime > b.startTime
          )
    """)
    boolean existsOverlappingBooking(@Param("roomId") Integer roomId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime")  LocalDateTime endTime);
    @Query("""
    SELECT COUNT(b) > 0
    FROM Booking b
    WHERE b.roomId = :roomId
      AND b.id <> :bookingId
      AND b.status <> 'CANCELLED'
      AND :start < b.endTime
      AND :end > b.startTime
""")
    boolean existsOverlappingBookingForUpdate(
            @Param("bookingId") Integer bookingId,
            @Param("roomId") Integer roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
