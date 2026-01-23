package com.example.bookingmeeting_be.controller.admin.view;

import com.example.bookingmeeting_be.model.Booking;
import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.model.dto.BookingRequest;
import com.example.bookingmeeting_be.repository.BookingAttendeeRepository;
import com.example.bookingmeeting_be.repository.MeetingRoomRepository;
import com.example.bookingmeeting_be.services.BookingService;
import com.example.bookingmeeting_be.services.DeviceService;
import com.example.bookingmeeting_be.services.MeetingRoomService;
import com.example.bookingmeeting_be.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/bookings")
public class BookingViewController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingAttendeeRepository bookingAttendeeRepository;
    @Autowired
    private MeetingRoomService meetingRoomService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String listBookings(@RequestParam(value = "userId", required = false) Integer userId, Model model) {
        List<Booking> bookings;
        if (userId != null) {
            bookings = bookingService.getBookingsByUser(userId);
        } else {
            bookings = bookingService.getAllBookings();
        }
        //Số lượng đã chấp nhận
        Map<Integer, Long> attendeeCounts = new HashMap<>();
        for (Object[] row : bookingAttendeeRepository.countAcceptedGroupByBooking()) {
            Integer bookingId = ((Number) row[0]).intValue();
            Long cnt = ((Number) row[1]).longValue();
            attendeeCounts.put(bookingId, cnt);
        }

        Map<Integer, Long> totalCounts = new HashMap<>();
        for (Object[] row : bookingAttendeeRepository.countAllGroupByBooking()) {
            Integer bookingId = ((Number) row[0]).intValue();
            Long cnt = ((Number) row[1]).longValue();
            totalCounts.put(bookingId, cnt);
        }
        model.addAttribute("invitedCounts", totalCounts);

        model.addAttribute("bookings", bookings);
        model.addAttribute("attendeeCounts", attendeeCounts);

        return "admin/booking-list";
    }

    @GetMapping("/create")
    public String showBookingForm(Model model) {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setDevices(new ArrayList<>());
        model.addAttribute("bookingRequest", bookingRequest);
        model.addAttribute("rooms", meetingRoomService.getAllWithAssets());
        List<Device> availableDevices = deviceService.getAvailableDevices();
        model.addAttribute("availableDevices", availableDevices);

        model.addAttribute("hostUsers", userService.findBookers());

        //Chọn người tham dự
        model.addAttribute("attendees",userService.findAll());

        return "admin/booking-form";
    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute("bookingRequest") BookingRequest bookingRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            bookingService.createBooking(bookingRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Booking successful!");
            return "redirect:/admin/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/bookings/create";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled and devices returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cancellation failed: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
    @GetMapping("/{id}/edit")
    public String editBookingForm(@PathVariable("id") Integer id, Model model) {
        Booking booking = bookingService.getBookingById(id);
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setTitle(booking.getTitle());
        bookingRequest.setRoomId(booking.getRoomId());
        bookingRequest.setHostUserId(booking.getHostUserId());
        bookingRequest.setStartTime(booking.getStartTime());
        bookingRequest.setEndTime(booking.getEndTime());
        bookingRequest.setDescription(booking.getDescription());
        bookingRequest.setDevices(new ArrayList<>());

        model.addAttribute("isEdit", true);
        model.addAttribute("bookingId", id);
        model.addAttribute("bookingRequest", bookingRequest);

        //Chọn người tham dự
        model.addAttribute("attendees",userService.findAll());

        // data cho select
        model.addAttribute("rooms", meetingRoomService.getAllWithAssets());
        model.addAttribute("availableDevices", deviceService.getAvailableDevices());
        model.addAttribute("hostUsers", userService.findBookers());

        return "admin/booking-form";
    }
    @PostMapping("/{id}/edit")
    public String updateBooking(@PathVariable("id") Integer id,
                                @ModelAttribute("bookingRequest") BookingRequest bookingRequest,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBooking(id, bookingRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Booking updated successfully!");
            return "redirect:/admin/bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/bookings/" + id + "/edit";
        }
    }
    @GetMapping("/{id}")
    public String bookingDetail(@PathVariable Integer id, Model model) {
        Booking booking = bookingService.getBookingById(id);

        // unwrap Optional -> trả entity thật
        var roomOpt = meetingRoomService.getById(booking.getRoomId());
        model.addAttribute("room", roomOpt.orElse(null));
        var host = userService.findById(booking.getHostUserId());

        var attendees = bookingAttendeeRepository.findAllByBookingIdFetchUser(id);
        var accepted = attendees.stream()
                .filter(a -> "ACCEPTED".equalsIgnoreCase(a.getStatus()))
                .toList();

        model.addAttribute("booking", booking);
        model.addAttribute("host", host);
        model.addAttribute("attendees", attendees);
        model.addAttribute("acceptedAttendees", accepted);
        model.addAttribute("acceptedCount", (long) accepted.size());
        model.addAttribute("invitedCount", (long) attendees.size());

        return "admin/booking-detail";
    }



}