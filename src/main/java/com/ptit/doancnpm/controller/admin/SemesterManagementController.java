package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.Semester;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.SemesterService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SemesterManagementController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<Semester> tblSemesters;

    @FXML
    private TableColumn<Semester, String> colCode;

    @FXML
    private TableColumn<Semester, String> colName;

    @FXML
    private TableColumn<Semester, String> colSchoolYear;

    @FXML
    private TableColumn<Semester, String> colStartDate;

    @FXML
    private TableColumn<Semester, String> colEndDate;

    @FXML
    private TableColumn<Semester, String> colStatus;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDraft;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnClose;

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
        showSemesterDialog(null);
    }

    @FXML
    private void handleUpdateSemester() {
        Semester selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần sửa.");
            return;
        }

        showSemesterDialog(selectedSemester);
    }

    @FXML
    private void handleOpenSemester() {
        Semester selectedSemester = getSelectedSemester();
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
        Semester selectedSemester = getSelectedSemester();
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
        Semester selectedSemester = getSelectedSemester();
        if (selectedSemester == null) {
            showMessage("Vui lòng chọn học kỳ cần chuyển về nháp.");
            return;
        }

        semesterService.draftSemester(selectedSemester.getMaHocKy());
        showMessage("Đã chuyển học kỳ về nháp.");
        loadSemesters();
    }

    @FXML
    private void handleRefreshSemesters() {
        loadSemesters();
        showMessage("Đã làm mới danh sách học kỳ.");
    }

    private void setupTable() {
        tblSemesters.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaHocKyHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenHocKy()));
        colSchoolYear.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNamHoc()));
        colStartDate.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getNgayBatDau())));
        colEndDate.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDate(data.getValue().getNgayKetThuc())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                formatStatus(data.getValue().getTrangThai())));

        btnEdit.disableProperty().bind(tblSemesters.getSelectionModel().selectedItemProperty().isNull());
        btnDraft.disableProperty().bind(tblSemesters.getSelectionModel().selectedItemProperty().isNull());
        btnOpen.disableProperty().bind(tblSemesters.getSelectionModel().selectedItemProperty().isNull());
        btnClose.disableProperty().bind(tblSemesters.getSelectionModel().selectedItemProperty().isNull());

        tblSemesters.setRowFactory(table -> {
            TableRow<Semester> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    tblSemesters.getSelectionModel().select(row.getItem());
                    handleUpdateSemester();
                }
            });
            return row;
        });
    }

    private void loadSemesters() {
        try {
            List<Semester> semesters = semesterService.getAllSemesters();
            tblSemesters.getItems().setAll(semesters);
        } catch (RuntimeException exception) {
            tblSemesters.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void showSemesterDialog(Semester semester) {
        boolean isEdit = semester != null;
        TextField codeField = new TextField(isEdit ? semester.getMaHocKyHeThong() : "");
        codeField.setPromptText("VD: HK2_2025_2026");
        codeField.setPrefWidth(320);
        TextField nameField = new TextField(isEdit ? semester.getTenHocKy() : "");
        nameField.setPromptText("Học kỳ 2");
        TextField schoolYearField = new TextField(isEdit ? semester.getNamHoc() : "");
        schoolYearField.setPromptText("2025-2026");
        DatePicker startDatePicker = new DatePicker(isEdit ? semester.getNgayBatDau() : null);
        startDatePicker.setMaxWidth(Double.MAX_VALUE);
        DatePicker endDatePicker = new DatePicker(isEdit ? semester.getNgayKetThuc() : null);
        endDatePicker.setMaxWidth(Double.MAX_VALUE);
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(SemesterService.STATUS_DRAFT, SemesterService.STATUS_OPEN,
                SemesterService.STATUS_CLOSED);
        statusBox.setValue(isEdit ? semester.getTrangThai() : SemesterService.STATUS_DRAFT);
        statusBox.setMaxWidth(Double.MAX_VALUE);

        GridPane form = AdminFormDialog.createForm();
        AdminFormDialog.addRow(form, 0, "Mã học kỳ", codeField);
        AdminFormDialog.addRow(form, 1, "Tên học kỳ", nameField);
        AdminFormDialog.addRow(form, 2, "Năm học", schoolYearField);
        AdminFormDialog.addRow(form, 3, "Ngày bắt đầu", startDatePicker);
        AdminFormDialog.addRow(form, 4, "Ngày kết thúc", endDatePicker);
        AdminFormDialog.addRow(form, 5, "Trạng thái", statusBox);

        boolean saved = AdminFormDialog.show(
                tblSemesters.getScene().getWindow(),
                isEdit ? "Sửa học kỳ" : "Thêm học kỳ",
                isEdit ? "Chỉnh sửa học kỳ " + semester.getMaHocKyHeThong() : "Nhập thông tin học kỳ mới",
                isEdit ? "Lưu thay đổi" : "Thêm học kỳ",
                form,
                () -> {
                    if (isEdit) {
                        semesterService.updateSemester(semester.getMaHocKy(), codeField.getText(), nameField.getText(),
                                schoolYearField.getText(), startDatePicker.getValue(), endDatePicker.getValue(),
                                statusBox.getValue());
                    } else {
                        semesterService.createSemester(codeField.getText(), nameField.getText(),
                                schoolYearField.getText(), startDatePicker.getValue(), endDatePicker.getValue(),
                                statusBox.getValue());
                    }
                });

        if (saved) {
            loadSemesters();
            tblSemesters.getSelectionModel().clearSelection();
            showMessage(isEdit ? "Đã cập nhật học kỳ." : "Đã thêm học kỳ.");
        }
    }

    private Semester getSelectedSemester() {
        return tblSemesters.getSelectionModel().getSelectedItem();
    }

    private String formatStatus(String status) {
        return switch (status == null ? "" : status) {
            case SemesterService.STATUS_OPEN -> "Đang mở";
            case SemesterService.STATUS_CLOSED -> "Đã đóng";
            default -> "Nháp";
        };
    }

    private String formatDate(java.time.LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
