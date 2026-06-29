package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class Student {
    private int maSinhVien;
    private int maTaiKhoan;
    private String maSoSinhVien;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String lopSinhHoat;
    private String khoaHoc;
    private String nganh;
    private String trangThai;
    private LocalDateTime thoiDiemTao;

    public Student() {
    }

    public Student(
            int maSinhVien,
            int maTaiKhoan,
            String maSoSinhVien,
            String hoTen,
            String email,
            String soDienThoai,
            String lopSinhHoat,
            String khoaHoc,
            String nganh,
            String trangThai,
            LocalDateTime thoiDiemTao) {
        this.maSinhVien = maSinhVien;
        this.maTaiKhoan = maTaiKhoan;
        this.maSoSinhVien = maSoSinhVien;
        this.hoTen = hoTen;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.lopSinhHoat = lopSinhHoat;
        this.khoaHoc = khoaHoc;
        this.nganh = nganh;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(int maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getMaSoSinhVien() {
        return maSoSinhVien;
    }

    public void setMaSoSinhVien(String maSoSinhVien) {
        this.maSoSinhVien = maSoSinhVien;
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

    public String getLopSinhHoat() {
        return lopSinhHoat;
    }

    public void setLopSinhHoat(String lopSinhHoat) {
        this.lopSinhHoat = lopSinhHoat;
    }

    public String getKhoaHoc() {
        return khoaHoc;
    }

    public void setKhoaHoc(String khoaHoc) {
        this.khoaHoc = khoaHoc;
    }

    public String getNganh() {
        return nganh;
    }

    public void setNganh(String nganh) {
        this.nganh = nganh;
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
