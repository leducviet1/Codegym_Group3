package onmeet.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventsController {
    @GetMapping("/events")
    public String sukien(Model model) {
        model.addAttribute("activePage", "events");
        return "users/events";
    }
}
