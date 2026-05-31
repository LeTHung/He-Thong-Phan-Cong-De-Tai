package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AdminDashboardSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AdminDashboardService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblAccountCount;

    @FXML
    private Label lblAccountNote;

    @FXML
    private Label lblStudentCount;

    @FXML
    private Label lblStudentNote;

    @FXML
    private Label lblLecturerCount;

    @FXML
    private Label lblLecturerNote;

    @FXML
    private Label lblCourseSectionCount;

    @FXML
    private Label lblCourseSectionNote;

    @FXML
    private Label lblDataStatus;

    private final AdminDashboardService adminDashboardService = new AdminDashboardService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình quản trị viên.");
            MainApp.showLogin();
            return;
        }

        lblWelcome.setText("Xin chào, " + user.getTenDangNhap());
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        loadDashboardData();
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleShowAccounts() {
        MainApp.setRoot(MainApp.ACCOUNT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleCreateAccount() {
        MainApp.setRoot(MainApp.ACCOUNT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowSubjects() {
        MainApp.setRoot(MainApp.SUBJECT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowSemesters() {
        MainApp.setRoot(MainApp.SEMESTER_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowCourseSections() {
        MainApp.setRoot(MainApp.COURSE_SECTION_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowStudentImport() {
        MainApp.setRoot(MainApp.STUDENT_IMPORT_VIEW);
    }

    @FXML
    private void handleShowReports() {
        MainApp.setRoot(MainApp.ADMIN_REPORT_VIEW);
    }

    @FXML
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này sẽ làm ở ngày tiếp theo.");
    }

    private void loadDashboardData() {
        try {
            AdminDashboardSummary summary = adminDashboardService.getSummary();
            lblAccountCount.setText(formatCount(summary.getTotalAccounts()));
            lblAccountNote.setText(summary.getActiveAccounts() + " đang hoạt động");
            lblStudentCount.setText(formatCount(summary.getTotalStudents()));
            lblStudentNote.setText(summary.getActiveStudents() + " đang học");
            lblLecturerCount.setText(formatCount(summary.getTotalLecturers()));
            lblLecturerNote.setText(summary.getActiveLecturers() + " đang công tác");
            lblCourseSectionCount.setText(formatCount(summary.getTotalCourseSections()));
            lblCourseSectionNote.setText(summary.getOpenCourseSections() + " đang mở");
            lblDataStatus.setText(adminDashboardService.getDemoDataStatus(summary));
        } catch (RuntimeException exception) {
            lblAccountNote.setText(exception.getMessage());
            lblDataStatus.setText("Không kiểm tra được dữ liệu demo: " + exception.getMessage());
        }
    }

    @FXML
    private void handleRefreshDashboard() {
        loadDashboardData();
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }
}
