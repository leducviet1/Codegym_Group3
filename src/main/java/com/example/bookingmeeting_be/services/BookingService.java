package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.dto.BookingRequest;
import com.example.bookingmeeting_be.model.*;
import com.example.bookingmeeting_be.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDeviceRepository bookingDeviceRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        Booking booking = new Booking();
        booking.setTitle(request.getTitle());
        booking.setDescription(request.getDescription());
        booking.setRoomId(request.getRoomId());
        booking.setHostUserId(request.getHostUserId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("Booked");

        Booking savedBooking = bookingRepository.save(booking);

        if (request.getDevices() != null) {
            for (BookingRequest.DeviceRequest item : request.getDevices()) {
                if (item.getDeviceId() == null) continue;

                if (item.getQuantity() > 0) {
                    Device device = deviceRepository.findById(item.getDeviceId())
                            .orElseThrow(() -> new RuntimeException("Device not found with ID: " + item.getDeviceId()));

                    if (device.getQuantity() < item.getQuantity()) {
                        throw new RuntimeException("Insufficient quantity for device: " + device.getName());
                    }

                    device.setQuantity(device.getQuantity() - item.getQuantity());
                    deviceRepository.save(device);

                    BookingDevice bookingDevice = new BookingDevice(savedBooking, device, item.getQuantity());
                    bookingDeviceRepository.save(bookingDevice);
                }
            }
        }

        return savedBooking;
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("Cancelled".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        List<BookingDevice> bookingDevices = bookingDeviceRepository.findByBookingId(bookingId);

        for (BookingDevice bd : bookingDevices) {
            Device device = bd.getDevice();
            device.setQuantity(device.getQuantity() + bd.getQuantity());
            deviceRepository.save(device);
        }

        booking.setStatus("Cancelled");
        bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(Integer userId) {
        return bookingRepository.findByHostUserIdOrderByStartTimeDesc(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}