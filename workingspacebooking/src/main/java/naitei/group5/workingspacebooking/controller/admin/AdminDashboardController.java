package naitei.group5.workingspacebooking.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController extends BaseAdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
