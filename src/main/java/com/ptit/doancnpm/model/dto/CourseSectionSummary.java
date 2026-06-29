package com.ptit.doancnpm.model.dto;

public class CourseSectionSummary {
    private final int maLopHocPhan;
    private final String maLop;
    private final String tenLopHocPhan;
    private final int maMonHoc;
    private final String tenMonHoc;
    private final int maHocKy;
    private final String tenHocKy;
    private final String namHoc;
    private final int maGiangVien;
    private final String tenGiangVien;
    private final Integer siSoToiDa;
    private final String ghiChu;
    private final String trangThai;

    public CourseSectionSummary(
            int maLopHocPhan,
            String maLop,
            String tenLopHocPhan,
            int maMonHoc,
            String tenMonHoc,
            int maHocKy,
            String tenHocKy,
            String namHoc,
            int maGiangVien,
            String tenGiangVien,
            Integer siSoToiDa,
            String ghiChu,
            String trangThai) {
        this.maLopHocPhan = maLopHocPhan;
        this.maLop = maLop;
        this.tenLopHocPhan = tenLopHocPhan;
        this.maMonHoc = maMonHoc;
        this.tenMonHoc = tenMonHoc;
        this.maHocKy = maHocKy;
        this.tenHocKy = tenHocKy;
        this.namHoc = namHoc;
        this.maGiangVien = maGiangVien;
        this.tenGiangVien = tenGiangVien;
        this.siSoToiDa = siSoToiDa;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public String getMaLop() {
        return maLop;
    }

    public String getTenLopHocPhan() {
        return tenLopHocPhan;
    }

    public int getMaMonHoc() {
        return maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public int getMaHocKy() {
        return maHocKy;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public int getMaGiangVien() {
        return maGiangVien;
    }

    public String getTenGiangVien() {
        return tenGiangVien;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getHocKyText() {
        return tenHocKy + " " + namHoc;
    }

    public String getTrangThaiText() {
        return switch (trangThai == null ? "" : trangThai.toUpperCase()) {
            case "DA_DONG" -> "Đã đóng";
            case "LUU_TRU" -> "Lưu trữ";
            default -> "Đang mở";
        };
    }
}
