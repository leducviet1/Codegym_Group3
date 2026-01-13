package com.example.bookingmeeting_be.controller.admin.view;

import com.example.bookingmeeting_be.model.Booking;
import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.model.dto.BookingRequest;
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
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class BookingViewController {

    @Autowired
    private BookingService bookingService;

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
        model.addAttribute("bookings", bookings);
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

    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled and devices returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cancellation failed: " + e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}