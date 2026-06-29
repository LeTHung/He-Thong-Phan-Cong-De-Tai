package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AdminReportDAO;
import com.ptit.doancnpm.model.dto.AdminCourseSectionReport;

import java.util.List;

public class AdminReportService {

    private final AdminReportDAO adminReportDAO = new AdminReportDAO();

    public List<AdminCourseSectionReport> getCourseSectionReports() {
        return adminReportDAO.findCourseSectionReports();
    }
}
