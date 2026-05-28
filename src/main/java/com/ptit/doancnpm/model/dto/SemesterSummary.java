package com.ptit.doancnpm.model.dto;

import java.time.LocalDate;

public class SemesterSummary {
    private final int maHocKy;
    private final String maHocKyHeThong;
    private final String tenHocKy;
    private final String namHoc;
    private final LocalDate ngayBatDau;
    private final LocalDate ngayKetThuc;
    private final String trangThai;

    public SemesterSummary(
            int maHocKy,
            String maHocKyHeThong,
            String tenHocKy,
            String namHoc,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            String trangThai) {
        this.maHocKy = maHocKy;
        this.maHocKyHeThong = maHocKyHeThong;
        this.tenHocKy = tenHocKy;
        this.namHoc = namHoc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    public int getMaHocKy() {
        return maHocKy;
    }

    public String getMaHocKyHeThong() {
        return maHocKyHeThong;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getTrangThaiText() {
        return switch (trangThai == null ? "" : trangThai.toUpperCase()) {
            case "DANG_MO" -> "Đang mở";
            case "DA_DONG" -> "Đã đóng";
            default -> "Nháp";
        };
    }
}
