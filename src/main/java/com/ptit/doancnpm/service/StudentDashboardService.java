package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.StudentDashboardDAO;
import com.ptit.doancnpm.model.dao.TopicRegistrationDAO;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.dto.StudentCourseSection;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;

import java.util.List;
import java.util.Optional;

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
        List<StudentCourseSection> courseSections = studentDashboardDAO.findCourseSectionsByAccountId(maTaiKhoan);
        List<StudentTopicSummary> topics = studentDashboardDAO.findTopicsByAccountId(maTaiKhoan);

        RegistrationPeriodInfo period = studentInfo.maLopHocPhan() == null
                ? null
                : topicRegistrationDAO.findRegistrationPeriod(studentInfo.maLopHocPhan()).orElse(null);
        List<RegisteredTopic> registeredTopics = topicRegistrationDAO.findMyRegistrations(maTaiKhoan);

        return new StudentDashboardData(studentInfo, courseSections, topics, period, registeredTopics);
    }

    /** Đợt đăng ký của một lớp học phần cụ thể (dùng khi sinh viên đổi lớp ở trang chủ). */
    public Optional<RegistrationPeriodInfo> getRegistrationPeriod(int maLopHocPhan) {
        if (maLopHocPhan <= 0) {
            return Optional.empty();
        }
        return topicRegistrationDAO.findRegistrationPeriod(maLopHocPhan);
    }
}
