package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;

/**
 * Một đề tài mới được giảng viên thêm vào lớp học phần của sinh viên.
 * Dùng cho thông báo "đề tài mới" ở trang chủ sinh viên.
 */
public record TopicNotification(
        int maDeTaiLop,
        String maLop,
        String maDeTaiHeThong,
        String tenDeTai,
        String tenLopHocPhan,
        String tenGiangVien,
        LocalDateTime thoiDiemTao) {
}
