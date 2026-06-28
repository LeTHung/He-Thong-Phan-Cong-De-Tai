package com.ptit.doancnpm.model.entity;

import java.time.LocalDate;

public class CourseSectionStudent {
    private int maLopHocPhan;
    private int maSinhVien;
    private LocalDate ngayThamGia;
    private String trangThai;
    private String ghiChu;

    public CourseSectionStudent() {
    }

    public CourseSectionStudent(
            int maLopHocPhan,
            int maSinhVien,
            LocalDate ngayThamGia,
            String trangThai,
            String ghiChu) {
        this.maLopHocPhan = maLopHocPhan;
        this.maSinhVien = maSinhVien;
        this.ngayThamGia = ngayThamGia;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
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

    public LocalDate getNgayThamGia() {
        return ngayThamGia;
    }

    public void setNgayThamGia(LocalDate ngayThamGia) {
        this.ngayThamGia = ngayThamGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
