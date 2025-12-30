package onmeet.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ListFriendsController {
    @GetMapping("/list-friends")
    public String listFriends(Model model) {
        model.addAttribute("activePage", "list-friends");
        return "users/list-friends";
    }
}
