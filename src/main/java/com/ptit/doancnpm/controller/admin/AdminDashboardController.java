package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AdminDashboardSummary;
import com.ptit.doancnpm.model.dto.AdminDashboardTask;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AdminDashboardService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    private TableView<AdminDashboardTask> tblAdminTasks;

    @FXML
    private TableColumn<AdminDashboardTask, String> colModule;

    @FXML
    private TableColumn<AdminDashboardTask, String> colOwner;

    @FXML
    private TableColumn<AdminDashboardTask, String> colStatus;

    @FXML
    private TableColumn<AdminDashboardTask, String> colNote;

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
        setupTaskTable();
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
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này sẽ làm ở ngày tiếp theo.");
    }

    private void setupTaskTable() {
        colModule.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getModule()));
        colOwner.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOwner()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatus()));
        colNote.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNote()));
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
        } catch (RuntimeException exception) {
            lblAccountNote.setText(exception.getMessage());
        }

        tblAdminTasks.getItems().setAll(adminDashboardService.getAdminTasks());
    }

    private String formatCount(int count) {
        return String.format("%02d", count);
    }
}
