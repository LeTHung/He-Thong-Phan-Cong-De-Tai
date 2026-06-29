package com.ptit.doancnpm.model.dto;

public class AdminCourseSectionReport {
    private final int maLopHocPhan;
    private final String maLop;
    private final String tenLopHocPhan;
    private final int tongSoSinhVien;
    private final int soSinhVienDaCoDeTai;
    private final int soSinhVienChuaCoDeTai;
    private final int tongSoDeTai;

    public AdminCourseSectionReport(
            int maLopHocPhan,
            String maLop,
            String tenLopHocPhan,
            int tongSoSinhVien,
            int soSinhVienDaCoDeTai,
            int soSinhVienChuaCoDeTai,
            int tongSoDeTai) {
        this.maLopHocPhan = maLopHocPhan;
        this.maLop = maLop;
        this.tenLopHocPhan = tenLopHocPhan;
        this.tongSoSinhVien = tongSoSinhVien;
        this.soSinhVienDaCoDeTai = soSinhVienDaCoDeTai;
        this.soSinhVienChuaCoDeTai = soSinhVienChuaCoDeTai;
        this.tongSoDeTai = tongSoDeTai;
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

    public int getTongSoSinhVien() {
        return tongSoSinhVien;
    }

    public int getSoSinhVienDaCoDeTai() {
        return soSinhVienDaCoDeTai;
    }

    public int getSoSinhVienChuaCoDeTai() {
        return soSinhVienChuaCoDeTai;
    }

    public int getTongSoDeTai() {
        return tongSoDeTai;
    }
}
