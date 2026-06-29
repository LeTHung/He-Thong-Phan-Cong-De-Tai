package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.TopicBankDAO;
import com.ptit.doancnpm.model.entity.Topic;

import java.util.List;

public class TopicBankService {

    private final TopicBankDAO topicBankDAO = new TopicBankDAO();

    public int findMaGiangVienByTaiKhoan(int maTaiKhoan) {
        return topicBankDAO.findMaGiangVienByTaiKhoan(maTaiKhoan);
    }

    public List<Topic> findByGiangVien(int maGiangVien) {
        return topicBankDAO.findByGiangVien(maGiangVien);
    }

    public List<Topic> searchByName(int maGiangVien, String keyword) {
        return topicBankDAO.searchByName(maGiangVien, keyword);
    }

    public void insert(int maGiangVien, String code, String name,
                       String description, String requirement, int maxStudents) {
        topicBankDAO.insert(new Topic(0, code, name, description, requirement, null,
                maxStudents, maGiangVien, "DANG_SU_DUNG", null, null));
    }

    public void update(int topicId, int maGiangVien, String code, String name,
                       String description, String requirement, int maxStudents) {
        topicBankDAO.update(new Topic(topicId, code, name, description, requirement, null,
                maxStudents, maGiangVien, "DANG_SU_DUNG", null, null));
    }

    public void softDelete(int topicId, int maGiangVien) {
        topicBankDAO.softDelete(topicId, maGiangVien);
    }

    public boolean isAssignedToClass(int topicId) {
        return topicBankDAO.isAssignedToClass(topicId);
    }

    public boolean existsByCode(String code, Integer excludeId) {
        return topicBankDAO.existsByCode(code, excludeId);
    }
}
