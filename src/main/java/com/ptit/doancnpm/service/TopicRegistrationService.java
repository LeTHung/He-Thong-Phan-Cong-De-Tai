package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.TopicRegistrationDAO;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationHistoryEntry;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.dto.TopicDetail;

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

    public Optional<TopicDetail> getTopicDetail(int maDeTaiLop) {
        if (maDeTaiLop <= 0) {
            return Optional.empty();
        }
        return topicRegistrationDAO.findTopicDetail(maDeTaiLop);
    }

    public List<RegisteredTopic> getMyRegistrations(int maTaiKhoan) {
        return topicRegistrationDAO.findMyRegistrations(maTaiKhoan);
    }

    public List<RegistrationHistoryEntry> getRegistrationHistory(int maTaiKhoan) {
        return topicRegistrationDAO.findRegistrationHistory(maTaiKhoan);
    }

    public Optional<RegistrationPeriod> getRegistrationPeriod(int maLopHocPhan) {
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
