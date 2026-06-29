package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.NotificationDAO;
import com.ptit.doancnpm.model.dto.StudentNotification;
import com.ptit.doancnpm.util.NotificationStateStore;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nghiệp vụ thông báo dành cho sinh viên và trạng thái đã đọc.
 */
public class NotificationService {

    /** Chỉ hiển thị lịch sử được thêm lớp trong khoảng số ngày gần đây. */
    private static final int LOOKBACK_DAYS = 30;

    private final NotificationDAO notificationDAO = new NotificationDAO();

    /** Danh sách thông báo mới nhất của sinh viên. */
    public List<StudentNotification> getNotifications(int maTaiKhoan) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(LOOKBACK_DAYS);
        return notificationDAO.findForStudent(maTaiKhoan, cutoff);
    }

    /** Mốc đã xem thông báo gần nhất của tài khoản, hoặc {@code null} nếu chưa từng xem. */
    public LocalDateTime getLastSeen(int maTaiKhoan) {
        return NotificationStateStore.getLastSeen(maTaiKhoan);
    }

    /** Thông báo chưa đọc nếu sự kiện phát sinh sau mốc đã xem gần nhất. */
    public boolean isUnread(StudentNotification notification, LocalDateTime lastSeen) {
        if (lastSeen == null) {
            return true;
        }
        return notification.eventTime() != null && notification.eventTime().isAfter(lastSeen);
    }

    public long countUnread(List<StudentNotification> notifications, LocalDateTime lastSeen) {
        return notifications.stream().filter(notification -> isUnread(notification, lastSeen)).count();
    }

    /** Đánh dấu đã đọc toàn bộ thông báo tính đến hiện tại. */
    public void markAllRead(int maTaiKhoan) {
        NotificationStateStore.setLastSeen(maTaiKhoan, LocalDateTime.now());
    }
}
