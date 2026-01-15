package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.dto.BookingRequest;
import com.example.bookingmeeting_be.model.*;
import com.example.bookingmeeting_be.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingDeviceRepository bookingDeviceRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private BookingAttendeeRepository bookingAttendeeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationRecipientRepository notificationRecipientRepository;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        //Validate Time
        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("Không đuợc trống thời gian");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Thời gian kết thúc phải lớn hơn thời gian bắt đầu");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không được đặt thời gian trong quá khứ");
        }
        boolean isConflict = bookingRepository.existsOverlappingBooking(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime()
        );
        if (isConflict) {
            throw new RuntimeException("Không thể đặt phòng họp này trong thời gian này");
        }
        Booking booking = new Booking();
        booking.setTitle(request.getTitle());
        booking.setDescription(request.getDescription());
        booking.setRoomId(request.getRoomId());
        booking.setHostUserId(request.getHostUserId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus("BOOKED");

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
                    syncAttendeesAndNotify(savedBooking,request.getAttendeeUserIds(),false);
                }
            }
        }

        return savedBooking;
    }

    @Transactional
    public void cancelBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        List<BookingDevice> bookingDevices = bookingDeviceRepository.findByBookingId(bookingId);

        for (BookingDevice bd : bookingDevices) {
            Device device = bd.getDevice();
            device.setQuantity(device.getQuantity() + bd.getQuantity());
            deviceRepository.save(device);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Transactional
    public Booking updateBooking(Integer bookingId, BookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Không thể sửa phòng họp đã hủy");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new RuntimeException("Thời gian không hợp lệ");
        }
        boolean conflict = bookingRepository.existsOverlappingBookingForUpdate(
                bookingId,
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (conflict) {
            throw new RuntimeException("Trùng lịch với booking khác");
        }

        booking.setTitle(request.getTitle());
        booking.setRoomId(request.getRoomId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setDescription(request.getDescription());

        Booking updatedBooking = bookingRepository.save(booking);
        syncAttendeesAndNotify(updatedBooking,request.getAttendeeUserIds(),true);

        return updatedBooking;
    }

    public List<Booking> getBookingsByUser(Integer userId) {
        return bookingRepository.findByHostUserIdOrderByStartTimeDesc(userId);
    }

    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public void syncAttendeesAndNotify(Booking booking, List<Integer> attendeeIds, boolean isUpdate) {

        // 1) nếu update: xoá hết attendees cũ (bản đơn giản)
        if (isUpdate) {
            bookingAttendeeRepository.deleteByBookingAttendeeIdBookingId(booking.getId());
        }

        // 2) normalize list: null-safe, bỏ host, bỏ trùng
        if (attendeeIds == null || attendeeIds.isEmpty()) return;

        List<Integer> normalized = attendeeIds.stream()
                .filter(id -> id != null)
                .filter(id -> !id.equals(booking.getHostUserId()))
                .distinct()
                .toList();

        if (normalized.isEmpty()) return;

        // 3) tạo BookingAttendee
        for (Integer uid : normalized) {
            Users u = userRepository.findById(uid)
                    .orElseThrow(() -> new RuntimeException("User not found: " + uid));

            BookingAttendee ba = new BookingAttendee();
            ba.setBookingAttendeeId(new BookingAttendeeId(booking.getId(), uid));
            ba.setBooking(booking);
            ba.setUsers(u);
            ba.setStatus("INVITED");

            bookingAttendeeRepository.save(ba);
        }

        // 4) tạo 1 notification chung cho booking (không gắn user ở đây)
        Notification noti = new Notification();
        noti.setBookingId(booking.getId()); // ✅ đúng: bookingId = booking.getId()
        noti.setType(isUpdate ? "BOOKING_UPDATED" : "INVITE");
        noti.setContent(
                (isUpdate ? "Lịch họp đã được cập nhật: " : "Bạn được mời tham dự: ")
                        + booking.getTitle()
        );
        noti.setCreatedAt(LocalDateTime.now());

        Notification savedNoti = notificationRepository.save(noti);

        // 5) tạo recipients cho từng user
        for (Integer uid : normalized) {
            NotificationRecipient nr = new NotificationRecipient();
            nr.setNotification(savedNoti);
            Users uRef = userRepository.getReferenceById(uid); // hoặc findById(uid).orElseThrow()
            nr.setUser(uRef); // ✅ bắt buộc (MapsId userId)
            nr.setId(new NotificationRecipientId(savedNoti.getNotificationId(), uid));
            nr.setIsRead(false);

            notificationRecipientRepository.save(nr);
        }
    }

}