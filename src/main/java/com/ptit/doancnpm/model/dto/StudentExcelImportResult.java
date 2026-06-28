package com.ptit.doancnpm.model.dto;

public record StudentExcelImportResult(
        int totalRows,
        int createdStudents,
        int updatedStudents,
        int addedToClass,
        int skippedRows) {
}
