package com.ptit.doancnpm.model.dto;

/**
 * Sinh viên chưa có đề tài trong một lớp học phần.
 */
public class UnregisteredStudentRow {

    private final int maSinhVien;
    private final String maSoSinhVien;
    private final String tenSinhVien;
    private final String lopSinhHoat;
    private final String email;

    public UnregisteredStudentRow(int maSinhVien, String maSoSinhVien, String tenSinhVien,
                                   String lopSinhHoat, String email) {
        this.maSinhVien = maSinhVien;
        this.maSoSinhVien = maSoSinhVien;
        this.tenSinhVien = tenSinhVien;
        this.lopSinhHoat = lopSinhHoat;
        this.email = email;
    }

    public int getMaSinhVien() { return maSinhVien; }
    public String getMaSoSinhVien() { return maSoSinhVien; }
    public String getTenSinhVien() { return tenSinhVien; }
    public String getLopSinhHoat() { return lopSinhHoat; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return maSoSinhVien + " - " + tenSinhVien;
    }
}
