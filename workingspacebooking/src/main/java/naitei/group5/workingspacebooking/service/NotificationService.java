package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.dto.response.SendResult;

public interface NotificationService {
    SendResult sendSystemNotification(CreateSystemNotificationRequest req, Integer adminId);
}
