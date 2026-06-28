package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class Lecturer {
    private int maGiangVien;
    private int maTaiKhoan;
    private String maSoGiangVien;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String khoaBoMon;
    private String hocVi;
    private String trangThai;
    private LocalDateTime thoiDiemTao;

    public Lecturer() {
    }

    public Lecturer(
            int maGiangVien,
            int maTaiKhoan,
            String maSoGiangVien,
            String hoTen,
            String email,
            String soDienThoai,
            String khoaBoMon,
            String hocVi,
            String trangThai,
            LocalDateTime thoiDiemTao) {
        this.maGiangVien = maGiangVien;
        this.maTaiKhoan = maTaiKhoan;
        this.maSoGiangVien = maSoGiangVien;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.khoaBoMon = khoaBoMon;
        this.hocVi = hocVi;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(int maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaSoGiangVien() {
        return maSoGiangVien;
    }

    public void setMaSoGiangVien(String maSoGiangVien) {
        this.maSoGiangVien = maSoGiangVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getKhoaBoMon() {
        return khoaBoMon;
    }

    public void setKhoaBoMon(String khoaBoMon) {
        this.khoaBoMon = khoaBoMon;
    }

    public String getHocVi() {
        return hocVi;
    }

    public void setHocVi(String hocVi) {
        this.hocVi = hocVi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }
}
