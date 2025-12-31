package com.example.bookingmeeting_be.controller.admin;

import com.example.bookingmeeting_be.repository.UserRepository;
import com.example.bookingmeeting_be.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminUsersController {
    @Autowired
    private UserService userService;

    @GetMapping("/admin-users")
    public String listUsers(@RequestParam(required = false) String q,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        var userPage = userService.listUsers(q, page, size);
        model.addAttribute("userPage", userPage);
        model.addAttribute("q",q);
        model.addAttribute("page",page);
        model.addAttribute("size",size);
        return "admin/admin-users";
    }
}
