package com.ptit.doancnpm.model.entity;

public enum UserRole {
    QUAN_TRI_VIEN("Quản trị viên"),
    GIANG_VIEN("Giảng viên"),
    SINH_VIEN("Sinh viên");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromDatabaseValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Vai trò không được để trống.");
        }

        return switch (value.trim().toUpperCase()) {
            case "QUAN_TRI_VIEN" -> QUAN_TRI_VIEN;
            case "GIANG_VIEN" -> GIANG_VIEN;
            case "SINH_VIEN" -> SINH_VIEN;
            default -> throw new IllegalArgumentException("Vai trò không hợp lệ");
        };
    }
}
