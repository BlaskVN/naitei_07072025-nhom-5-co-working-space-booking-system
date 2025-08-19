package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import naitei.group5.workingspacebooking.constant.NotificationType;
import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.dto.response.SendResult;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.EmailService;
import naitei.group5.workingspacebooking.service.NotificationService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;
    
    @Override
    public SendResult sendSystemNotification(CreateSystemNotificationRequest req, Integer adminId) {
        // Lấy danh sách users theo targetRoles (tránh N+1, chỉ load các trường cần thiết)
        List<User> recipients = userRepository.findByRoleIn(req.targetRoles());
        
        // Xây dựng subject theo i18n key phụ thuộc NotificationType
        String subject = getSubjectByType(req.type());
        
        // Body email dùng mẫu chung i18n
        String emailBody = getEmailBody(req.title(), req.content());
        
        int totalRecipients = recipients.size();
        int successCount = 0;
        int failedCount = 0;
        List<String> failedEmails = new ArrayList<>();
        
        // Gửi email tuần tự
        for (User user : recipients) {
            try {
                emailService.sendSimpleMessage(user.getEmail(), subject, emailBody);
                successCount++;
                log.info("Email sent successfully to: {}", user.getEmail());
            } catch (Exception e) {
                failedCount++;
                failedEmails.add(user.getEmail());
                log.error("Failed to send email to: {}", user.getEmail(), e);
            }
        }
        
        return new SendResult(totalRecipients, successCount, failedCount, failedEmails);
    }
    
    private String getSubjectByType(NotificationType type) {
        Locale locale = Locale.forLanguageTag("vi");
        return switch (type) {
            case EMERGENCY -> messageSource.getMessage("email.subject.emergency", null, locale);
            case MAINTENANCE -> messageSource.getMessage("email.subject.maintenance", null, locale);
            case POLICY_UPDATE -> messageSource.getMessage("email.subject.policyUpdate", null, locale);
        };
    }
    
    private String getEmailBody(String title, String content) {
        Locale locale = Locale.forLanguageTag("vi");
        String template = messageSource.getMessage("email.body.systemNotification", null, locale);
        return MessageFormat.format(template, title, content);
    }
}
