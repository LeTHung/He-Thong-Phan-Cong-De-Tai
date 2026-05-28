package com.ptit.doancnpm.model.dto;

public record StudentTopicSummary(
        int maDeTaiLop,
        String maLop,
        String maDeTaiHeThong,
        String tenDeTai,
        int soLuongToiDa,
        int soLuongHienTai,
        int soChoConLai,
        String trangThai,
        String cheDoPhanCong,
        boolean daDangKy) {
}
