package naitei.group5.workingspacebooking.dto.response;

import java.util.List;

public record SendResult(
    int totalRecipients,
    int successCount,
    int failedCount,
    List<String> failedEmails
) {
}
