package com.example.bookingmeeting_be.controller;

import com.example.bookingmeeting_be.model.UserPrincipal;
import com.example.bookingmeeting_be.model.Users;
import com.example.bookingmeeting_be.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return userService.register(user);
    }
    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return userService.verify(user);
    }
    @GetMapping("/me")
    public Object me(@AuthenticationPrincipal UserPrincipal user) {
        return user;
    }
    @PostMapping("/logout")
    public String logout(){
        return "Logged out";
    }
}
