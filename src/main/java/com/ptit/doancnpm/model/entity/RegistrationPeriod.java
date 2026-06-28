package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class RegistrationPeriod {
    private int maDotDangKy;
    private int maLopHocPhan;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private String trangThai;
    private int maGiangVienTao;
    private String ghiChu;
    private LocalDateTime thoiDiemTao;
    private LocalDateTime thoiDiemCapNhat;

    public RegistrationPeriod() {
    }

    public RegistrationPeriod(
            int maDotDangKy,
            int maLopHocPhan,
            LocalDateTime thoiGianBatDau,
            LocalDateTime thoiGianKetThuc,
            String trangThai,
            int maGiangVienTao,
            String ghiChu,
            LocalDateTime thoiDiemTao,
            LocalDateTime thoiDiemCapNhat) {
        this.maDotDangKy = maDotDangKy;
        this.maLopHocPhan = maLopHocPhan;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.trangThai = trangThai;
        this.maGiangVienTao = maGiangVienTao;
        this.ghiChu = ghiChu;
        this.thoiDiemTao = thoiDiemTao;
        this.thoiDiemCapNhat = thoiDiemCapNhat;
    }

    public int getMaDotDangKy() {
        return maDotDangKy;
    }

    public void setMaDotDangKy(int maDotDangKy) {
        this.maDotDangKy = maDotDangKy;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(int maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getMaGiangVienTao() {
        return maGiangVienTao;
    }

    public void setMaGiangVienTao(int maGiangVienTao) {
        this.maGiangVienTao = maGiangVienTao;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }

    public LocalDateTime getThoiDiemCapNhat() {
        return thoiDiemCapNhat;
    }

    public void setThoiDiemCapNhat(LocalDateTime thoiDiemCapNhat) {
        this.thoiDiemCapNhat = thoiDiemCapNhat;
    }
}
