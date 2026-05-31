package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.SemesterSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.SemesterService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SemesterManagementController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<SemesterSummary> tblSemesters;

    @FXML
    private TableColumn<SemesterSummary, String> colCode;

    @FXML
    private TableColumn<SemesterSummary, String> colName;

    @FXML
    private TableColumn<SemesterSummary, String> colSchoolYear;

    @FXML
    private TableColumn<SemesterSummary, String> colStartDate;

    @FXML
    private TableColumn<SemesterSummary, String> colEndDate;

    @FXML
    private TableColumn<SemesterSummary, String> colStatus;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSchoolYear;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private ComboBox<String> cboStatus;

    private final SemesterService semesterService = new SemesterService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý học kỳ.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        setupForm();
        loadSemesters();
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

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddSemester() {
        try {
            semesterService.createSemester(
                    txtCode.getText(),
                    txtName.getText(),
                    txtSchoolYear.getText(),
                    dpStartDate.getValue(),
                    dpEndDate.getValue(),
                    cboStatus.getValue());
            showMessage("Đã thêm học kỳ.");
            clearForm();
            loadSemesters();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleUpdateSemester() {
        SemesterSummary selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần sửa.");
            return;
        }

        try {
            semesterService.updateSemester(
                    selectedSemester.getMaHocKy(),
                    txtCode.getText(),
                    txtName.getText(),
                    txtSchoolYear.getText(),
                    dpStartDate.getValue(),
                    dpEndDate.getValue(),
                    cboStatus.getValue());
            showMessage("Đã cập nhật học kỳ.");
            loadSemesters();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleOpenSemester() {
        SemesterSummary selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần mở.");
            return;
        }

        semesterService.openSemester(selectedSemester.getMaHocKy());
        showMessage("Đã mở học kỳ.");
        loadSemesters();
    }

    @FXML
    private void handleCloseSemester() {
        SemesterSummary selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần đóng.");
            return;
        }

        semesterService.closeSemester(selectedSemester.getMaHocKy());
        showMessage("Đã đóng học kỳ.");
        loadSemesters();
    }

    @FXML
    private void handleDraftSemester() {
        SemesterSummary selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần chuyển về nháp.");
            return;
        }

        semesterService.draftSemester(selectedSemester.getMaHocKy());
        showMessage("Đã chuyển học kỳ về nháp.");
        loadSemesters();
    }

    @FXML
    private void handleClearForm() {
        clearForm();
        showMessage("Đã xóa trắng dữ liệu đang nhập.");
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaHocKyHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenHocKy()));
        colSchoolYear.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNamHoc()));
        colStartDate.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getNgayBatDau())));
        colEndDate.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getNgayKetThuc())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));

        tblSemesters.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillForm(newValue);
            }
        });
    }

    private void setupForm() {
        cboStatus.getItems().setAll(
                SemesterService.STATUS_DRAFT,
                SemesterService.STATUS_OPEN,
                SemesterService.STATUS_CLOSED);
        cboStatus.setValue(SemesterService.STATUS_DRAFT);
    }

    private void loadSemesters() {
        try {
            List<SemesterSummary> semesters = semesterService.getAllSemesters();
            tblSemesters.getItems().setAll(semesters);
        } catch (RuntimeException exception) {
            tblSemesters.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void fillForm(SemesterSummary semester) {
        txtCode.setText(semester.getMaHocKyHeThong());
        txtName.setText(semester.getTenHocKy());
        txtSchoolYear.setText(semester.getNamHoc());
        dpStartDate.setValue(semester.getNgayBatDau());
        dpEndDate.setValue(semester.getNgayKetThuc());
        cboStatus.setValue(semester.getTrangThai());
        showMessage("Đang chọn học kỳ " + semester.getMaHocKyHeThong() + ".");
    }

    private void clearForm() {
        tblSemesters.getSelectionModel().clearSelection();
        txtCode.clear();
        txtName.clear();
        txtSchoolYear.clear();
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
        cboStatus.setValue(SemesterService.STATUS_DRAFT);
    }

    private SemesterSummary getSelectedSemester() {
        return tblSemesters.getSelectionModel().getSelectedItem();
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
