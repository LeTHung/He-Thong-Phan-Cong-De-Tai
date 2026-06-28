package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.Subject;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.SubjectService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.List;

public class SubjectManagementController {

    private static final String ALL_STATUSES = "Tất cả trạng thái";

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<Subject> tblSubjects;

    @FXML
    private TableColumn<Subject, Void> colStt;

    @FXML
    private TableColumn<Subject, String> colCode;

    @FXML
    private TableColumn<Subject, String> colName;

    @FXML
    private TableColumn<Subject, String> colCredits;

    @FXML
    private TableColumn<Subject, String> colStatus;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnActivate;

    @FXML
    private Button btnDeactivate;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cboStatusFilter;

    private final SubjectService subjectService = new SubjectService();
    private final ObservableList<Subject> allSubjects = FXCollections.observableArrayList();
    private final FilteredList<Subject> filteredSubjects = new FilteredList<>(allSubjects, subject -> true);

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý môn học.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        setupFilters();
        loadSubjects();
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

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddSubject() {
        showSubjectDialog(null);
    }

    @FXML
    private void handleUpdateSubject() {
        Subject selectedSubject = getSelectedSubject();
        if (selectedSubject == null) {
            showMessage("Vui lòng chọn môn học cần sửa.");
            return;
        }

        showSubjectDialog(selectedSubject);
    }

    @FXML
    private void handleActivateSubject() {
        Subject selectedSubject = getSelectedSubject();
        if (selectedSubject == null) {
            showMessage("Vui lòng chọn môn học cần mở sử dụng.");
            return;
        }

        subjectService.activateSubject(selectedSubject.getMaMonHoc());
        showMessage("Đã mở sử dụng môn học.");
        loadSubjects();
    }

    @FXML
    private void handleDeactivateSubject() {
        Subject selectedSubject = getSelectedSubject();
        if (selectedSubject == null) {
            showMessage("Vui lòng chọn môn học cần ngừng sử dụng.");
            return;
        }

        subjectService.deactivateSubject(selectedSubject.getMaMonHoc());
        showMessage("Đã ngừng sử dụng môn học.");
        loadSubjects();
    }

    @FXML
    private void handleRefreshSubjects() {
        loadSubjects();
        showMessage("Đã làm mới danh sách môn học.");
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        cboStatusFilter.setValue(ALL_STATUSES);
    }

    private void setupTable() {
        tblSubjects.setItems(filteredSubjects);
        tblSubjects.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colStt.setCellFactory(TableCells.indexColumn());
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaMonHocHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenMonHoc()));
        colCredits.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoTinChi())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                formatStatus(data.getValue().getTrangThai())));

        btnEdit.disableProperty().bind(tblSubjects.getSelectionModel().selectedItemProperty().isNull());
        btnActivate.disableProperty().bind(tblSubjects.getSelectionModel().selectedItemProperty().isNull());
        btnDeactivate.disableProperty().bind(tblSubjects.getSelectionModel().selectedItemProperty().isNull());

        tblSubjects.setRowFactory(table -> {
            TableRow<Subject> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    tblSubjects.getSelectionModel().select(row.getItem());
                    handleUpdateSubject();
                }
            });
            return row;
        });
    }

    private void setupFilters() {
        cboStatusFilter.getItems().setAll(ALL_STATUSES, "Đang sử dụng", "Ngừng sử dụng");
        cboStatusFilter.setValue(ALL_STATUSES);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cboStatusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        String status = cboStatusFilter.getValue();
        filteredSubjects.setPredicate(subject ->
                AdminFilterSupport.contains(
                        txtSearch.getText(),
                        subject.getMaMonHocHeThong(),
                        subject.getTenMonHoc(),
                        subject.getMoTa())
                        && (ALL_STATUSES.equals(status) || formatStatus(subject.getTrangThai()).equals(status)));
    }

    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            allSubjects.setAll(subjects);
        } catch (RuntimeException exception) {
            allSubjects.clear();
            showMessage(exception.getMessage());
        }
    }

    private void showSubjectDialog(Subject subject) {
        boolean isEdit = subject != null;
        TextField codeField = new TextField(isEdit ? subject.getMaMonHocHeThong() : "");
        codeField.setPromptText("VD: CNPM");
        codeField.setPrefWidth(320);
        TextField nameField = new TextField(isEdit ? subject.getTenMonHoc() : "");
        nameField.setPromptText("Công nghệ phần mềm");
        TextField creditsField = new TextField(isEdit ? String.valueOf(subject.getSoTinChi()) : "");
        creditsField.setPromptText("3");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(SubjectService.STATUS_ACTIVE, SubjectService.STATUS_INACTIVE);
        statusBox.setValue(isEdit ? subject.getTrangThai() : SubjectService.STATUS_ACTIVE);
        statusBox.setMaxWidth(Double.MAX_VALUE);
        TextArea descriptionArea = new TextArea(isEdit && subject.getMoTa() != null ? subject.getMoTa() : "");
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setWrapText(true);

        GridPane form = AdminFormDialog.createForm();
        AdminFormDialog.addRow(form, 0, "Mã môn", codeField);
        AdminFormDialog.addRow(form, 1, "Tên môn học", nameField);
        AdminFormDialog.addRow(form, 2, "Số tín chỉ", creditsField);
        AdminFormDialog.addRow(form, 3, "Trạng thái", statusBox);
        AdminFormDialog.addRow(form, 4, "Mô tả", descriptionArea);

        boolean saved = AdminFormDialog.show(
                tblSubjects.getScene().getWindow(),
                isEdit ? "Sửa môn học" : "Thêm môn học",
                isEdit ? "Chỉnh sửa môn học " + subject.getMaMonHocHeThong() : "Nhập thông tin môn học mới",
                isEdit ? "Lưu thay đổi" : "Thêm môn học",
                form,
                () -> {
                    if (isEdit) {
                        subjectService.updateSubject(subject.getMaMonHoc(), codeField.getText(), nameField.getText(),
                                creditsField.getText(), descriptionArea.getText(), statusBox.getValue());
                    } else {
                        subjectService.createSubject(codeField.getText(), nameField.getText(), creditsField.getText(),
                                descriptionArea.getText(), statusBox.getValue());
                    }
                });

        if (saved) {
            loadSubjects();
            tblSubjects.getSelectionModel().clearSelection();
            showMessage(isEdit ? "Đã cập nhật môn học." : "Đã thêm môn học.");
        }
    }

    private Subject getSelectedSubject() {
        return tblSubjects.getSelectionModel().getSelectedItem();
    }

    private String formatStatus(String status) {
        return SubjectService.STATUS_ACTIVE.equals(status) ? "Đang sử dụng" : "Ngừng sử dụng";
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
