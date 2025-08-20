package naitei.group5.workingspacebooking.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import naitei.group5.workingspacebooking.constant.NotificationType;
import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.dto.response.SendResult;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.service.NotificationService;

@Controller
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController extends BaseAdminController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("notificationRequest", new CreateSystemNotificationRequest("", "", null, null));
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("notificationTypes", NotificationType.values());
        return "admin/notifications/new";
    }
    
    @PostMapping
    public String sendNotification(@Valid @ModelAttribute("notificationRequest") CreateSystemNotificationRequest request,
                                   BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", UserRole.values());
            model.addAttribute("notificationTypes", NotificationType.values());
            return "admin/notifications/new";
        }
        
        // Lấy adminId từ SecurityContext
        // Tạm thời sử dụng 1 vì chưa có logic lấy ID từ username
        Integer adminId = 1;
        
        SendResult result = notificationService.sendSystemNotification(request, adminId);
        model.addAttribute("result", result);
        
        return "admin/notifications/result";
    }
}
