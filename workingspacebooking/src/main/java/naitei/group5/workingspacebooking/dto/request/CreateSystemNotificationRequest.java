package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import naitei.group5.workingspacebooking.constant.NotificationType;
import naitei.group5.workingspacebooking.entity.enums.UserRole;

import java.util.Set;

public record CreateSystemNotificationRequest(
    @NotBlank(message = "{validation.notification.title.required}")
    String title,
    
    @NotBlank(message = "{validation.notification.content.required}")
    String content,
    
    @NotNull(message = "{validation.notification.type.required}")
    NotificationType type,
    
    @NotEmpty(message = "{validation.notification.targetRoles.required}")
    Set<UserRole> targetRoles
) {
}
