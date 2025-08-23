package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller

@RequiredArgsConstructor
public class AdminUserController extends BaseAdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public String getUserList(Model model, HttpServletRequest request) {
        List<UserResponse> users = adminService.getAllUsers();
        model.addAttribute("users", users);

        // Truyền current URI để sidebar highlight menu đúng
        model.addAttribute("currentUri", request.getRequestURI());

        return "admin/users/user-list";
    }
}
