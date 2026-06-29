package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.RegistrationResultDAO;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.model.dto.UnregisteredStudentRow;

import java.util.List;

public class RegistrationResultService {

    private final RegistrationResultDAO registrationResultDAO = new RegistrationResultDAO();

    public List<RegistrationResultRow> findRegisteredByLop(int maLopHocPhan) {
        return registrationResultDAO.findRegisteredByLop(maLopHocPhan);
    }

    public List<UnregisteredStudentRow> findUnregisteredByLop(int maLopHocPhan) {
        return registrationResultDAO.findUnregisteredByLop(maLopHocPhan);
    }

    public List<AssignedTopicRow> findAssignableTopicsByLop(int maLopHocPhan) {
        return maLopHocPhan <= 0
                ? List.of()
                : registrationResultDAO.findAssignableTopicsByLop(maLopHocPhan);
    }

    public void assignStudentManually(int maGiangVien, int maSinhVien,
                                      int maDeTaiLop, String ghiChu) {
        if (maGiangVien <= 0 || maSinhVien <= 0 || maDeTaiLop <= 0) {
            throw new IllegalArgumentException("Thông tin phân công không hợp lệ.");
        }
        registrationResultDAO.assignStudentManually(maGiangVien, maSinhVien, maDeTaiLop, ghiChu);
    }

    public int autoAssignUnregisteredStudents(int maGiangVien, int maLopHocPhan) {
        if (maGiangVien <= 0 || maLopHocPhan <= 0) {
            throw new IllegalArgumentException("Thông tin lớp học phần không hợp lệ.");
        }
        return registrationResultDAO.autoAssignUnregisteredStudents(maGiangVien, maLopHocPhan);
    }
}
