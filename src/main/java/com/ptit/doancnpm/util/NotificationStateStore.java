package com.ptit.doancnpm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * Lưu mốc thời gian "đã xem thông báo" của từng tài khoản vào một file cục bộ
 * ({@code user.home/.unitopics/notifications.properties}). Nhờ vậy có thể xác
 * định đề tài nào là mới (chưa đọc) mà không cần thay đổi cơ sở dữ liệu dùng chung.
 */
public final class NotificationStateStore {

    private static final Path FILE = Path.of(
            System.getProperty("user.home"), ".unitopics", "notifications.properties");

    private NotificationStateStore() {
    }

    /** Mốc đã xem gần nhất của tài khoản, hoặc {@code null} nếu chưa từng xem. */
    public static synchronized LocalDateTime getLastSeen(int maTaiKhoan) {
        String value = load().getProperty(key(maTaiKhoan));
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    /** Ghi nhận tài khoản đã xem thông báo đến thời điểm {@code time}. */
    public static synchronized void setLastSeen(int maTaiKhoan, LocalDateTime time) {
        Properties props = load();
        props.setProperty(key(maTaiKhoan), time.toString());
        save(props);
    }

    private static String key(int maTaiKhoan) {
        return "lastSeen." + maTaiKhoan;
    }

    private static Properties load() {
        Properties props = new Properties();
        if (Files.exists(FILE)) {
            try (InputStream in = Files.newInputStream(FILE)) {
                props.load(in);
            } catch (IOException ignored) {
                // File hỏng thì coi như chưa có trạng thái nào.
            }
        }
        return props;
    }

    private static void save(Properties props) {
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) {
                props.store(out, "UniTopics notification state");
            }
        } catch (IOException ignored) {
            // Không lưu được thì bỏ qua; lần sau sẽ hiển thị lại như chưa đọc.
        }
    }
}
