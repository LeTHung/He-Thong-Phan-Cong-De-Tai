package com.ptit.doancnpm.model.dto;

import com.ptit.doancnpm.util.DateTimeFormatters;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Đợt đăng ký đề tài của một lớp học phần (bảng dot_dang_ky).
 * dangMo = true khi cổng đang mở và thời điểm hiện tại nằm trong khoảng cho phép.
 */
public record RegistrationPeriodInfo(
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String trangThai,
        boolean dangMo) {

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
                    : "Cổng đăng ký đang mở · Hạn: " + DateTimeFormatters.DATE_TIME.format(thoiGianKetThuc);
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
            return "Ngoài thời gian đăng ký (" + DateTimeFormatters.DATE_TIME.format(thoiGianBatDau)
                    + " - " + DateTimeFormatters.DATE_TIME.format(thoiGianKetThuc) + ")";
        }
        return "Cổng đăng ký đã đóng";
    }
}
