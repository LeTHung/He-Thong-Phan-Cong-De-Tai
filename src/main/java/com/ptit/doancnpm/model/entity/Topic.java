package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class Topic {
    private int maDeTai;
    private String maDeTaiHeThong;
    private String tenDeTai;
    private String moTa;
    private String yeuCau;
    private String ghiChu;
    private int soLuongMacDinh;
    private int maGiangVienTao;
    private String trangThai;
    private LocalDateTime thoiDiemTao;
    private LocalDateTime thoiDiemCapNhat;

    public Topic() {
    }

    public Topic(
            int maDeTai,
            String maDeTaiHeThong,
            String tenDeTai,
            String moTa,
            String yeuCau,
            String ghiChu,
            int soLuongMacDinh,
            int maGiangVienTao,
            String trangThai,
            LocalDateTime thoiDiemTao,
            LocalDateTime thoiDiemCapNhat) {
        this.maDeTai = maDeTai;
        this.maDeTaiHeThong = maDeTaiHeThong;
        this.tenDeTai = tenDeTai;
        this.moTa = moTa;
        this.yeuCau = yeuCau;
        this.ghiChu = ghiChu;
        this.soLuongMacDinh = soLuongMacDinh;
        this.maGiangVienTao = maGiangVienTao;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
        this.thoiDiemCapNhat = thoiDiemCapNhat;
    }

    public int getMaDeTai() {
        return maDeTai;
    }

    public void setMaDeTai(int maDeTai) {
        this.maDeTai = maDeTai;
    }

    public String getMaDeTaiHeThong() {
        return maDeTaiHeThong;
    }

    public void setMaDeTaiHeThong(String maDeTaiHeThong) {
        this.maDeTaiHeThong = maDeTaiHeThong;
    }

    public String getTenDeTai() {
        return tenDeTai;
    }

    public void setTenDeTai(String tenDeTai) {
        this.tenDeTai = tenDeTai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getYeuCau() {
        return yeuCau;
    }

    public void setYeuCau(String yeuCau) {
        this.yeuCau = yeuCau;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getSoLuongMacDinh() {
        return soLuongMacDinh;
    }

    public void setSoLuongMacDinh(int soLuongMacDinh) {
        this.soLuongMacDinh = soLuongMacDinh;
    }

    public int getMaGiangVienTao() {
        return maGiangVienTao;
    }

    public void setMaGiangVienTao(int maGiangVienTao) {
        this.maGiangVienTao = maGiangVienTao;
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

    public LocalDateTime getThoiDiemCapNhat() {
        return thoiDiemCapNhat;
    }

    public void setThoiDiemCapNhat(LocalDateTime thoiDiemCapNhat) {
        this.thoiDiemCapNhat = thoiDiemCapNhat;
    }

    @Override
    public String toString() {
        return maDeTaiHeThong + " - " + tenDeTai;
    }

}
