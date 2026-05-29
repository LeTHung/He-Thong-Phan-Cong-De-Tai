package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.CourseSectionSummary;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.CourseSectionService;
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

public class CourseSectionManagementController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<CourseSectionSummary> tblCourseSections;

    @FXML
    private TableColumn<CourseSectionSummary, String> colCode;

    @FXML
    private TableColumn<CourseSectionSummary, String> colName;

    @FXML
    private TableColumn<CourseSectionSummary, String> colSubject;

    @FXML
    private TableColumn<CourseSectionSummary, String> colSemester;

    @FXML
    private TableColumn<CourseSectionSummary, String> colLecturer;

    @FXML
    private TableColumn<CourseSectionSummary, String> colMaxSize;

    @FXML
    private TableColumn<CourseSectionSummary, String> colStatus;

    @FXML
    private TextField txtCode;

    @FXML
    private TextField txtName;

    @FXML
    private ComboBox<OptionItem> cboSubject;

    @FXML
    private ComboBox<OptionItem> cboSemester;

    @FXML
    private ComboBox<OptionItem> cboLecturer;

    @FXML
    private TextField txtMaxSize;

    @FXML
    private ComboBox<String> cboStatus;

    @FXML
    private TextArea txtNote;

    private final CourseSectionService courseSectionService = new CourseSectionService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý lớp học phần.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        setupForm();
        loadCourseSections();
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
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này sẽ làm ở ngày tiếp theo.");
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddCourseSection() {
        try {
            courseSectionService.createCourseSection(
                    txtCode.getText(),
                    txtName.getText(),
                    cboSubject.getValue(),
                    cboSemester.getValue(),
                    cboLecturer.getValue(),
                    txtMaxSize.getText(),
                    txtNote.getText(),
                    cboStatus.getValue());
            showMessage("Đã thêm lớp học phần.");
            clearForm();
            loadCourseSections();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleUpdateCourseSection() {
        CourseSectionSummary selectedSection = getSelectedSection();
        if (selectedSection == null) {
            showMessage("Vui lòng chọn lớp học phần cần sửa.");
            return;
        }

        try {
            courseSectionService.updateCourseSection(
                    selectedSection.getMaLopHocPhan(),
                    txtCode.getText(),
                    txtName.getText(),
                    cboSubject.getValue(),
                    cboSemester.getValue(),
                    cboLecturer.getValue(),
                    txtMaxSize.getText(),
                    txtNote.getText(),
                    cboStatus.getValue());
            showMessage("Đã cập nhật lớp học phần.");
            loadCourseSections();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleOpenCourseSection() {
        CourseSectionSummary selectedSection = getSelectedSection();
        if (selectedSection == null) {
            showMessage("Vui lòng chọn lớp học phần cần mở.");
            return;
        }

        try {
            courseSectionService.openCourseSection(selectedSection.getMaLopHocPhan());
            showMessage("Đã mở lớp học phần.");
            loadCourseSections();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleCloseCourseSection() {
        CourseSectionSummary selectedSection = getSelectedSection();
        if (selectedSection == null) {
            showMessage("Vui lòng chọn lớp học phần cần đóng.");
            return;
        }

        try {
            courseSectionService.closeCourseSection(selectedSection.getMaLopHocPhan());
            showMessage("Đã đóng lớp học phần.");
            loadCourseSections();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleArchiveCourseSection() {
        CourseSectionSummary selectedSection = getSelectedSection();
        if (selectedSection == null) {
            showMessage("Vui lòng chọn lớp học phần cần lưu trữ.");
            return;
        }

        try {
            courseSectionService.archiveCourseSection(selectedSection.getMaLopHocPhan());
            showMessage("Đã lưu trữ lớp học phần.");
            loadCourseSections();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleClearForm() {
        clearForm();
        showMessage("Đã xóa trắng dữ liệu đang nhập.");
    }

    @FXML
    private void handleRefreshCourseSections() {
        setupForm();
        loadCourseSections();
        showMessage("Đã làm mới danh sách lớp học phần.");
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaLop()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenLopHocPhan()));
        colSubject.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenMonHoc()));
        colSemester.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHocKyText()));
        colLecturer.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenGiangVien()));
        colMaxSize.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatMaxSize(data.getValue().getSiSoToiDa())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));

        tblCourseSections.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillForm(newValue);
            }
        });
    }

    private void setupForm() {
        cboStatus.getItems().setAll(
                CourseSectionService.STATUS_OPEN,
                CourseSectionService.STATUS_CLOSED,
                CourseSectionService.STATUS_ARCHIVED);
        cboStatus.setValue(CourseSectionService.STATUS_OPEN);

        try {
            cboSubject.getItems().setAll(courseSectionService.getSubjectOptions());
            cboSemester.getItems().setAll(courseSectionService.getSemesterOptions());
            cboLecturer.getItems().setAll(courseSectionService.getLecturerOptions());
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void loadCourseSections() {
        try {
            List<CourseSectionSummary> sections = courseSectionService.getAllCourseSections();
            tblCourseSections.getItems().setAll(sections);
        } catch (RuntimeException exception) {
            tblCourseSections.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void fillForm(CourseSectionSummary section) {
        txtCode.setText(section.getMaLop());
        txtName.setText(section.getTenLopHocPhan());
        selectOptionById(cboSubject, section.getMaMonHoc());
        selectOptionById(cboSemester, section.getMaHocKy());
        selectOptionById(cboLecturer, section.getMaGiangVien());
        txtMaxSize.setText(section.getSiSoToiDa() == null ? "" : String.valueOf(section.getSiSoToiDa()));
        txtNote.setText(section.getGhiChu() == null ? "" : section.getGhiChu());
        cboStatus.setValue(section.getTrangThai());
        showMessage("Đang chọn lớp học phần " + section.getMaLop() + ".");
    }

    private void clearForm() {
        tblCourseSections.getSelectionModel().clearSelection();
        txtCode.clear();
        txtName.clear();
        cboSubject.setValue(null);
        cboSemester.setValue(null);
        cboLecturer.setValue(null);
        txtMaxSize.clear();
        txtNote.clear();
        cboStatus.setValue(CourseSectionService.STATUS_OPEN);
    }

    private void selectOptionById(ComboBox<OptionItem> comboBox, int id) {
        comboBox.getItems().stream()
                .filter(option -> option.getId() == id)
                .findFirst()
                .ifPresent(comboBox::setValue);
    }

    private CourseSectionSummary getSelectedSection() {
        return tblCourseSections.getSelectionModel().getSelectedItem();
    }

    private String formatMaxSize(Integer maxSize) {
        return maxSize == null ? "" : String.valueOf(maxSize);
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
