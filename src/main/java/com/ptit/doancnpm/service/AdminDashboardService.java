package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AdminDashboardDAO;
import com.ptit.doancnpm.model.dto.AdminDashboardSummary;

public class AdminDashboardService {

    private final AdminDashboardDAO adminDashboardDAO = new AdminDashboardDAO();

    public AdminDashboardSummary getSummary() {
        return adminDashboardDAO.getSummary();
    }

    public String getDemoDataStatus(AdminDashboardSummary summary) {
        if (summary.getTotalAccounts() < 5) {
            return "Thiếu tài khoản mẫu. Cần chạy lại sql/schema.sql.";
        }
        if (summary.getTotalStudents() < 3) {
            return "Thiếu dữ liệu 3 sinh viên nhóm.";
        }
        if (summary.getTotalLecturers() < 1) {
            return "Thiếu giảng viên mẫu.";
        }
        if (summary.getTotalCourseSections() < 1) {
            return "Thiếu lớp học phần mẫu.";
        }
        return "Dữ liệu demo Admin ổn: tài khoản, sinh viên, giảng viên và lớp học phần đã sẵn sàng.";
    }
}
