package naitei.group5.workingspacebooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import naitei.group5.workingspacebooking.constant.NotificationType;
import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.dto.response.SendResult;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.impl.NotificationServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private MessageSource messageSource;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    @Test
    void sendSystemNotification_WhenMultipleRoles_ShouldMergeRecipientsCorrectly() {
        // Given
        Set<UserRole> targetRoles = Set.of(UserRole.admin, UserRole.renter);
        CreateSystemNotificationRequest request = new CreateSystemNotificationRequest(
            "Test Title", "Test Content", NotificationType.EMERGENCY, targetRoles
        );
        
        List<User> recipients = Arrays.asList(
            User.builder().id(1).email("admin@test.com").role(UserRole.admin).build(),
            User.builder().id(2).email("renter@test.com").role(UserRole.renter).build()
        );
        
        when(userRepository.findByRoleIn(targetRoles)).thenReturn(recipients);
        when(messageSource.getMessage(eq("email.subject.emergency"), isNull(), any(Locale.class)))
            .thenReturn("[Khẩn cấp] Thông báo hệ thống");
        when(messageSource.getMessage(eq("email.body.systemNotification"), isNull(), any(Locale.class)))
            .thenReturn("{0}\n\n{1}");
        
        // When
        SendResult result = notificationService.sendSystemNotification(request, 1);
        
        // Then
        assertEquals(2, result.totalRecipients());
        assertEquals(2, result.successCount());
        assertEquals(0, result.failedCount());
        assertTrue(result.failedEmails().isEmpty());
        
        verify(emailService, times(2)).sendSimpleMessage(anyString(), anyString(), anyString());
    }
    
    @Test
    void sendSystemNotification_WhenEmailServiceThrowsException_ShouldIncrementFailedCount() {
        // Given
        Set<UserRole> targetRoles = Set.of(UserRole.admin);
        CreateSystemNotificationRequest request = new CreateSystemNotificationRequest(
            "Test Title", "Test Content", NotificationType.MAINTENANCE, targetRoles
        );
        
        List<User> recipients = Arrays.asList(
            User.builder().id(1).email("admin1@test.com").role(UserRole.admin).build(),
            User.builder().id(2).email("admin2@test.com").role(UserRole.admin).build()
        );
        
        when(userRepository.findByRoleIn(targetRoles)).thenReturn(recipients);
        when(messageSource.getMessage(eq("email.subject.maintenance"), isNull(), any(Locale.class)))
            .thenReturn("[Bảo trì] Thông báo hệ thống");
        when(messageSource.getMessage(eq("email.body.systemNotification"), isNull(), any(Locale.class)))
            .thenReturn("{0}\n\n{1}");
        
        // Simulate email service throwing exception for the first recipient
        doThrow(new RuntimeException("Email service error"))
            .when(emailService).sendSimpleMessage(eq("admin1@test.com"), anyString(), anyString());
        doNothing()
            .when(emailService).sendSimpleMessage(eq("admin2@test.com"), anyString(), anyString());
        
        // When
        SendResult result = notificationService.sendSystemNotification(request, 1);
        
        // Then
        assertEquals(2, result.totalRecipients());
        assertEquals(1, result.successCount());
        assertEquals(1, result.failedCount());
        assertEquals(1, result.failedEmails().size());
        assertEquals("admin1@test.com", result.failedEmails().get(0));
    }
}
