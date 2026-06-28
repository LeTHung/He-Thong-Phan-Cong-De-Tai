package com.ptit.doancnpm.model.dto;

/**
 * Một lớp học phần mà sinh viên đang theo học. Dùng cho bộ chọn lớp ở trang chủ
 * khi sinh viên học cùng lúc nhiều lớp học phần.
 */
public record StudentCourseSection(
        int maLopHocPhan,
        String maLop,
        String tenLopHocPhan,
        String tenMonHoc) {

    /**
     * Nhãn ngắn cho bộ chọn lớp: chỉ mã lớp (ví dụ "CNPM_D23CQCN01_N") để không bị
     * cắt "..." dù môn học dài. Phần môn học xem ở tooltip ({@link #moTaDayDu()}).
     */
    public String hienThi() {
        if (maLop != null && !maLop.isBlank()) {
            return maLop.trim();
        }
        return tenLopHocPhan == null ? "" : tenLopHocPhan.trim();
    }

    /** Mô tả đầy đủ "mã lớp • môn học", dùng cho tooltip của bộ chọn lớp. */
    public String moTaDayDu() {
        String lop = maLop == null || maLop.isBlank() ? null : maLop.trim();
        String ten = tenMonHoc != null && !tenMonHoc.isBlank()
                ? tenMonHoc.trim()
                : (tenLopHocPhan == null ? "" : tenLopHocPhan.trim());
        if (lop == null) {
            return ten;
        }
        return ten.isBlank() ? lop : lop + " • " + ten;
    }
}
