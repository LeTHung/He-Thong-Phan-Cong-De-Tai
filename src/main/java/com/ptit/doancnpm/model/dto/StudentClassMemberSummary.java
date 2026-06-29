package com.ptit.doancnpm.model.dto;

public class StudentClassMemberSummary {
    private final int maLopHocPhan;
    private final int maSinhVien;
    private final String maSoSinhVien;
    private final String hoTen;
    private final String email;
    private final String lopSinhHoat;
    private final String trangThaiTrongLop;
    private final String ghiChu;

    public StudentClassMemberSummary(
            int maLopHocPhan,
            int maSinhVien,
            String maSoSinhVien,
            String hoTen,
            String email,
            String lopSinhHoat,
            String trangThaiTrongLop,
            String ghiChu) {
        this.maLopHocPhan = maLopHocPhan;
        this.maSinhVien = maSinhVien;
        this.maSoSinhVien = maSoSinhVien;
        this.hoTen = hoTen;
        this.email = email;
        this.lopSinhHoat = lopSinhHoat;
        this.trangThaiTrongLop = trangThaiTrongLop;
        this.ghiChu = ghiChu;
    }

    public int getMaLopHocPhan() {
        return maLopHocPhan;
    }

    public int getMaSinhVien() {
        return maSinhVien;
    }

    public String getMaSoSinhVien() {
        return maSoSinhVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getEmail() {
        return email;
    }

    public String getLopSinhHoat() {
        return lopSinhHoat;
    }

    public String getTrangThaiTrongLop() {
        return trangThaiTrongLop;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getTrangThaiText() {
        return "DA_RUT".equalsIgnoreCase(trangThaiTrongLop) ? "Đã rút" : "Đang học";
    }
}
