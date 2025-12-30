package onmeet.admin.adminController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {
    @GetMapping("/admin")
    public String homeAdmin(){
        return "admin/admin-home";
    }
}
