package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.TopicRegistrationDAO;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationHistoryEntry;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.dto.StudentCourseSection;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.dto.TopicDetail;
import com.ptit.doancnpm.model.dto.TopicMember;

import java.util.List;
import java.util.Optional;

/**
 * Nghiệp vụ đăng ký / hủy đăng ký đề tài cho sinh viên.
 */
public class TopicRegistrationService {

    private final TopicRegistrationDAO topicRegistrationDAO;

    public TopicRegistrationService() {
        this(new TopicRegistrationDAO());
    }

    public TopicRegistrationService(TopicRegistrationDAO topicRegistrationDAO) {
        this.topicRegistrationDAO = topicRegistrationDAO;
    }

    public StudentInfo getStudentInfo(int maTaiKhoan) {
        return topicRegistrationDAO.findStudentInfo(maTaiKhoan)
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy hồ sơ sinh viên cho tài khoản đang đăng nhập."));
    }

    public List<StudentTopicSummary> getRegistrableTopics(int maTaiKhoan) {
        return topicRegistrationDAO.findRegistrableTopics(maTaiKhoan);
    }

    public List<StudentCourseSection> getCourseSections(int maTaiKhoan) {
        return topicRegistrationDAO.findCourseSectionsByAccountId(maTaiKhoan);
    }

    public Optional<TopicDetail> getTopicDetail(int maDeTaiLop, int maTaiKhoan) {
        if (maDeTaiLop <= 0 || maTaiKhoan <= 0) {
            return Optional.empty();
        }
        return topicRegistrationDAO.findTopicDetail(maDeTaiLop, maTaiKhoan);
    }

    public List<TopicMember> getTopicMembers(int maDeTaiLop) {
        if (maDeTaiLop <= 0) {
            return List.of();
        }
        return topicRegistrationDAO.findTopicMembers(maDeTaiLop);
    }

    public List<RegisteredTopic> getMyRegistrations(int maTaiKhoan) {
        return topicRegistrationDAO.findMyRegistrations(maTaiKhoan);
    }

    public List<RegistrationHistoryEntry> getRegistrationHistory(int maTaiKhoan) {
        return topicRegistrationDAO.findRegistrationHistory(maTaiKhoan);
    }

    public Optional<RegistrationPeriodInfo> getRegistrationPeriod(int maLopHocPhan) {
        if (maLopHocPhan <= 0) {
            return Optional.empty();
        }
        return topicRegistrationDAO.findRegistrationPeriod(maLopHocPhan);
    }

    /**
     * Sinh viên tự đăng ký đề tài. Mọi ràng buộc nghiệp vụ được stored procedure
     * kiểm tra; ở đây chỉ chặn các tham số rõ ràng không hợp lệ.
     */
    public void register(int maSinhVien, int maDeTaiLop) {
        if (maSinhVien <= 0) {
            throw new IllegalArgumentException("Không xác định được sinh viên đang đăng nhập.");
        }
        if (maDeTaiLop <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn một đề tài để đăng ký.");
        }
        topicRegistrationDAO.registerTopic(maSinhVien, maDeTaiLop);
    }

    /**
     * Đổi đề tài: hủy đề tài hiện tại và đăng ký đề tài mới trong một JDBC transaction.
     * Nếu bước nào thất bại, rollback tự động — sinh viên không bị mất đề tài cũ.
     */
    public void changeTopic(int maSinhVien, int maLopHocPhan, int oldMaDeTaiLop, int newMaDeTaiLop) {
        if (maSinhVien <= 0) {
            throw new IllegalArgumentException("Không xác định được sinh viên đang đăng nhập.");
        }
        if (maLopHocPhan <= 0) {
            throw new IllegalArgumentException("Không xác định được lớp học phần.");
        }
        if (newMaDeTaiLop <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn đề tài mới để đổi.");
        }
        if (newMaDeTaiLop == oldMaDeTaiLop) {
            throw new IllegalArgumentException("Đề tài mới trùng với đề tài hiện tại.");
        }
        topicRegistrationDAO.changeTopicTransactional(maSinhVien, maLopHocPhan, newMaDeTaiLop);
    }

    public void cancel(int maSinhVien, int maLopHocPhan, String lyDo) {
        if (maSinhVien <= 0) {
            throw new IllegalArgumentException("Không xác định được sinh viên đang đăng nhập.");
        }
        if (maLopHocPhan <= 0) {
            throw new IllegalArgumentException("Không xác định được lớp học phần để hủy đăng ký.");
        }
        topicRegistrationDAO.cancelRegistration(maSinhVien, maLopHocPhan, lyDo);
    }
}
