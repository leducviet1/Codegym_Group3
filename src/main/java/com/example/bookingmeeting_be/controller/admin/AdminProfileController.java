package com.example.bookingmeeting_be.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminProfileController {
    @GetMapping("/admin-profile")
    public String adminHoSo(){
        return "admin/admin-profile";
    }
}
