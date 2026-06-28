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
    private Button btnEdit;

    @FXML
    private Button btnOpen;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnArchive;

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
    private void handleAddCourseSection() {
        showCourseSectionDialog(null);
    }

    @FXML
    private void handleUpdateCourseSection() {
        CourseSectionSummary selectedSection = getSelectedSection();
        if (selectedSection == null) {
            showMessage("Vui lòng chọn lớp học phần cần sửa.");
            return;
        }

        showCourseSectionDialog(selectedSection);
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
    private void handleRefreshCourseSections() {
        loadCourseSections();
        showMessage("Đã làm mới danh sách lớp học phần.");
    }

    private void setupTable() {
        tblCourseSections.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaLop()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenLopHocPhan()));
        colSubject.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenMonHoc()));
        colSemester.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHocKyText()));
        colLecturer.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenGiangVien()));
        colMaxSize.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatMaxSize(data.getValue().getSiSoToiDa())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));

        btnEdit.disableProperty().bind(tblCourseSections.getSelectionModel().selectedItemProperty().isNull());
        btnOpen.disableProperty().bind(tblCourseSections.getSelectionModel().selectedItemProperty().isNull());
        btnClose.disableProperty().bind(tblCourseSections.getSelectionModel().selectedItemProperty().isNull());
        btnArchive.disableProperty().bind(tblCourseSections.getSelectionModel().selectedItemProperty().isNull());

        tblCourseSections.setRowFactory(table -> {
            TableRow<CourseSectionSummary> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    tblCourseSections.getSelectionModel().select(row.getItem());
                    handleUpdateCourseSection();
                }
            });
            return row;
        });
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

    private void showCourseSectionDialog(CourseSectionSummary section) {
        boolean isEdit = section != null;
        try {
            TextField codeField = new TextField(isEdit ? section.getMaLop() : "");
            codeField.setPromptText("VD: CNPM_D23CQCN01_N");
            codeField.setPrefWidth(340);
            TextField nameField = new TextField(isEdit ? section.getTenLopHocPhan() : "");
            nameField.setPromptText("Công nghệ phần mềm - D23CQCN01-N");

            ComboBox<OptionItem> subjectBox = new ComboBox<>();
            subjectBox.getItems().setAll(courseSectionService.getSubjectOptions());
            subjectBox.setMaxWidth(Double.MAX_VALUE);
            ComboBox<OptionItem> semesterBox = new ComboBox<>();
            semesterBox.getItems().setAll(courseSectionService.getSemesterOptions());
            semesterBox.setMaxWidth(Double.MAX_VALUE);
            ComboBox<OptionItem> lecturerBox = new ComboBox<>();
            lecturerBox.getItems().setAll(courseSectionService.getLecturerOptions());
            lecturerBox.setMaxWidth(Double.MAX_VALUE);

            TextField maxSizeField = new TextField(
                    isEdit && section.getSiSoToiDa() != null ? String.valueOf(section.getSiSoToiDa()) : "");
            maxSizeField.setPromptText("VD: 80");
            ComboBox<String> statusBox = new ComboBox<>();
            statusBox.getItems().setAll(CourseSectionService.STATUS_OPEN, CourseSectionService.STATUS_CLOSED,
                    CourseSectionService.STATUS_ARCHIVED);
            statusBox.setValue(isEdit ? section.getTrangThai() : CourseSectionService.STATUS_OPEN);
            statusBox.setMaxWidth(Double.MAX_VALUE);
            TextArea noteArea = new TextArea(isEdit && section.getGhiChu() != null ? section.getGhiChu() : "");
            noteArea.setPrefRowCount(3);
            noteArea.setWrapText(true);

            if (isEdit) {
                selectOptionById(subjectBox, section.getMaMonHoc());
                selectOptionById(semesterBox, section.getMaHocKy());
                selectOptionById(lecturerBox, section.getMaGiangVien());
            }

            GridPane form = AdminFormDialog.createForm();
            AdminFormDialog.addRow(form, 0, "Mã lớp", codeField);
            AdminFormDialog.addRow(form, 1, "Tên lớp học phần", nameField);
            AdminFormDialog.addRow(form, 2, "Môn học", subjectBox);
            AdminFormDialog.addRow(form, 3, "Học kỳ", semesterBox);
            AdminFormDialog.addRow(form, 4, "Giảng viên", lecturerBox);
            AdminFormDialog.addRow(form, 5, "Sĩ số tối đa", maxSizeField);
            AdminFormDialog.addRow(form, 6, "Trạng thái", statusBox);
            AdminFormDialog.addRow(form, 7, "Ghi chú", noteArea);

            boolean saved = AdminFormDialog.show(
                    tblCourseSections.getScene().getWindow(),
                    isEdit ? "Sửa lớp học phần" : "Thêm lớp học phần",
                    isEdit ? "Chỉnh sửa lớp " + section.getMaLop() : "Nhập thông tin lớp học phần mới",
                    isEdit ? "Lưu thay đổi" : "Thêm lớp học phần",
                    form,
                    () -> {
                        if (isEdit) {
                            courseSectionService.updateCourseSection(section.getMaLopHocPhan(), codeField.getText(),
                                    nameField.getText(), subjectBox.getValue(), semesterBox.getValue(),
                                    lecturerBox.getValue(), maxSizeField.getText(), noteArea.getText(),
                                    statusBox.getValue());
                        } else {
                            courseSectionService.createCourseSection(codeField.getText(), nameField.getText(),
                                    subjectBox.getValue(), semesterBox.getValue(), lecturerBox.getValue(),
                                    maxSizeField.getText(), noteArea.getText(), statusBox.getValue());
                        }
                    });

            if (saved) {
                loadCourseSections();
                tblCourseSections.getSelectionModel().clearSelection();
                showMessage(isEdit ? "Đã cập nhật lớp học phần." : "Đã thêm lớp học phần.");
            }
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
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
