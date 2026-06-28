package com.ptit.doancnpm.util;

import java.time.format.DateTimeFormatter;

/** Các định dạng ngày/giờ dùng chung trong toàn bộ ứng dụng. */
public final class DateTimeFormatters {

    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateTimeFormatters() {
    }
}
