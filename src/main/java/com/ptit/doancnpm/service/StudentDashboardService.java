package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.StudentDashboardDAO;
import com.ptit.doancnpm.model.dao.TopicRegistrationDAO;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;

import java.util.List;

/**
 * Nghiệp vụ cho màn hình tổng quan của sinh viên. Gộp thông tin cá nhân,
 * danh sách đề tài, đợt đăng ký và đề tài đã đăng ký từ các DAO sẵn có.
 */
public class StudentDashboardService {

    private final StudentDashboardDAO studentDashboardDAO = new StudentDashboardDAO();
    private final TopicRegistrationDAO topicRegistrationDAO = new TopicRegistrationDAO();

    public StudentDashboardData getDashboardData(int maTaiKhoan) {
        StudentInfo studentInfo = studentDashboardDAO.findStudentInfoByAccountId(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ sinh viên cho tài khoản này."));
        List<StudentTopicSummary> topics = studentDashboardDAO.findTopicsByAccountId(maTaiKhoan);

        RegistrationPeriod period = studentInfo.maLopHocPhan() == null
                ? null
                : topicRegistrationDAO.findRegistrationPeriod(studentInfo.maLopHocPhan()).orElse(null);
        List<RegisteredTopic> registeredTopics = topicRegistrationDAO.findMyRegistrations(maTaiKhoan);

        return new StudentDashboardData(studentInfo, topics, period, registeredTopics);
    }
}
