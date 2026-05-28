package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.SubjectDAO;
import com.ptit.doancnpm.model.dto.SubjectSummary;

import java.util.List;

public class SubjectService {
    public static final String STATUS_ACTIVE = "DANG_SU_DUNG";
    public static final String STATUS_INACTIVE = "NGUNG_SU_DUNG";

    private final SubjectDAO subjectDAO = new SubjectDAO();

    public List<SubjectSummary> getAllSubjects() {
        return subjectDAO.findAll();
    }

    public void createSubject(String code, String name, String creditsText, String description, String status) {
        String cleanCode = required(code, "Mã môn học không được để trống.");
        String cleanName = required(name, "Tên môn học không được để trống.");
        int credits = parseCredits(creditsText);
        String cleanStatus = normalizeStatus(status);

        if (subjectDAO.existsByCode(cleanCode, null)) {
            throw new IllegalArgumentException("Mã môn học đã tồn tại.");
        }

        subjectDAO.create(cleanCode, cleanName, credits, optional(description), cleanStatus);
    }

    public void updateSubject(int id, String code, String name, String creditsText, String description, String status) {
        String cleanCode = required(code, "Mã môn học không được để trống.");
        String cleanName = required(name, "Tên môn học không được để trống.");
        int credits = parseCredits(creditsText);
        String cleanStatus = normalizeStatus(status);

        if (subjectDAO.existsByCode(cleanCode, id)) {
            throw new IllegalArgumentException("Mã môn học đã tồn tại.");
        }

        subjectDAO.update(id, cleanCode, cleanName, credits, optional(description), cleanStatus);
    }

    public void activateSubject(int id) {
        subjectDAO.updateStatus(id, STATUS_ACTIVE);
    }

    public void deactivateSubject(int id) {
        subjectDAO.updateStatus(id, STATUS_INACTIVE);
    }

    private int parseCredits(String creditsText) {
        try {
            int credits = Integer.parseInt(required(creditsText, "Số tín chỉ không được để trống."));
            if (credits <= 0) {
                throw new IllegalArgumentException("Số tín chỉ phải lớn hơn 0.");
            }
            return credits;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Số tín chỉ phải là số nguyên.");
        }
    }

    private String normalizeStatus(String status) {
        if (STATUS_INACTIVE.equals(status)) {
            return STATUS_INACTIVE;
        }
        return STATUS_ACTIVE;
    }

    private String required(String value, String message) {
        String cleanValue = value == null ? "" : value.trim();
        if (cleanValue.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return cleanValue;
    }

    private String optional(String value) {
        String cleanValue = value == null ? "" : value.trim();
        return cleanValue.isBlank() ? null : cleanValue;
    }
}
