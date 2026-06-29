package com.ptit.doancnpm.model.dto;

public record StudentInfo(
        int maSinhVien,
        String maSoSinhVien,
        String hoTen,
        String lopSinhHoat,
        String email,
        String khoaHoc,
        String nganh,
        Integer maLopHocPhan,
        String maLop,
        String tenLopHocPhan) {
}
