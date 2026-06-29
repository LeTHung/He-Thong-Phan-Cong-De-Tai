package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class TopicRegistration {
    private int maDangKy;
    private int maLopHocPhan;
    private int maSinhVien;
    private int maDeTaiLop;
    private LocalDateTime thoiDiemDangKy;
    private String hinhThucPhanCong;
    private String ghiChu;

    public TopicRegistration() {
    }

    public TopicRegistration(
            int maDangKy,
            int maLopHocPhan,
            int maSinhVien,
            int maDeTaiLop,
            LocalDateTime thoiDiemDangKy,
            String hinhThucPhanCong,
            String ghiChu) {
        this.maDangKy = maDangKy;
        this.maLopHocPhan = maLopHocPhan;
        this.maSinhVien = maSinhVien;
        this.maDeTaiLop = maDeTaiLop;
        this.thoiDiemDangKy = thoiDiemDangKy;
        this.hinhThucPhanCong = hinhThucPhanCong;
        this.ghiChu = ghiChu;
    }

    public int getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(int maDangKy) {
        this.maDangKy = maDangKy;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(int maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public int getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(int maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public int getMaDeTaiLop() {
        return maDeTaiLop;
    }

    public void setMaDeTaiLop(int maDeTaiLop) {
        this.maDeTaiLop = maDeTaiLop;
    }

    public LocalDateTime getThoiDiemDangKy() {
        return thoiDiemDangKy;
    }

    public void setThoiDiemDangKy(LocalDateTime thoiDiemDangKy) {
        this.thoiDiemDangKy = thoiDiemDangKy;
    }

    public String getHinhThucPhanCong() {
        return hinhThucPhanCong;
    }

    public void setHinhThucPhanCong(String hinhThucPhanCong) {
        this.hinhThucPhanCong = hinhThucPhanCong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
