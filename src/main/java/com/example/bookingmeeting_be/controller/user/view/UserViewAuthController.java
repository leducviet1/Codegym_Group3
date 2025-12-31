package com.example.bookingmeeting_be.controller.user.view;

import com.example.bookingmeeting_be.services.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserViewAuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    @GetMapping("/login")
    public String login()
    {
        return "auth/login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletResponse response, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String jwt = jwtService.generateToken(username);
            ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, jwt).httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(60 * 60).build();
            response.addHeader("Set-Cookie", cookie.toString());
            return "redirect:/users/home";
        } catch (AuthenticationException ex) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "").httpOnly(true).secure(false).path("/").sameSite("Lax").maxAge(0).build();
        response.addHeader("Set-Cookie", cookie.toString());
        return "redirect:/login";
    }
    @GetMapping("/debug/me")
    @ResponseBody
    public Object me(Authentication authentication) {
        return authentication;
    }
}
