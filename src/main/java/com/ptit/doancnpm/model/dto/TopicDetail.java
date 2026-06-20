package com.ptit.doancnpm.model.dto;

/**
 * Thông tin chi tiết một đề tài trong lớp học phần.
 * Lấy từ view dbo.vw_de_tai_con_cho.
 */
public record TopicDetail(
        int maDeTaiLop,
        int maLopHocPhan,
        String maLop,
        String maDeTaiHeThong,
        String tenDeTai,
        String moTa,
        String yeuCau,
        int soLuongToiDa,
        int soLuongHienTai,
        int soChoConLai,
        String trangThai,
        String cheDoPhanCong) {

    public boolean conCho() {
        return soChoConLai > 0 && "DANG_MO".equals(trangThai);
    }
}
