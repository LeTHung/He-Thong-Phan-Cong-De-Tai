package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AssignTopicDAO;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.entity.Topic;

import java.util.List;

public class AssignTopicService {

    private final AssignTopicDAO assignTopicDAO = new AssignTopicDAO();

    public List<AssignedTopicRow> findByLop(int maLopHocPhan) {
        return assignTopicDAO.findByLop(maLopHocPhan);
    }

    public boolean isAlreadyAssigned(int maLopHocPhan, int maDeTai) {
        return assignTopicDAO.isAlreadyAssigned(maLopHocPhan, maDeTai);
    }

    public void assignTopic(int maLopHocPhan, int maDeTai,
                            int maxStudents, String assignmentMode) {
        assignTopicDAO.assignTopic(maLopHocPhan, maDeTai, maxStudents, assignmentMode);
    }

    public int assignTopics(int maLopHocPhan, List<Topic> topics,
                            Integer maxStudents) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException("Cần chọn ít nhất một đề tài.");
        }
        return assignTopicDAO.assignTopics(
                maLopHocPhan, topics, maxStudents);
    }

    public String getClassAssignmentMode(int maLopHocPhan) {
        return assignTopicDAO.getClassAssignmentMode(maLopHocPhan);
    }

    public void updateClassAssignmentMode(int maGiangVien, int maLopHocPhan,
                                          String assignmentMode) {
        assignTopicDAO.updateClassAssignmentMode(
                maGiangVien, maLopHocPhan, assignmentMode);
    }

    public void removeAssignment(int maDeTaiLop) {
        assignTopicDAO.removeAssignment(maDeTaiLop);
    }
}
