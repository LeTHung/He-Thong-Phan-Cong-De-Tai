package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;

import java.util.List;

public class LecturerDashboardService {

    private final LecturerDashboardDAO lecturerDashboardDAO = new LecturerDashboardDAO();

    public List<LecturerCourseSectionSummary> getCourseSections(int maTaiKhoan) {
        return lecturerDashboardDAO.findCourseSectionsByAccountId(maTaiKhoan);
    }
}
