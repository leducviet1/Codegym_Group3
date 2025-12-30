package onmeet.admin.adminController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {
    @GetMapping("/admin-login")
    public String adminLogin(){
        return "auth/admin-login";
    }

        @GetMapping("/admin-register")
        public String adminDangKy(){
            return "auth/admin-login";
        }
}