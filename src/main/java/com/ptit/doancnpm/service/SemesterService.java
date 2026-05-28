package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.SemesterDAO;
import com.ptit.doancnpm.model.dto.SemesterSummary;

import java.time.LocalDate;
import java.util.List;

public class SemesterService {
    public static final String STATUS_DRAFT = "NHAP";
    public static final String STATUS_OPEN = "DANG_MO";
    public static final String STATUS_CLOSED = "DA_DONG";

    private final SemesterDAO semesterDAO = new SemesterDAO();

    public List<SemesterSummary> getAllSemesters() {
        return semesterDAO.findAll();
    }

    public void createSemester(
            String code,
            String name,
            String schoolYear,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
        String cleanCode = required(code, "Mã học kỳ không được để trống.");
        String cleanName = required(name, "Tên học kỳ không được để trống.");
        String cleanSchoolYear = required(schoolYear, "Năm học không được để trống.");
        validateDateRange(startDate, endDate);
        String cleanStatus = normalizeStatus(status);

        if (semesterDAO.existsByCode(cleanCode, null)) {
            throw new IllegalArgumentException("Mã học kỳ đã tồn tại.");
        }

        semesterDAO.create(cleanCode, cleanName, cleanSchoolYear, startDate, endDate, cleanStatus);
    }

    public void updateSemester(
            int id,
            String code,
            String name,
            String schoolYear,
            LocalDate startDate,
            LocalDate endDate,
            String status) {
        String cleanCode = required(code, "Mã học kỳ không được để trống.");
        String cleanName = required(name, "Tên học kỳ không được để trống.");
        String cleanSchoolYear = required(schoolYear, "Năm học không được để trống.");
        validateDateRange(startDate, endDate);
        String cleanStatus = normalizeStatus(status);

        if (semesterDAO.existsByCode(cleanCode, id)) {
            throw new IllegalArgumentException("Mã học kỳ đã tồn tại.");
        }

        semesterDAO.update(id, cleanCode, cleanName, cleanSchoolYear, startDate, endDate, cleanStatus);
    }

    public void openSemester(int id) {
        semesterDAO.updateStatus(id, STATUS_OPEN);
    }

    public void closeSemester(int id) {
        semesterDAO.updateStatus(id, STATUS_CLOSED);
    }

    public void draftSemester(int id) {
        semesterDAO.updateStatus(id, STATUS_DRAFT);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
    }

    private String normalizeStatus(String status) {
        if (STATUS_OPEN.equals(status) || STATUS_CLOSED.equals(status)) {
            return status;
        }
        return STATUS_DRAFT;
    }

    private String required(String value, String message) {
        String cleanValue = value == null ? "" : value.trim();
        if (cleanValue.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return cleanValue;
    }
}
