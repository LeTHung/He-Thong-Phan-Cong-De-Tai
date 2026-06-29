package com.ptit.doancnpm.model.entity;

public enum UserStatus {
    HOAT_DONG,
    BI_KHOA;

    public static UserStatus fromDatabaseValue(String value) {
        if (value == null) {
            return BI_KHOA;
        }

        return switch (value.trim().toUpperCase()) {
            case "HOAT_DONG" -> HOAT_DONG;
            case "BI_KHOA" -> BI_KHOA;
            default -> BI_KHOA;
        };
    }

    public boolean isActive() {
        return this == HOAT_DONG;
    }
}
