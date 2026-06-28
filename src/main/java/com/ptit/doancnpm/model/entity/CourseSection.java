package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class CourseSection {
    private int maLopHocPhan;
    private String maLop;
    private String tenLopHocPhan;
    private int maMonHoc;
    private int maHocKy;
    private int maGiangVien;
    private Integer siSoToiDa;
    private String ghiChu;
    private String cheDoPhanCong;
    private String trangThai;
    private LocalDateTime thoiDiemTao;

    public CourseSection() {
    }

    public CourseSection(
            int maLopHocPhan,
            String maLop,
            String tenLopHocPhan,
            int maMonHoc,
            int maHocKy,
            int maGiangVien,
            Integer siSoToiDa,
            String ghiChu,
            String cheDoPhanCong,
            String trangThai,
            LocalDateTime thoiDiemTao) {
        this.maLopHocPhan = maLopHocPhan;
        this.maLop = maLop;
        this.tenLopHocPhan = tenLopHocPhan;
        this.maMonHoc = maMonHoc;
        this.maHocKy = maHocKy;
        this.maGiangVien = maGiangVien;
        this.siSoToiDa = siSoToiDa;
        this.ghiChu = ghiChu;
        this.cheDoPhanCong = cheDoPhanCong;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public void setMaLopHocPhan(int maLopHocPhan) {
        this.maLopHocPhan = maLopHocPhan;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLopHocPhan() {
        return tenLopHocPhan;
    }

    public void setTenLopHocPhan(String tenLopHocPhan) {
        this.tenLopHocPhan = tenLopHocPhan;
    }

    public int getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(int maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public int getMaHocKy() {
        return maHocKy;
    }

    public void setMaHocKy(int maHocKy) {
        this.maHocKy = maHocKy;
    }

    public int getMaGiangVien() {
        return maGiangVien;
    }

    public void setMaGiangVien(int maGiangVien) {
        this.maGiangVien = maGiangVien;
    }

    public Integer getSiSoToiDa() {
        return siSoToiDa;
    }

    public void setSiSoToiDa(Integer siSoToiDa) {
        this.siSoToiDa = siSoToiDa;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getCheDoPhanCong() {
        return cheDoPhanCong;
    }

    public void setCheDoPhanCong(String cheDoPhanCong) {
        this.cheDoPhanCong = cheDoPhanCong;
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
