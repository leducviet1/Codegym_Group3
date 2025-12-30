package onmeet.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DepartmentController {
    @GetMapping("/department")
    public String department(Model model) {
        model.addAttribute("activePage", "department");
        return "users/department";
    }

    @GetMapping("/department-detail")
    public String departmentDetail(Model model) {
        model.addAttribute("activePage", "department");
        return "users/department-detail";
    }
}
