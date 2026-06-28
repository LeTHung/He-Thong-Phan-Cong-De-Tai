package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;

import java.util.List;

public class LecturerDashboardService {

    private final LecturerDashboardDAO lecturerDashboardDAO = new LecturerDashboardDAO();

    public String getLecturerName(int maTaiKhoan) {
        return lecturerDashboardDAO.findLecturerNameByAccountId(maTaiKhoan);
    }

    public List<LecturerCourseSectionSummary> getCourseSections(int maTaiKhoan) {
        return lecturerDashboardDAO.findCourseSectionsByAccountId(maTaiKhoan);
    }

    public int getTotalRegisteredStudents(int maTaiKhoan) {
        return lecturerDashboardDAO.getTotalRegisteredStudents(maTaiKhoan);
    }

    public String getRegistrationGateStatus(int maTaiKhoan) {
        return lecturerDashboardDAO.getRegistrationGateStatus(maTaiKhoan);
    }
}
