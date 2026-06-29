package com.ptit.doancnpm.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Semester {
    private int maHocKy;
    private String maHocKyHeThong;
    private String tenHocKy;
    private String namHoc;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;
    private LocalDateTime thoiDiemTao;

    public Semester() {
    }

    public Semester(
            int maHocKy,
            String maHocKyHeThong,
            String tenHocKy,
            String namHoc,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            String trangThai,
            LocalDateTime thoiDiemTao) {
        this.maHocKy = maHocKy;
        this.maHocKyHeThong = maHocKyHeThong;
        this.tenHocKy = tenHocKy;
        this.namHoc = namHoc;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(int maHocKy) {
        this.maHocKy = maHocKy;
    }

    public String getMaHocKyHeThong() {
        return maHocKyHeThong;
    }

    public void setMaHocKyHeThong(String maHocKyHeThong) {
        this.maHocKyHeThong = maHocKyHeThong;
    }

    public String getTenHocKy() {
        return tenHocKy;
    }

    public void setTenHocKy(String tenHocKy) {
        this.tenHocKy = tenHocKy;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
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
