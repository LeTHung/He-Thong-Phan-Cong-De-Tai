package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AdminCourseSectionReport;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AdminReportService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminReportController {

    private static final String ALL_PROGRESS = "Tất cả tình trạng";
    private static final String MISSING_TOPICS = "Còn thiếu đề tài";
    private static final String COMPLETE = "Đã đủ đề tài";

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<AdminCourseSectionReport> tblReports;

    @FXML
    private TableColumn<AdminCourseSectionReport, Void> colStt;

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

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cboProgressFilter;

    private final AdminReportService adminReportService = new AdminReportService();
    private final ObservableList<AdminCourseSectionReport> allReports = FXCollections.observableArrayList();
    private final FilteredList<AdminCourseSectionReport> filteredReports =
            new FilteredList<>(allReports, report -> true);

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
        setupFilters();
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
    private void handleClearFilters() {
        txtSearch.clear();
        cboProgressFilter.setValue(ALL_PROGRESS);
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private void setupTable() {
        tblReports.setItems(filteredReports);
        tblReports.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colStt.setCellFactory(TableCells.indexColumn());
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaLop()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenLopHocPhan()));
        colStudentCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getTongSoSinhVien())));
        colRegisteredCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoSinhVienDaCoDeTai())));
        colMissingCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoSinhVienChuaCoDeTai())));
        colTopicCount.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getTongSoDeTai())));
    }

    private void setupFilters() {
        cboProgressFilter.getItems().setAll(ALL_PROGRESS, MISSING_TOPICS, COMPLETE);
        cboProgressFilter.setValue(ALL_PROGRESS);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cboProgressFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String progress = cboProgressFilter.getValue();
        filteredReports.setPredicate(report -> {
            boolean matchesSearch = AdminFilterSupport.contains(
                    txtSearch.getText(), report.getMaLop(), report.getTenLopHocPhan());
            boolean matchesProgress = ALL_PROGRESS.equals(progress)
                    || (MISSING_TOPICS.equals(progress) && report.getSoSinhVienChuaCoDeTai() > 0)
                    || (COMPLETE.equals(progress) && report.getSoSinhVienChuaCoDeTai() == 0);
            return matchesSearch && matchesProgress;
        });
    }

    private void loadReports() {
        try {
            List<AdminCourseSectionReport> reports = adminReportService.getCourseSectionReports();
            allReports.setAll(reports);
            lblMessage.setText("Đã tải " + reports.size() + " dòng báo cáo lớp học phần.");
        } catch (RuntimeException exception) {
            allReports.clear();
            lblMessage.setText(exception.getMessage());
        }
    }
}
