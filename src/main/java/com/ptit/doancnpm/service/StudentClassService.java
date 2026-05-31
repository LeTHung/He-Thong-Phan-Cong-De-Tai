package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.StudentClassDAO;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;

import java.util.List;

public class StudentClassService {
    private static final String STATUS_ACTIVE = "DANG_HOC";

    private final StudentClassDAO studentClassDAO = new StudentClassDAO();

    public List<OptionItem> getCourseSectionOptions() {
        return studentClassDAO.findCourseSectionOptions();
    }

    public List<OptionItem> getStudentOptions() {
        return studentClassDAO.findAvailableStudentOptions();
    }

    public List<StudentClassMemberSummary> getStudentsByCourseSection(OptionItem courseSection) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        return studentClassDAO.findStudentsByCourseSection(cleanCourseSection.getId());
    }

    public void addStudentToCourseSection(OptionItem courseSection, OptionItem student, String note) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        OptionItem cleanStudent = requireOption(student, "Vui lòng chọn sinh viên.");

        String currentStatus = studentClassDAO.findEnrollmentStatus(cleanCourseSection.getId(), cleanStudent.getId());
        if (STATUS_ACTIVE.equalsIgnoreCase(currentStatus)) {
            throw new IllegalArgumentException("Sinh viên này đang học trong lớp đã chọn.");
        }

        studentClassDAO.addStudentToCourseSection(
                cleanCourseSection.getId(),
                cleanStudent.getId(),
                optional(note));
    }

    public void withdrawStudentFromCourseSection(OptionItem courseSection, StudentClassMemberSummary student) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        if (student == null) {
            throw new IllegalArgumentException("Vui lòng chọn sinh viên cần rút khỏi lớp.");
        }

        studentClassDAO.withdrawStudentFromCourseSection(cleanCourseSection.getId(), student.getMaSinhVien());
    }

    private OptionItem requireOption(OptionItem option, String message) {
        if (option == null) {
            throw new IllegalArgumentException(message);
        }
        return option;
    }

    private String optional(String value) {
        String cleanValue = value == null ? "" : value.trim();
        return cleanValue.isBlank() ? null : cleanValue;
    }
}
