package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.LecturerClassStudentRow;

import java.util.List;

public class LecturerDashboardService {

    private final LecturerDashboardDAO lecturerDashboardDAO = new LecturerDashboardDAO();

    public String getLecturerName(int maTaiKhoan) {
        return lecturerDashboardDAO.findLecturerNameByAccountId(maTaiKhoan);
    }

    public List<LecturerCourseSectionSummary> getCourseSections(int maTaiKhoan) {
        return lecturerDashboardDAO.findCourseSectionsByAccountId(maTaiKhoan);
    }

    public List<LecturerClassStudentRow> getClassStudents(
            int maTaiKhoan, int maLopHocPhan) {
        if (maTaiKhoan <= 0 || maLopHocPhan <= 0) {
            throw new IllegalArgumentException("Tài khoản hoặc lớp học phần không hợp lệ.");
        }
        return lecturerDashboardDAO.findClassStudents(maTaiKhoan, maLopHocPhan);
    }

    public int getTotalRegisteredStudents(int maTaiKhoan) {
        return lecturerDashboardDAO.getTotalRegisteredStudents(maTaiKhoan);
    }

    public String getRegistrationGateStatus(int maTaiKhoan) {
        return lecturerDashboardDAO.getRegistrationGateStatus(maTaiKhoan);
    }
}
