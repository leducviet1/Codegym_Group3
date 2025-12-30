package onmeet.admin.adminController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDepartmentController {
    @GetMapping("/admin-department")
    public String phongHop(){
        return "admin/admin-department";
    }
}
