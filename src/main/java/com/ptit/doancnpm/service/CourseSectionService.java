package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.CourseSectionDAO;
import com.ptit.doancnpm.model.dto.CourseSectionSummary;
import com.ptit.doancnpm.model.dto.OptionItem;

import java.util.List;

public class CourseSectionService {
    public static final String STATUS_OPEN = "DANG_MO";
    public static final String STATUS_CLOSED = "DA_DONG";
    public static final String STATUS_ARCHIVED = "LUU_TRU";

    private final CourseSectionDAO courseSectionDAO = new CourseSectionDAO();

    public List<CourseSectionSummary> getAllCourseSections() {
        return courseSectionDAO.findAll();
    }

    public List<OptionItem> getSubjectOptions() {
        return courseSectionDAO.findSubjectOptions();
    }

    public List<OptionItem> getSemesterOptions() {
        return courseSectionDAO.findSemesterOptions();
    }

    public List<OptionItem> getLecturerOptions() {
        return courseSectionDAO.findLecturerOptions();
    }

    public void createCourseSection(
            String code,
            String name,
            OptionItem subject,
            OptionItem semester,
            OptionItem lecturer,
            String maxSizeText,
            String note,
            String status) {
        String cleanCode = required(code, "Mã lớp học phần không được để trống.");
        String cleanName = required(name, "Tên lớp học phần không được để trống.");
        Integer maxSize = parseOptionalPositiveInt(maxSizeText);
        String cleanStatus = normalizeStatus(status);

        if (subject == null) {
            throw new IllegalArgumentException("Vui lòng chọn môn học.");
        }
        if (semester == null) {
            throw new IllegalArgumentException("Vui lòng chọn học kỳ.");
        }
        if (lecturer == null) {
            throw new IllegalArgumentException("Vui lòng chọn giảng viên.");
        }
        if (courseSectionDAO.existsByCode(cleanCode, null)) {
            throw new IllegalArgumentException("Mã lớp học phần đã tồn tại.");
        }

        courseSectionDAO.create(
                cleanCode,
                cleanName,
                subject.getId(),
                semester.getId(),
                lecturer.getId(),
                maxSize,
                optional(note),
                cleanStatus);
    }

    public void updateCourseSection(
            int id,
            String code,
            String name,
            OptionItem subject,
            OptionItem semester,
            OptionItem lecturer,
            String maxSizeText,
            String note,
            String status) {
        String cleanCode = required(code, "Mã lớp học phần không được để trống.");
        String cleanName = required(name, "Tên lớp học phần không được để trống.");
        Integer maxSize = parseOptionalPositiveInt(maxSizeText);
        String cleanStatus = normalizeStatus(status);

        if (subject == null) {
            throw new IllegalArgumentException("Vui lòng chọn môn học.");
        }
        if (semester == null) {
            throw new IllegalArgumentException("Vui lòng chọn học kỳ.");
        }
        if (lecturer == null) {
            throw new IllegalArgumentException("Vui lòng chọn giảng viên.");
        }
        if (courseSectionDAO.existsByCode(cleanCode, id)) {
            throw new IllegalArgumentException("Mã lớp học phần đã tồn tại.");
        }

        courseSectionDAO.update(
                id,
                cleanCode,
                cleanName,
                subject.getId(),
                semester.getId(),
                lecturer.getId(),
                maxSize,
                optional(note),
                cleanStatus);
    }

    public void openCourseSection(int id) {
        courseSectionDAO.updateStatus(id, STATUS_OPEN);
    }

    public void closeCourseSection(int id) {
        courseSectionDAO.updateStatus(id, STATUS_CLOSED);
    }

    public void archiveCourseSection(int id) {
        courseSectionDAO.updateStatus(id, STATUS_ARCHIVED);
    }

    private Integer parseOptionalPositiveInt(String value) {
        String cleanValue = value == null ? "" : value.trim();
        if (cleanValue.isBlank()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(cleanValue);
            if (parsed <= 0) {
                throw new IllegalArgumentException("Sĩ số tối đa phải lớn hơn 0.");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Sĩ số tối đa phải là số nguyên.");
        }
    }

    private String normalizeStatus(String status) {
        if (STATUS_CLOSED.equals(status) || STATUS_ARCHIVED.equals(status)) {
            return status;
        }
        return STATUS_OPEN;
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
