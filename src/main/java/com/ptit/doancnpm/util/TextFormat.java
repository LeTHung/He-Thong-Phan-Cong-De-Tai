package com.ptit.doancnpm.util;

/** Hàm định dạng chuỗi dùng chung khi hiển thị dữ liệu có thể null/rỗng lên giao diện. */
public final class TextFormat {

    private TextFormat() {
    }

    /** Trả về "—" nếu value null/rỗng, ngược lại trả về value. */
    public static String orDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    /** Trả về "" nếu value null, ngược lại trả về value. */
    public static String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}
