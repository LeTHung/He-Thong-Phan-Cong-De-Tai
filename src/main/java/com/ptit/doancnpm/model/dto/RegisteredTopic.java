package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;

/**
 * Đề tài mà sinh viên đã đăng ký trong một lớp học phần.
 */
public record RegisteredTopic(
        int maDangKy,
        int maSinhVien,
        int maLopHocPhan,
        int maDeTaiLop,
        String maLop,
        String tenLopHocPhan,
        String tenMonHoc,
        String tenHocKy,
        String namHoc,
        String tenGiangVien,
        String maDeTaiHeThong,
        String tenDeTai,
        String moTa,
        String yeuCau,
        String hinhThucPhanCong,
        LocalDateTime thoiDiemDangKy,
        int soLuongToiDa,
        int soLuongHienTai,
        String trangThaiDeTai) {

    public String hinhThucPhanCongText() {
        if (hinhThucPhanCong == null) {
            return "";
        }
        return switch (hinhThucPhanCong) {
            case "TU_DANG_KY" -> "Sinh viên tự đăng ký";
            case "THU_CONG" -> "Giảng viên phân công";
            case "TU_DONG" -> "Hệ thống phân công tự động";
            default -> hinhThucPhanCong;
        };
    }
}
