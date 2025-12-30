package onmeet.admin.adminController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminUsersController {
    @GetMapping("/admin-users")
    public String adminUsers(){
        return "admin/admin-users";
    }
}
