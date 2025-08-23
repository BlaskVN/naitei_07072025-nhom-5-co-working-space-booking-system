package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.service.AdminService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminUserController extends BaseAdminController {

    private final AdminService adminService;
    private final MessageSource messageSource;

    @GetMapping("/users")
    public String getUserList(
            @RequestParam(value = "role", required = false) UserRole role, // lấy role từ query param
            Model model,
            HttpServletRequest request) {

        List<UserResponse> users;

        if (role != null) {
            users = adminService.getUsersByRole(role);
        } else {
            users = adminService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("currentUri", request.getRequestURI());

        return "admin/users/user-list";
    }
    @PostMapping("/users/{id}/approve-owner")
    public String approveOwner(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminService.approveOwner(id);
            String successMessage = messageSource.getMessage("user.approve.owner.success", 
                                                            null, 
                                                            LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        } catch (Exception e) {
            String errorMessage = messageSource.getMessage("user.approve.owner.error", 
                                                         null, 
                                                         LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage + ": " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
