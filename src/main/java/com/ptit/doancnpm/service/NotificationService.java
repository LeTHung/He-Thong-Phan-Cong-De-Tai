package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.NotificationDAO;
import com.ptit.doancnpm.model.dto.TopicNotification;
import com.ptit.doancnpm.util.NotificationStateStore;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Nghiệp vụ thông báo "đề tài mới" cho sinh viên: lấy các đề tài mới được thêm
 * gần đây và xác định đề tài nào chưa đọc dựa trên mốc đã xem lưu cục bộ.
 */
public class NotificationService {

    /** Chỉ coi là "mới" các đề tài được thêm trong khoảng số ngày gần đây. */
    private static final int LOOKBACK_DAYS = 30;

    private final NotificationDAO notificationDAO = new NotificationDAO();

    /** Danh sách đề tài mới được thêm gần đây trong các lớp của sinh viên, mới nhất trước. */
    public List<TopicNotification> getRecentTopics(int maTaiKhoan) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(LOOKBACK_DAYS);
        return notificationDAO.findRecentTopicsForStudent(maTaiKhoan, cutoff);
    }

    /** Mốc đã xem thông báo gần nhất của tài khoản, hoặc {@code null} nếu chưa từng xem. */
    public LocalDateTime getLastSeen(int maTaiKhoan) {
        return NotificationStateStore.getLastSeen(maTaiKhoan);
    }

    /** Một đề tài là chưa đọc nếu được thêm sau mốc đã xem gần nhất. */
    public boolean isUnread(TopicNotification topic, LocalDateTime lastSeen) {
        if (lastSeen == null) {
            return true;
        }
        return topic.thoiDiemTao() != null && topic.thoiDiemTao().isAfter(lastSeen);
    }

    public long countUnread(List<TopicNotification> topics, LocalDateTime lastSeen) {
        return topics.stream().filter(topic -> isUnread(topic, lastSeen)).count();
    }

    /** Đánh dấu đã đọc toàn bộ thông báo tính đến hiện tại. */
    public void markAllRead(int maTaiKhoan) {
        NotificationStateStore.setLastSeen(maTaiKhoan, LocalDateTime.now());
    }
}
