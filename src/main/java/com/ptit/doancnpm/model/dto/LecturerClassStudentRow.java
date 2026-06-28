package com.ptit.doancnpm.model.dto;

/** Dòng theo dõi sinh viên trong lớp học phần của giảng viên. */
public record LecturerClassStudentRow(
        int maSinhVien,
        String maSoSinhVien,
        String hoTen,
        String email,
        String lopSinhHoat,
        String maDeTai,
        String tenDeTai,
        String hinhThucPhanCong) {

    public boolean hasTopic() {
        return maDeTai != null && !maDeTai.isBlank();
    }
}
