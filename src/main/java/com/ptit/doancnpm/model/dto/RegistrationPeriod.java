package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;

/**
 * Đợt đăng ký đề tài của một lớp học phần (bảng dot_dang_ky).
 * dangMo = true khi cổng đang mở và thời điểm hiện tại nằm trong khoảng cho phép.
 */
public record RegistrationPeriod(
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String trangThai,
        boolean dangMo) {
}
