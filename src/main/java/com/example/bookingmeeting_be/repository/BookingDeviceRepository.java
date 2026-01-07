package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.BookingDevice;
import com.example.bookingmeeting_be.model.BookingDeviceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingDeviceRepository extends JpaRepository<BookingDevice, BookingDeviceKey> {
}
