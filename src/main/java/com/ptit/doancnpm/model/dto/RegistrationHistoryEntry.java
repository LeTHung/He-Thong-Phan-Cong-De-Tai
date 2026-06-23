package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;

/**
 * Một dòng nhật ký đăng ký/hủy đề tài của sinh viên (bảng lich_su_dang_ky).
 */
public record RegistrationHistoryEntry(
        String hanhDong,
        String maLop,
        String maDeTaiHeThong,
        String tenDeTai,
        String hinhThucPhanCong,
        String lyDo,
        LocalDateTime thoiDiemThucHien) {

    public String hanhDongText() {
        if (hanhDong == null) {
            return "";
        }
        return switch (hanhDong) {
            case "DANG_KY" -> "Đăng ký";
            case "HUY_DANG_KY" -> "Hủy đăng ký";
            case "PHAN_CONG_THU_CONG" -> "Giảng viên phân công";
            case "PHAN_CONG_TU_DONG" -> "Hệ thống phân công";
            default -> hanhDong;
        };
    }

    public String hinhThucText() {
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
