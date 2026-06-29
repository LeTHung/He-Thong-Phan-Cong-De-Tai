package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class SystemAuditLog {
    private int maNhatKy;
    private Integer maTaiKhoan;
    private String chucNang;
    private String hanhDong;
    private String tenBangLienQuan;
    private String khoaChinhLienQuan;
    private String noiDung;
    private String diaChiIp;
    private LocalDateTime thoiDiemThucHien;

    public SystemAuditLog() {
    }

    public SystemAuditLog(
            int maNhatKy,
            Integer maTaiKhoan,
            String chucNang,
            String hanhDong,
            String tenBangLienQuan,
            String khoaChinhLienQuan,
            String noiDung,
            String diaChiIp,
            LocalDateTime thoiDiemThucHien) {
        this.maNhatKy = maNhatKy;
        this.maTaiKhoan = maTaiKhoan;
        this.chucNang = chucNang;
        this.hanhDong = hanhDong;
        this.tenBangLienQuan = tenBangLienQuan;
        this.khoaChinhLienQuan = khoaChinhLienQuan;
        this.noiDung = noiDung;
        this.diaChiIp = diaChiIp;
        this.thoiDiemThucHien = thoiDiemThucHien;
    }

    public int getMaNhatKy() {
        return maNhatKy;
    }

    public void setMaNhatKy(int maNhatKy) {
        this.maNhatKy = maNhatKy;
    }

    public Integer getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(Integer maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getChucNang() {
        return chucNang;
    }

    public void setChucNang(String chucNang) {
        this.chucNang = chucNang;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getTenBangLienQuan() {
        return tenBangLienQuan;
    }

    public void setTenBangLienQuan(String tenBangLienQuan) {
        this.tenBangLienQuan = tenBangLienQuan;
    }

    public String getKhoaChinhLienQuan() {
        return khoaChinhLienQuan;
    }

    public void setKhoaChinhLienQuan(String khoaChinhLienQuan) {
        this.khoaChinhLienQuan = khoaChinhLienQuan;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getDiaChiIp() {
        return diaChiIp;
    }

    public void setDiaChiIp(String diaChiIp) {
        this.diaChiIp = diaChiIp;
    }

    public LocalDateTime getThoiDiemThucHien() {
        return thoiDiemThucHien;
    }

    public void setThoiDiemThucHien(LocalDateTime thoiDiemThucHien) {
        this.thoiDiemThucHien = thoiDiemThucHien;
    }
}
