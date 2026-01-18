package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Booking;
import com.example.bookingmeeting_be.model.BookingAttendee;
import com.example.bookingmeeting_be.model.BookingAttendeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingAttendeeRepository
        extends JpaRepository<BookingAttendee, BookingAttendeeId> {

    // tất cả attendee của 1 booking
    List<BookingAttendee> findByBookingAttendeeIdBookingId(Integer bookingId);

    // 1 attendee cụ thể (accept / decline)
    Optional<BookingAttendee>
    findByBookingAttendeeIdBookingIdAndBookingAttendeeIdUserId(Integer bookingId, Integer userId);

    // xoá khi update booking
    void deleteByBookingAttendeeIdBookingId(Integer bookingId);

    // đếm số người ACCEPTED của 1 booking
    long countByBookingAttendeeIdBookingIdAndStatus(Integer bookingId, String status);

    // dùng cho trang "lịch tôi được mời"
    List<BookingAttendee> findByUsers_UserId(Integer userId);

    // đếm ACCEPTED cho tất cả booking (list page)
    @Query("""
              select ba.bookingAttendeeId.bookingId, count(ba)
              from BookingAttendee ba
              where ba.status = 'ACCEPTED'
              group by ba.bookingAttendeeId.bookingId
            """)
    List<Object[]> countAcceptedGroupByBooking();


//Đếm tổng số lượng
    @Query("""
              select ba.bookingAttendeeId.bookingId, count(ba)
              from BookingAttendee ba
              group by ba.bookingAttendeeId.bookingId
            """)
    List<Object[]> countAllGroupByBooking();



    @Modifying
    @Query("""
              update BookingAttendee ba
               set ba.status = :status
               where ba.bookingAttendeeId.bookingId = :bookingId
               and ba.bookingAttendeeId.userId = :userId
                and ba.status = 'INVITED'
            """)
    int updateStatus(@Param("bookingId") Integer bookingId,
                     @Param("userId") Integer userId,
                     @Param("status") String status);
    @Query("""
   select ba.bookingAttendeeId.bookingId, ba.status
   from BookingAttendee ba
   where ba.bookingAttendeeId.userId = :userId
     and ba.bookingAttendeeId.bookingId in :bookingIds
""")
    List<Object[]> findStatusesByUserAndBookingIds(@Param("userId") Integer userId,
                                                   @Param("bookingIds") List<Integer> bookingIds);

    @Query("""
    select count(distinct ba.users.userId)
    from BookingAttendee ba
    join ba.booking b
    where b.status <> :cancelled
      and b.startTime >= :from and b.startTime < :to
""")
    long countDistinctActiveParticipants(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("cancelled") String cancelled
    );
}

