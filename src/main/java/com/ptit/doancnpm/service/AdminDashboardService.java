package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AdminDashboardDAO;
import com.ptit.doancnpm.model.dto.AdminDashboardSummary;
import com.ptit.doancnpm.model.dto.AdminDashboardTask;

import java.util.List;

public class AdminDashboardService {

    private final AdminDashboardDAO adminDashboardDAO = new AdminDashboardDAO();

    public AdminDashboardSummary getSummary() {
        return adminDashboardDAO.getSummary();
    }

    public List<AdminDashboardTask> getAdminTasks() {
        return List.of(
                new AdminDashboardTask("Tài khoản", "Lê Tiến Hưng", "Đã nối", "Thêm, sửa, khóa/mở khóa và reset mật khẩu demo."),
                new AdminDashboardTask("Môn học", "Lê Tiến Hưng", "Đã nối", "CRUD môn học và đổi trạng thái sử dụng."),
                new AdminDashboardTask("Học kỳ", "Lê Tiến Hưng", "Đã nối", "Tạo học kỳ, mở/đóng/nháp học kỳ."),
                new AdminDashboardTask("Lớp học phần", "Lê Tiến Hưng", "Đã nối", "Tạo lớp, gán môn học, học kỳ và giảng viên."),
                new AdminDashboardTask("Import sinh viên", "Lê Tiến Hưng", "Ngày 8", "Chưa thuộc phạm vi ngày 1-7."));
    }
}
