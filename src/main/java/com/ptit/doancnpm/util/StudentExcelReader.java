package com.ptit.doancnpm.util;

import com.ptit.doancnpm.model.dto.StudentExcelRow;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class StudentExcelReader {

    private static final int MAX_HEADER_SCAN_ROWS = 20;
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public List<StudentExcelRow> read(File file) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("Không tìm thấy file Excel.");
        }

        try (FileInputStream input = new FileInputStream(file);
                Workbook workbook = WorkbookFactory.create(input)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new IllegalArgumentException("File Excel không có sheet dữ liệu.");
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter = new DataFormatter(Locale.forLanguageTag("vi-VN"));
            HeaderMapping headers = findHeaders(sheet, formatter, evaluator);
            return readRows(sheet, headers, formatter, evaluator);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Không thể đọc file Excel: " + exception.getMessage(), exception);
        }
    }

    private HeaderMapping findHeaders(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        int lastRow = Math.min(sheet.getLastRowNum(), MAX_HEADER_SCAN_ROWS - 1);
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= lastRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            Map<String, Integer> columns = new HashMap<>();
            for (int column = row.getFirstCellNum(); column >= 0 && column < row.getLastCellNum(); column++) {
                String header = normalize(cellText(row, column, formatter, evaluator));
                if (!header.isEmpty()) {
                    columns.putIfAbsent(header, column);
                }
            }

            Integer studentCode = findColumn(columns, "masv", "masinhvien", "mssv");
            Integer email = findColumn(columns, "email", "emailsv", "emailstudent");
            if (studentCode == null || email == null) {
                continue;
            }

            Integer fullName = findColumn(columns, "hoten", "hovaten");
            Integer lastName = findColumn(columns, "holot", "hodem", "ho");
            Integer firstName = findColumn(columns, "ten");
            Integer classCode = findColumn(columns, "malop", "lopsinhhoat", "lop");
            if (fullName == null && (lastName == null || firstName == null)) {
                throw new IllegalArgumentException(
                        "Thiếu cột Họ lót/Tên hoặc Họ tên trong file Excel.");
            }
            if (classCode == null) {
                throw new IllegalArgumentException("Thiếu cột Mã lớp trong file Excel.");
            }
            return new HeaderMapping(rowIndex, studentCode, lastName, firstName, fullName, classCode, email);
        }
        throw new IllegalArgumentException(
                "Không tìm thấy dòng tiêu đề. File cần có các cột: Mã SV, Họ lót, Tên, Mã lớp, Email.");
    }

    private List<StudentExcelRow> readRows(
            Sheet sheet,
            HeaderMapping headers,
            DataFormatter formatter,
            FormulaEvaluator evaluator) {
        List<RawStudentRow> rawRows = new ArrayList<>();
        for (int rowIndex = headers.headerRow() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            String studentCode = clean(cellText(row, headers.studentCode(), formatter, evaluator)).toUpperCase(Locale.ROOT);
            String email = clean(cellText(row, headers.email(), formatter, evaluator)).toLowerCase(Locale.ROOT);
            String classCode = clean(cellText(row, headers.classCode(), formatter, evaluator));

            String lastName;
            String firstName;
            if (headers.fullName() != null) {
                String[] nameParts = splitFullName(clean(cellText(row, headers.fullName(), formatter, evaluator)));
                lastName = nameParts[0];
                firstName = nameParts[1];
            } else {
                lastName = clean(cellText(row, headers.lastName(), formatter, evaluator));
                firstName = clean(cellText(row, headers.firstName(), formatter, evaluator));
            }

            if (studentCode.isBlank() && lastName.isBlank() && firstName.isBlank()
                    && classCode.isBlank() && email.isBlank()) {
                continue;
            }
            rawRows.add(new RawStudentRow(rowIndex + 1, studentCode, lastName, firstName, classCode, email));
        }

        if (rawRows.isEmpty()) {
            throw new IllegalArgumentException("File Excel không có dữ liệu sinh viên.");
        }

        Set<String> duplicateCodes = findDuplicateCodes(rawRows);
        List<StudentExcelRow> result = new ArrayList<>();
        for (int index = 0; index < rawRows.size(); index++) {
            RawStudentRow row = rawRows.get(index);
            result.add(new StudentExcelRow(
                    row.excelRowNumber(),
                    index + 1,
                    row.studentCode(),
                    row.lastName(),
                    row.firstName(),
                    row.classCode(),
                    row.email(),
                    validate(row, duplicateCodes)));
        }
        return result;
    }

    private String validate(RawStudentRow row, Set<String> duplicateCodes) {
        List<String> errors = new ArrayList<>();
        if (row.studentCode().isBlank()) {
            errors.add("Thiếu Mã SV");
        }
        if ((row.lastName() + row.firstName()).isBlank()) {
            errors.add("Thiếu họ tên");
        }
        if (row.classCode().isBlank()) {
            errors.add("Thiếu Mã lớp");
        }
        if (row.email().isBlank()) {
            errors.add("Thiếu Email");
        } else if (!EMAIL_PATTERN.matcher(row.email()).matches()) {
            errors.add("Email không hợp lệ");
        }
        if (!row.studentCode().isBlank() && duplicateCodes.contains(row.studentCode())) {
            errors.add("Trùng Mã SV trong file");
        }
        if (row.studentCode().length() > 30) {
            errors.add("Mã SV quá 30 ký tự");
        }
        if ((row.lastName() + " " + row.firstName()).trim().length() > 100) {
            errors.add("Họ tên quá 100 ký tự");
        }
        if (row.classCode().length() > 50) {
            errors.add("Mã lớp quá 50 ký tự");
        }
        if (row.email().length() > 100) {
            errors.add("Email quá 100 ký tự");
        }
        return String.join("; ", errors);
    }

    private Set<String> findDuplicateCodes(List<RawStudentRow> rows) {
        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (RawStudentRow row : rows) {
            if (!row.studentCode().isBlank() && !seen.add(row.studentCode())) {
                duplicates.add(row.studentCode());
            }
        }
        return duplicates;
    }

    private String cellText(Row row, Integer column, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (column == null || row.getCell(column) == null) {
            return "";
        }
        return formatter.formatCellValue(row.getCell(column), evaluator);
    }

    private Integer findColumn(Map<String, Integer> columns, String... aliases) {
        for (String alias : aliases) {
            Integer column = columns.get(alias);
            if (column != null) {
                return column;
            }
        }
        return null;
    }

    private String normalize(String value) {
        String withoutAccents = DIACRITICS.matcher(
                Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD))
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return withoutAccents.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String[] splitFullName(String fullName) {
        int separator = fullName.lastIndexOf(' ');
        return separator < 0
                ? new String[] {"", fullName}
                : new String[] {fullName.substring(0, separator).trim(), fullName.substring(separator + 1).trim()};
    }

    private record HeaderMapping(
            int headerRow,
            int studentCode,
            Integer lastName,
            Integer firstName,
            Integer fullName,
            int classCode,
            int email) {
    }

    private record RawStudentRow(
            int excelRowNumber,
            String studentCode,
            String lastName,
            String firstName,
            String classCode,
            String email) {
    }
}
