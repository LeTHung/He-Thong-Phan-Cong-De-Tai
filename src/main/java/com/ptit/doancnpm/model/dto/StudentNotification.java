package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;

/** Một thông báo nghiệp vụ dành cho sinh viên. */
public record StudentNotification(
        String notificationId,
        Type type,
        int maLopHocPhan,
        String maLop,
        String tenLopHocPhan,
        String tenGiangVien,
        LocalDateTime eventTime,
        LocalDateTime registrationDeadline) {

    public enum Type {
        ADDED_TO_CLASS,
        REGISTRATION_DEADLINE
    }
}
