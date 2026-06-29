package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class Subject {
    private int maMonHoc;
    private String maMonHocHeThong;
    private String tenMonHoc;
    private int soTinChi;
    private String moTa;
    private String trangThai;
    private LocalDateTime thoiDiemTao;

    public Subject() {
    }

    public Subject(
            int maMonHoc,
            String maMonHocHeThong,
            String tenMonHoc,
            int soTinChi,
            String moTa,
            String trangThai,
            LocalDateTime thoiDiemTao) {
        this.maMonHoc = maMonHoc;
        this.maMonHocHeThong = maMonHocHeThong;
        this.tenMonHoc = tenMonHoc;
        this.soTinChi = soTinChi;
        this.moTa = moTa;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(int maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getMaMonHocHeThong() {
        return maMonHocHeThong;
    }

    public void setMaMonHocHeThong(String maMonHocHeThong) {
        this.maMonHocHeThong = maMonHocHeThong;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
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
