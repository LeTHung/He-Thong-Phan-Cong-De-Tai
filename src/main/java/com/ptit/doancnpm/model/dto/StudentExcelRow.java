package com.ptit.doancnpm.model.dto;

public record StudentExcelRow(
        int excelRowNumber,
        int sequenceNumber,
        String studentCode,
        String lastName,
        String firstName,
        String classCode,
        String email,
        String validationError) {

    public String fullName() {
        return ((lastName == null ? "" : lastName.trim()) + " "
                + (firstName == null ? "" : firstName.trim())).trim();
    }

    public boolean isValid() {
        return validationError == null || validationError.isBlank();
    }

    public String statusText() {
        return isValid() ? "Hợp lệ" : validationError;
    }
}
