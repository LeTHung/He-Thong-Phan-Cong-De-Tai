package com.ptit.doancnpm.model.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Đợt đăng ký đề tài của một lớp học phần (bảng dot_dang_ky).
 * dangMo = true khi cổng đang mở và thời điểm hiện tại nằm trong khoảng cho phép.
 */
public record RegistrationPeriod(
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String trangThai,
        boolean dangMo) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Số ngày còn lại được xem là "sắp hết hạn". */
    private static final long NGUONG_SAP_HET_HAN_NGAY = 3;

    /**
     * Thời gian còn lại tới hạn chót, dạng thân thiện ("Còn 3 ngày" / "Còn 5 giờ").
     * Trả về chuỗi rỗng nếu cổng không mở hoặc đã quá hạn.
     */
    public String thoiGianConLaiText() {
        if (!dangMo || thoiGianKetThuc == null) {
            return "";
        }
        Duration conLai = Duration.between(LocalDateTime.now(), thoiGianKetThuc);
        if (conLai.isNegative() || conLai.isZero()) {
            return "";
        }
        long ngay = conLai.toDays();
        if (ngay >= 1) {
            return "Còn " + ngay + " ngày";
        }
        long gio = conLai.toHours();
        if (gio >= 1) {
            return "Còn " + gio + " giờ";
        }
        return "Còn " + Math.max(conLai.toMinutes(), 1) + " phút";
    }

    /** Cổng đang mở và chỉ còn dưới ngưỡng ngày tới hạn. */
    public boolean sapHetHan() {
        if (!dangMo || thoiGianKetThuc == null) {
            return false;
        }
        Duration conLai = Duration.between(LocalDateTime.now(), thoiGianKetThuc);
        return !conLai.isNegative() && conLai.toDays() <= NGUONG_SAP_HET_HAN_NGAY;
    }

    /**
     * Mô tả ngắn để hiển thị cho sinh viên (kèm hạn chót và thời gian còn lại khi đang mở).
     */
    public String moTaTrangThai() {
        if (dangMo) {
            String base = thoiGianKetThuc == null
                    ? "Cổng đăng ký đang mở"
                    : "Cổng đăng ký đang mở · Hạn: " + FORMATTER.format(thoiGianKetThuc);
            String conLai = thoiGianConLaiText();
            return conLai.isEmpty() ? base : base + " (" + conLai + ")";
        }
        if ("NHAP".equals(trangThai)) {
            return "Chưa mở đăng ký";
        }
        if ("DA_DONG".equals(trangThai)) {
            return "Đã đóng (đã chốt danh sách)";
        }
        if (thoiGianBatDau != null && thoiGianKetThuc != null) {
            return "Ngoài thời gian đăng ký (" + FORMATTER.format(thoiGianBatDau)
                    + " - " + FORMATTER.format(thoiGianKetThuc) + ")";
        }
        return "Cổng đăng ký đã đóng";
    }
}
