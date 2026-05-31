package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AdminCourseSectionReport;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AdminReportService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class AdminReportController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<AdminCourseSectionReport> tblReports;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colCode;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colName;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colStudentCount;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colRegisteredCount;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colMissingCount;

    @FXML
    private TableColumn<AdminCourseSectionReport, String> colTopicCount;

    private final AdminReportService adminReportService = new AdminReportService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập báo cáo quản trị.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        loadReports();
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.ADMIN_DASHBOARD_VIEW);
    }

    @FXML
    private void handleShowAccounts() {
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
    private void handleRefreshReports() {
        loadReports();
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaLop()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenLopHocPhan()));
        colStudentCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getTongSoSinhVien())));
        colRegisteredCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoSinhVienDaCoDeTai())));
        colMissingCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoSinhVienChuaCoDeTai())));
        colTopicCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getTongSoDeTai())));
    }

    private void loadReports() {
        try {
            List<AdminCourseSectionReport> reports = adminReportService.getCourseSectionReports();
            tblReports.getItems().setAll(reports);
            lblMessage.setText("Đã tải " + reports.size() + " dòng báo cáo lớp học phần.");
        } catch (RuntimeException exception) {
            tblReports.getItems().clear();
            lblMessage.setText(exception.getMessage());
        }
    }
}
