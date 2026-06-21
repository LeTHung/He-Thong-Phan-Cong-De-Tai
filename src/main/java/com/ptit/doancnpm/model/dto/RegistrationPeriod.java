package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Đợt đăng ký đề tài của một lớp học phần (bảng dot_dang_ky).
 * dangMo = true khi cổng đang mở và thời điểm hiện tại nằm trong khoảng cho phép.
 */
public record RegistrationPeriod(
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String trangThai,
        boolean dangMo) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Mô tả ngắn để hiển thị cho sinh viên (kèm hạn chót khi đang mở).
     */
    public String moTaTrangThai() {
        if (dangMo) {
            return thoiGianKetThuc == null
                    ? "Cổng đăng ký đang mở"
                    : "Cổng đăng ký đang mở · Hạn: " + FORMATTER.format(thoiGianKetThuc);
        }
        if ("NHAP".equals(trangThai)) {
            return "Chưa mở đăng ký";
        }
        if ("DA_DONG".equals(trangThai)) {
            return "Đã đóng (đã chốt danh sách)";
        }
        if (thoiGianBatDau != null && thoiGianKetThuc != null) {
            return "Ngoài thời gian đăng ký (" + FORMATTER.format(thoiGianBatDau)
                    + " - " + FORMATTER.format(thoiGianKetThuc) + ")";
        }
        return "Cổng đăng ký đã đóng";
    }
}
