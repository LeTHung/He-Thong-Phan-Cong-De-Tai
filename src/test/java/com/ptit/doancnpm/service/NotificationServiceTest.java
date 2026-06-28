package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dto.StudentNotification;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationServiceTest {

    private final NotificationService service = new NotificationService();

    @Test
    void notificationIsUnreadWhenUserHasNeverOpenedThePanel() {
        assertTrue(service.isUnread(notificationAt(LocalDateTime.now()), null));
    }

    @Test
    void notificationIsUnreadOnlyWhenEventHappenedAfterLastSeen() {
        LocalDateTime eventTime = LocalDateTime.of(2026, 6, 28, 10, 0);

        assertTrue(service.isUnread(notificationAt(eventTime), eventTime.minusSeconds(1)));
        assertFalse(service.isUnread(notificationAt(eventTime), eventTime));
        assertFalse(service.isUnread(notificationAt(eventTime), eventTime.plusSeconds(1)));
    }

    private StudentNotification notificationAt(LocalDateTime eventTime) {
        return new StudentNotification(
                "CLASS:1", StudentNotification.Type.ADDED_TO_CLASS, 1,
                "CNPM_D23CQCN01_N", "Công nghệ phần mềm", "Giảng viên",
                eventTime, null);
    }
}
