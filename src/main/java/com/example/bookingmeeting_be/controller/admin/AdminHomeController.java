package com.example.bookingmeeting_be.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/admin")
@Controller
public class AdminHomeController {
    @GetMapping("/home")
    public String homeAdmin(){
        return "admin/admin-home";
    }
}
