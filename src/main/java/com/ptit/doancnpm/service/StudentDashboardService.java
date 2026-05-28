package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.StudentDashboardDAO;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;

import java.util.List;

public class StudentDashboardService {

    private final StudentDashboardDAO studentDashboardDAO = new StudentDashboardDAO();

    public StudentDashboardData getDashboardData(int maTaiKhoan) {
        StudentInfo studentInfo = studentDashboardDAO.findStudentInfoByAccountId(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ sinh viên cho tài khoản này."));
        List<StudentTopicSummary> topics = studentDashboardDAO.findTopicsByAccountId(maTaiKhoan);

        return new StudentDashboardData(studentInfo, topics);
    }
}
