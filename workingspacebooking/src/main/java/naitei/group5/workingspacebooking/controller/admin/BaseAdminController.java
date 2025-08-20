package naitei.group5.workingspacebooking.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('admin')")
public abstract class BaseAdminController {
    // Base controller cho tất cả admin controllers
    // Áp dụng quy tắc bảo mật chung: yêu cầu role admin
}
