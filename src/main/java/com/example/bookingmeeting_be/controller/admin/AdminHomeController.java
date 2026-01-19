package com.example.bookingmeeting_be.controller.admin;

import com.example.bookingmeeting_be.repository.BookingRepository;
import com.example.bookingmeeting_be.repository.DeviceRepository;
import com.example.bookingmeeting_be.repository.MeetingRoomRepository;
import com.example.bookingmeeting_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminHomeController {
    @Autowired
    private MeetingRoomRepository roomRepo;

    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @GetMapping("/home")
    public String homeAdmin(Model model){
        long totalRooms = roomRepo.count();
        long totalDevices = deviceRepo.count();
        long totalUsers = userRepo.count();
        long activeBookings = bookingRepo.countByStatus("Booked");
        model.addAttribute("totalRooms", totalRooms);
        model.addAttribute("totalDevices", totalDevices);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeBookings", activeBookings);

        return "admin/admin-home";
    }
}
