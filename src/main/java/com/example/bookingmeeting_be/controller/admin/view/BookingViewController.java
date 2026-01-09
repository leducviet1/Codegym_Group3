package com.example.bookingmeeting_be.controller.admin.view;

import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.model.dto.BookingRequest;
import com.example.bookingmeeting_be.services.BookingService;
import com.example.bookingmeeting_be.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class BookingViewController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/create")
    public String showBookingForm(Model model) {
        BookingRequest bookingRequest = new BookingRequest();
        model.addAttribute("bookingRequest", bookingRequest);

        List<Device> availableDevices = deviceService.getAvailableDevices();
        model.addAttribute("availableDevices", availableDevices);

        return "admin/booking-form";
    }

    @PostMapping("/save")
    public String saveBooking(@ModelAttribute("bookingRequest") BookingRequest bookingRequest,
                              RedirectAttributes redirectAttributes) {
        try {
            bookingService.createBooking(bookingRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Booking successful!");
            return "redirect:/admin/bookings/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/admin/bookings/create";
        }
    }
}