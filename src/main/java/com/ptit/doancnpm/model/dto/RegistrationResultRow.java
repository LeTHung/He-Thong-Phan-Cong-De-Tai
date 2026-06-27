package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Một dòng kết quả đăng ký đề tài của sinh viên trong lớp học phần.
 */
public class RegistrationResultRow {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String maSoSinhVien;
    private final String tenSinhVien;
    private final String lopSinhHoat;
    private final String maDeTaiHeThong;
    private final String tenDeTai;
    private final String hinhThucPhanCong;
    private final LocalDateTime thoiGianDangKy;

    public RegistrationResultRow(String maSoSinhVien, String tenSinhVien, String lopSinhHoat,
                                  String maDeTaiHeThong, String tenDeTai,
                                  String hinhThucPhanCong, LocalDateTime thoiGianDangKy) {
        this.maSoSinhVien = maSoSinhVien;
        this.tenSinhVien = tenSinhVien;
        this.lopSinhHoat = lopSinhHoat;
        this.maDeTaiHeThong = maDeTaiHeThong;
        this.tenDeTai = tenDeTai;
        this.hinhThucPhanCong = hinhThucPhanCong;
        this.thoiGianDangKy = thoiGianDangKy;
    }

    public String getMaSoSinhVien() { return maSoSinhVien; }
    public String getTenSinhVien() { return tenSinhVien; }
    public String getLopSinhHoat() { return lopSinhHoat; }
    public String getMaDeTaiHeThong() { return maDeTaiHeThong; }
    public String getTenDeTai() { return tenDeTai; }
    public String getHinhThucPhanCong() { return hinhThucPhanCong; }
    public LocalDateTime getThoiGianDangKy() { return thoiGianDangKy; }

    public String getThoiGianDangKyText() {
        return thoiGianDangKy == null ? "" : FMT.format(thoiGianDangKy);
    }

    public String getHinhThucPhanCongText() {
        if (hinhThucPhanCong == null) return "";
        return switch (hinhThucPhanCong) {
            case "TU_DANG_KY" -> "Tự đăng ký";
            case "THU_CONG" -> "GV phân công";
            case "TU_DONG" -> "Tự động";
            default -> hinhThucPhanCong;
        };
    }
}
