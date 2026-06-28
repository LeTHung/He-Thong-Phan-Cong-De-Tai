package com.ptit.doancnpm.util;

import com.ptit.doancnpm.model.dto.StudentExcelRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentExcelReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void readsStudentTableWithVietnameseHeaders() throws Exception {
        Path file = createWorkbook(false);

        List<StudentExcelRow> rows = new StudentExcelReader().read(file.toFile());

        assertEquals(2, rows.size());
        assertEquals("N23DCCN001", rows.get(0).studentCode());
        assertEquals("Đặng Kim An", rows.get(0).fullName());
        assertEquals("D23CQCN01-N", rows.get(0).classCode());
        assertTrue(rows.get(0).isValid());
    }

    @Test
    void marksDuplicateStudentCodesAsInvalid() throws Exception {
        Path file = createWorkbook(true);

        List<StudentExcelRow> rows = new StudentExcelReader().read(file.toFile());

        assertFalse(rows.get(0).isValid());
        assertFalse(rows.get(1).isValid());
        assertTrue(rows.get(0).validationError().contains("Trùng Mã SV"));
    }

    private Path createWorkbook(boolean duplicateCode) throws Exception {
        Path file = tempDir.resolve("students.xlsx");
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sinh viên");
            Row header = sheet.createRow(0);
            String[] headers = {"STT", "Mã SV", "Họ lót", "Tên", "Mã lớp", "Email"};
            for (int index = 0; index < headers.length; index++) {
                header.createCell(index).setCellValue(headers[index]);
            }

            addStudent(sheet.createRow(1), 1, "N23DCCN001", "Đặng Kim", "An",
                    "D23CQCN01-N", "n23dccn001@student.ptithcm.edu.vn");
            addStudent(sheet.createRow(2), 2, duplicateCode ? "N23DCCN001" : "N23DCCN070",
                    "Nguyễn Kỳ Đức", "An", "D23CQCN02-N", "n23dccn070@student.ptithcm.edu.vn");

            try (OutputStream output = Files.newOutputStream(file)) {
                workbook.write(output);
            }
        }
        return file;
    }

    private void addStudent(
            Row row,
            int sequence,
            String code,
            String lastName,
            String firstName,
            String classCode,
            String email) {
        row.createCell(0).setCellValue(sequence);
        row.createCell(1).setCellValue(code);
        row.createCell(2).setCellValue(lastName);
        row.createCell(3).setCellValue(firstName);
        row.createCell(4).setCellValue(classCode);
        row.createCell(5).setCellValue(email);
    }
}
