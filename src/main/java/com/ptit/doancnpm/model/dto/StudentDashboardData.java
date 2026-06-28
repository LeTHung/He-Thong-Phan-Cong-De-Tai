package com.ptit.doancnpm.model.dto;

import java.util.List;

/**
 * Dữ liệu tổng hợp cho màn hình tổng quan của sinh viên: thông tin cá nhân,
 * danh sách đề tài trong lớp học phần, đợt đăng ký hiện tại và các đề tài
 * sinh viên đã đăng ký.
 */
public record StudentDashboardData(
        StudentInfo studentInfo,
        List<StudentTopicSummary> topics,
        RegistrationPeriodInfo registrationPeriod,
        List<RegisteredTopic> registeredTopics) {

    /** Tổng số đề tài trong lớp học phần của sinh viên. */
    public int soDeTai() {
        return topics.size();
    }

    /** Số đề tài đang mở và còn chỗ để đăng ký. */
    public long soDeTaiConCho() {
        return topics.stream()
                .filter(topic -> topic.soChoConLai() > 0 && "DANG_MO".equals(topic.trangThai()))
                .count();
    }

    /** Số đề tài sinh viên đã đăng ký. */
    public int soDeTaiDaDangKy() {
        return registeredTopics.size();
    }

    /** Đề tài đã đăng ký mới nhất, hoặc null nếu chưa đăng ký đề tài nào. */
    public RegisteredTopic deTaiDaDangKyMoiNhat() {
        return registeredTopics.isEmpty() ? null : registeredTopics.get(0);
    }
}
