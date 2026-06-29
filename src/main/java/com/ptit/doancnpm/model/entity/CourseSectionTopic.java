package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class CourseSectionTopic {
    private int maDeTaiLop;
    private int maLopHocPhan;
    private int maDeTai;
    private int soLuongToiDa;
    private int soLuongHienTai;
    private String trangThai;
    private String cheDoPhanCong;
    private LocalDateTime thoiDiemTao;

    public CourseSectionTopic() {
    }

    public CourseSectionTopic(
            int maDeTaiLop,
            int maLopHocPhan,
            int maDeTai,
            int soLuongToiDa,
            int soLuongHienTai,
            String trangThai,
            String cheDoPhanCong,
            LocalDateTime thoiDiemTao) {
        this.maDeTaiLop = maDeTaiLop;
        this.maLopHocPhan = maLopHocPhan;
        this.maDeTai = maDeTai;
        this.soLuongToiDa = soLuongToiDa;
        this.soLuongHienTai = soLuongHienTai;
        this.trangThai = trangThai;
        this.cheDoPhanCong = cheDoPhanCong;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaDeTaiLop() {
        return maDeTaiLop;
    }

    public void setMaDeTaiLop(int maDeTaiLop) {
        this.maDeTaiLop = maDeTaiLop;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(int maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public int getMaDeTai() {
        return maDeTai;
    }

    public void setMaDeTai(int maDeTai) {
        this.maDeTai = maDeTai;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }

    public int getSoLuongHienTai() {
        return soLuongHienTai;
    }

    public void setSoLuongHienTai(int soLuongHienTai) {
        this.soLuongHienTai = soLuongHienTai;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getCheDoPhanCong() {
        return cheDoPhanCong;
    }

    public void setCheDoPhanCong(String cheDoPhanCong) {
        this.cheDoPhanCong = cheDoPhanCong;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        this.thoiDiemTao = thoiDiemTao;
    }
}
