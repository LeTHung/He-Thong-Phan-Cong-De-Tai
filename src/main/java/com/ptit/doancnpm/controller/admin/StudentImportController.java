package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.StudentClassService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.util.List;

public class StudentImportController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private ComboBox<OptionItem> cboCourseSection;

    @FXML
    private TableView<StudentClassMemberSummary> tblStudents;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colStudentCode;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colFullName;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colEmail;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colClassName;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colStatus;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colNote;

    @FXML
    private ComboBox<OptionItem> cboStudent;

    @FXML
    private TextArea txtNote;

    private final StudentClassService studentClassService = new StudentClassService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý sinh viên lớp.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        setupForm();
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
    private void handleShowReports() {
        MainApp.setRoot(MainApp.ADMIN_REPORT_VIEW);
    }

    @FXML
    private void handleNotImplemented() {
        MainApp.showInfo("Import Excel sẽ được bổ sung sau khi thống nhất mẫu file.");
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddStudent() {
        try {
            studentClassService.addStudentToCourseSection(
                    cboCourseSection.getValue(),
                    cboStudent.getValue(),
                    txtNote.getText());
            showMessage("Đã thêm sinh viên vào lớp.");
            txtNote.clear();
            loadStudents();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleWithdrawStudent() {
        try {
            studentClassService.withdrawStudentFromCourseSection(
                    cboCourseSection.getValue(),
                    tblStudents.getSelectionModel().getSelectedItem());
            showMessage("Đã rút sinh viên khỏi lớp.");
            loadStudents();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleRefreshStudents() {
        setupForm();
        loadStudents();
        showMessage("Đã làm mới dữ liệu sinh viên lớp.");
    }

    private void setupTable() {
        colStudentCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaSoSinhVien()));
        colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHoTen()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getEmail())));
        colClassName.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getLopSinhHoat())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));
        colNote.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getGhiChu())));
    }

    private void setupForm() {
        try {
            List<OptionItem> courseSections = studentClassService.getCourseSectionOptions();
            OptionItem selectedCourseSection = cboCourseSection.getValue();
            cboCourseSection.getItems().setAll(courseSections);
            selectOrFirst(cboCourseSection, selectedCourseSection);

            OptionItem selectedStudent = cboStudent.getValue();
            cboStudent.getItems().setAll(studentClassService.getStudentOptions());
            selectOrFirst(cboStudent, selectedStudent);

            cboCourseSection.setOnAction(event -> loadStudents());
            loadStudents();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void loadStudents() {
        OptionItem courseSection = cboCourseSection.getValue();
        if (courseSection == null) {
            tblStudents.getItems().clear();
            return;
        }

        try {
            tblStudents.getItems().setAll(studentClassService.getStudentsByCourseSection(courseSection));
        } catch (RuntimeException exception) {
            tblStudents.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void selectOrFirst(ComboBox<OptionItem> comboBox, OptionItem previousValue) {
        if (previousValue != null) {
            comboBox.getItems().stream()
                    .filter(option -> option.getId() == previousValue.getId())
                    .findFirst()
                    .ifPresent(comboBox::setValue);
        }

        if (comboBox.getValue() == null && !comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(0));
        }
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
