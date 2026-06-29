package com.ptit.doancnpm.util;

import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalDateTime;

/**
 * Đồng hồ đếm ngược thời gian còn lại của đợt đăng ký: cập nhật nhãn badge mỗi
 * giây để hiển thị "Còn X ngày/giờ/phút" theo thời gian thực.
 *
 * <p>Khi đợt đăng ký (đang mở) vượt qua thời điểm kết thúc trong lúc sinh viên
 * đang xem, đồng hồ tự dừng và gọi {@code onExpire} đúng một lần để màn hình khóa
 * các thao tác (đăng ký / hủy). Đồng hồ cũng tự dừng khi nhãn bị gỡ khỏi scene
 * (chuyển màn) để tránh chạy nền vô ích.
 */
public final class RegistrationCountdown {

    private RegistrationCountdown() {
    }

    /**
     * Bắt đầu đếm ngược cho một badge. Trả về Timeline đang chạy (để gọi nơi khác
     * có thể dừng thủ công), hoặc null nếu đợt đăng ký không mở / không có hạn chót.
     */
    public static Timeline start(Label badge, RegistrationPeriodInfo period, Runnable onExpire) {
        if (badge == null || period == null || !period.dangMo() || period.thoiGianKetThuc() == null) {
            return null;
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            if (LocalDateTime.now().isBefore(period.thoiGianKetThuc())) {
                badge.setText(period.moTaTrangThai());
                badge.getStyleClass().setAll("badge", period.sapHetHan() ? "badge-warning" : "badge-success");
            } else {
                timeline.stop();
                badge.setText("Đợt đăng ký đã hết hạn");
                badge.getStyleClass().setAll("badge", "badge-warning");
                if (onExpire != null) {
                    onExpire.run();
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);

        // Tự dừng khi badge bị gỡ khỏi scene (sinh viên chuyển sang màn khác).
        badge.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                timeline.stop();
            }
        });

        timeline.playFromStart();
        return timeline;
    }
}
