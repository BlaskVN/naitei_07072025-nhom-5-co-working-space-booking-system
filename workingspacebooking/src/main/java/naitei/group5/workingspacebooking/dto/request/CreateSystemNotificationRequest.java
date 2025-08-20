package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import naitei.group5.workingspacebooking.constant.NotificationType;
import naitei.group5.workingspacebooking.entity.enums.UserRole;

import java.util.Set;

public record CreateSystemNotificationRequest(
    @NotBlank(message = "Vui lòng nhập tiêu đề.")
    String title,
    
    @NotBlank(message = "Vui lòng nhập nội dung.")
    String content,
    
    @NotNull(message = "Vui lòng chọn loại thông báo.")
    NotificationType type,
    
    @NotEmpty(message = "Vui lòng chọn ít nhất một vai trò.")
    Set<UserRole> targetRoles
) {
}
