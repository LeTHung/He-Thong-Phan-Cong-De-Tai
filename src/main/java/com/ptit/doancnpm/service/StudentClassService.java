package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.StudentClassDAO;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;
import com.ptit.doancnpm.model.dto.StudentExcelImportResult;
import com.ptit.doancnpm.model.dto.StudentExcelRow;
import com.ptit.doancnpm.util.StudentExcelReader;

import java.io.File;
import java.util.List;

public class StudentClassService {
    private final StudentClassDAO studentClassDAO = new StudentClassDAO();
    private final StudentExcelReader studentExcelReader = new StudentExcelReader();

    public List<OptionItem> getCourseSectionOptions() {
        return studentClassDAO.findCourseSectionOptions();
    }

    public List<OptionItem> getStudentOptions(OptionItem courseSection) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        return studentClassDAO.findAvailableStudentOptions(cleanCourseSection.getId());
    }

    public List<StudentClassMemberSummary> getStudentsByCourseSection(OptionItem courseSection) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        return studentClassDAO.findStudentsByCourseSection(cleanCourseSection.getId());
    }

    public int addStudentsToCourseSection(OptionItem courseSection, List<OptionItem> students, String note) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        List<Integer> studentIds = students == null
                ? List.of()
                : students.stream().filter(java.util.Objects::nonNull).map(OptionItem::getId).distinct().toList();
        if (studentIds.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng tích chọn ít nhất một sinh viên.");
        }

        studentClassDAO.addStudentsToCourseSection(cleanCourseSection.getId(), studentIds, optional(note));
        return studentIds.size();
    }

    public List<StudentExcelRow> readExcel(File file) {
        return studentExcelReader.read(file);
    }

    public StudentExcelImportResult importExcel(
            OptionItem courseSection,
            List<StudentExcelRow> rows) {
        OptionItem cleanCourseSection = requireOption(courseSection, "Vui lòng chọn lớp học phần.");
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("File Excel không có dữ liệu sinh viên.");
        }
        long invalidRows = rows.stream().filter(row -> !row.isValid()).count();
        if (invalidRows > 0) {
            throw new IllegalArgumentException(
                    "Còn " + invalidRows + " dòng không hợp lệ. Vui lòng sửa file Excel rồi thử lại.");
        }
        return studentClassDAO.importStudents(cleanCourseSection.getId(), rows);
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
