package com.ptit.doancnpm.model.entity;

import java.time.LocalDateTime;

public class RegistrationHistory {
    private int maNhatKy;
    private Integer maDangKy;
    private int maLopHocPhan;
    private int maSinhVien;
    private int maDeTaiLop;
    private String hanhDong;
    private String hinhThucPhanCong;
    private String lyDo;
    private Integer nguoiThucHien;
    private LocalDateTime thoiDiemThucHien;

    public RegistrationHistory() {
    }

    public RegistrationHistory(
            int maNhatKy,
            Integer maDangKy,
            int maLopHocPhan,
            int maSinhVien,
            int maDeTaiLop,
            String hanhDong,
            String hinhThucPhanCong,
            String lyDo,
            Integer nguoiThucHien,
            LocalDateTime thoiDiemThucHien) {
        this.maNhatKy = maNhatKy;
        this.maDangKy = maDangKy;
        this.maLopHocPhan = maLopHocPhan;
        this.maSinhVien = maSinhVien;
        this.maDeTaiLop = maDeTaiLop;
        this.hanhDong = hanhDong;
        this.hinhThucPhanCong = hinhThucPhanCong;
        this.lyDo = lyDo;
        this.nguoiThucHien = nguoiThucHien;
        this.thoiDiemThucHien = thoiDiemThucHien;
    }

    public int getMaNhatKy() {
        return maNhatKy;
    }

    public void setMaNhatKy(int maNhatKy) {
        this.maNhatKy = maNhatKy;
    }

    public Integer getMaDangKy() {
        return maDangKy;
    }

    public void setMaDangKy(Integer maDangKy) {
        this.maDangKy = maDangKy;
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

    public int getMaDeTaiLop() {
        return maDeTaiLop;
    }

    public void setMaDeTaiLop(int maDeTaiLop) {
        this.maDeTaiLop = maDeTaiLop;
    }

    public String getHanhDong() {
        return hanhDong;
    }

    public void setHanhDong(String hanhDong) {
        this.hanhDong = hanhDong;
    }

    public String getHinhThucPhanCong() {
        return hinhThucPhanCong;
    }

    public void setHinhThucPhanCong(String hinhThucPhanCong) {
        this.hinhThucPhanCong = hinhThucPhanCong;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public Integer getNguoiThucHien() {
        return nguoiThucHien;
    }

    public void setNguoiThucHien(Integer nguoiThucHien) {
        this.nguoiThucHien = nguoiThucHien;
    }

    public LocalDateTime getThoiDiemThucHien() {
        return thoiDiemThucHien;
    }

    public void setThoiDiemThucHien(LocalDateTime thoiDiemThucHien) {
        this.thoiDiemThucHien = thoiDiemThucHien;
    }
}
