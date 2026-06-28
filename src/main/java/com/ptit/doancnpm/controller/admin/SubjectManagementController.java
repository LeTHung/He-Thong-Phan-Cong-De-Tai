package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.Subject;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.SubjectService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class SubjectManagementController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<Subject> tblSubjects;

    @FXML
    private TableColumn<Subject, String> colCode;

    @FXML
    private TableColumn<Subject, String> colName;

    @FXML
    private TableColumn<Subject, String> colCredits;

    @FXML
    private TableColumn<Subject, String> colStatus;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtCredits;

    @FXML
    private TextArea txtDescription;

    @FXML
    private ComboBox<String> cboStatus;

    private final SubjectService subjectService = new SubjectService();

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
        setupForm();
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
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddSubject() {
        try {
            subjectService.createSubject(
                    txtCode.getText(),
                    txtName.getText(),
                    txtCredits.getText(),
                    txtDescription.getText(),
                    cboStatus.getValue());
            showMessage("Đã thêm môn học.");
            clearForm();
            loadSubjects();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleUpdateSubject() {
        Subject selectedSubject = getSelectedSubject();
        if (selectedSubject == null) {
            showMessage("Vui lòng chọn môn học cần sửa.");
            return;
        }

        try {
            subjectService.updateSubject(
                    selectedSubject.getMaMonHoc(),
                    txtCode.getText(),
                    txtName.getText(),
                    txtCredits.getText(),
                    txtDescription.getText(),
                    cboStatus.getValue());
            showMessage("Đã cập nhật môn học.");
            loadSubjects();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
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
    private void handleClearForm() {
        clearForm();
        showMessage("Đã xóa trắng dữ liệu đang nhập.");
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaMonHocHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenMonHoc()));
        colCredits.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getSoTinChi())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                formatStatus(data.getValue().getTrangThai())));

        tblSubjects.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillForm(newValue);
            }
        });
    }

    private void setupForm() {
        cboStatus.getItems().setAll(SubjectService.STATUS_ACTIVE, SubjectService.STATUS_INACTIVE);
        cboStatus.setValue(SubjectService.STATUS_ACTIVE);
    }

    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            tblSubjects.getItems().setAll(subjects);
        } catch (RuntimeException exception) {
            tblSubjects.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void fillForm(Subject subject) {
        txtCode.setText(subject.getMaMonHocHeThong());
        txtName.setText(subject.getTenMonHoc());
        txtCredits.setText(String.valueOf(subject.getSoTinChi()));
        txtDescription.setText(subject.getMoTa() == null ? "" : subject.getMoTa());
        cboStatus.setValue(subject.getTrangThai());
        showMessage("Đang chọn môn học " + subject.getMaMonHocHeThong() + ".");
    }

    private void clearForm() {
        tblSubjects.getSelectionModel().clearSelection();
        txtCode.clear();
        txtName.clear();
        txtCredits.clear();
        txtDescription.clear();
        cboStatus.setValue(SubjectService.STATUS_ACTIVE);
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
