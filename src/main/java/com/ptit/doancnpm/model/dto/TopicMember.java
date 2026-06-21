package com.ptit.doancnpm.model.dto;

/**
 * Một sinh viên đã đăng ký cùng một đề tài (thành viên nhóm).
 */
public record TopicMember(
        String maSoSinhVien,
        String hoTen,
        String hinhThucPhanCong) {

    public String hinhThucText() {
        if (hinhThucPhanCong == null) {
            return "";
        }
        return switch (hinhThucPhanCong) {
            case "TU_DANG_KY" -> "Tự đăng ký";
            case "THU_CONG" -> "GV phân công";
            case "TU_DONG" -> "Tự động";
            default -> hinhThucPhanCong;
        };
    }
}
