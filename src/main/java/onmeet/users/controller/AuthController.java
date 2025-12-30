package onmeet.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/register")
    public String login() {
        return "auth/register";
    }

    @GetMapping("/login")
    public String register() {
        return "auth/login";
    }
}
