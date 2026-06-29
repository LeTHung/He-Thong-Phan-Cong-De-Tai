package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.dto.LecturerClassStudentRow;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.model.dto.UnregisteredStudentRow;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.service.RegistrationResultService;
import com.ptit.doancnpm.service.TopicBankService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import com.ptit.doancnpm.util.TextFormat;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class RegistrationResultController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private TabPane tabPane;

    // Tab 0 — Sinh viên
    @FXML private TextField txtSearchStudents;
    @FXML private TableView<LecturerClassStudentRow> tableStudents;
    @FXML private TableColumn<LecturerClassStudentRow, Void> colSStt;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSMaSV;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSHoTen;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSEmail;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSLopSH;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSMaDeTai;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSTenDeTai;
    @FXML private TableColumn<LecturerClassStudentRow, String> colSHinhThuc;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblWithTopicStudents;
    @FXML private Label lblWithoutTopicStudents;

    // Tab 1 — Đã đăng ký
    @FXML private TableView<RegistrationResultRow> tableRegistered;
    @FXML private TableColumn<RegistrationResultRow, Void> colStt;
    @FXML private TableColumn<RegistrationResultRow, String> colMaSV;
    @FXML private TableColumn<RegistrationResultRow, String> colTenSV;
    @FXML private TableColumn<RegistrationResultRow, String> colLopSH;
    @FXML private TableColumn<RegistrationResultRow, String> colMaDeTai;
    @FXML private TableColumn<RegistrationResultRow, String> colTenDeTai;
    @FXML private TableColumn<RegistrationResultRow, String> colHinhThuc;
    @FXML private TableColumn<RegistrationResultRow, String> colThoiGian;
    @FXML private Label lblRegisteredCount;

    // Tab 2 — Chưa đăng ký
    @FXML private TableView<UnregisteredStudentRow> tableUnregistered;
    @FXML private TableColumn<UnregisteredStudentRow, Void> colUStt;
    @FXML private TableColumn<UnregisteredStudentRow, String> colUMaSV;
    @FXML private TableColumn<UnregisteredStudentRow, String> colUTenSV;
    @FXML private TableColumn<UnregisteredStudentRow, String> colULopSH;
    @FXML private TableColumn<UnregisteredStudentRow, String> colUEmail;
    @FXML private Label lblUnregisteredCount;
    @FXML private ComboBox<UnregisteredStudentRow> cbUnregisteredStudent;
    @FXML private ComboBox<AssignedTopicRow> cbAssignableTopic;
    @FXML private Label lblAssignHint;

    private final LecturerDashboardService lecturerDashboardService = new LecturerDashboardService();
    private final RegistrationResultService registrationResultService = new RegistrationResultService();
    private final TopicBankService topicBankService = new TopicBankService();
    private int maGiangVien;
    private int maTaiKhoan;
    private List<LecturerClassStudentRow> allStudents = List.of();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }
        maTaiKhoan = user.getMaTaiKhoan();

        try {
            maGiangVien = topicBankService.findMaGiangVienByTaiKhoan(user.getMaTaiKhoan());
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        setupStudentsTable();
        setupRegisteredTable();
        setupUnregisteredTable();
        setupAssignmentCombos();
        loadSections(user.getMaTaiKhoan());
        cbSection.setOnAction(e -> handleRefresh());
        txtSearchStudents.textProperty().addListener((observable, oldValue, newValue) -> applyStudentsFilter());
        tableStudents.setPlaceholder(new Label("Lớp chưa có sinh viên đang học."));
        tableRegistered.setPlaceholder(new Label("Chưa có sinh viên nào có đề tài."));
        tableUnregistered.setPlaceholder(new Label("Tất cả sinh viên đã có đề tài."));
    }

    private void setupStudentsTable() {
        colSStt.setCellFactory(TableCells.indexColumn());
        colSMaSV.setCellValueFactory(row -> studentValue(row.getValue().maSoSinhVien()));
        colSHoTen.setCellValueFactory(row -> studentValue(row.getValue().hoTen()));
        colSEmail.setCellValueFactory(row -> studentValue(row.getValue().email()));
        colSLopSH.setCellValueFactory(row -> studentValue(row.getValue().lopSinhHoat()));
        colSMaDeTai.setCellValueFactory(row -> studentValue(TextFormat.orDash(row.getValue().maDeTai())));
        colSTenDeTai.setCellValueFactory(row -> studentValue(TextFormat.orDash(row.getValue().tenDeTai())));
        colSTenDeTai.setCellFactory(TableCells.wrapping());
        colSHinhThuc.setCellValueFactory(row -> studentValue(assignmentModeText(row.getValue().hinhThucPhanCong())));
    }

    private void setupRegisteredTable() {
        colStt.setCellFactory(TableCells.indexColumn());
        colMaSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaSoSinhVien()));
        colTenSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenSinhVien()));
        colLopSH.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLopSinhHoat()));
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colHinhThuc.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHinhThucPhanCongText()));
        colThoiGian.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getThoiGianDangKyText()));
    }

    private void setupUnregisteredTable() {
        colUStt.setCellFactory(TableCells.indexColumn());
        colUMaSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaSoSinhVien()));
        colUTenSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenSinhVien()));
        colULopSH.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLopSinhHoat()));
        colUEmail.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail()));
        tableUnregistered.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                cbUnregisteredStudent.setValue(newValue);
            }
        });
    }

    private void setupAssignmentCombos() {
        cbUnregisteredStudent.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(UnregisteredStudentRow student) {
                return student == null ? "" : student.toString();
            }
            @Override public UnregisteredStudentRow fromString(String value) { return null; }
        });
        cbAssignableTopic.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(AssignedTopicRow topic) {
                return topic == null ? "" : topic.getMaDeTaiHeThong() + " - " + topic.getTenDeTai()
                        + " (còn " + topic.getSoChoConLai() + " chỗ, "
                        + topic.getCheDoPhanCongText() + ")";
            }
            @Override public AssignedTopicRow fromString(String value) { return null; }
        });
    }

    private void loadSections(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = lecturerDashboardService.getCourseSections(maTaiKhoan);
            cbSection.setItems(FXCollections.observableArrayList(sections));
            cbSection.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(LecturerCourseSectionSummary s) {
                    return s == null ? "" : s.maLop() + " - " + s.tenLopHocPhan();
                }
                @Override public LecturerCourseSectionSummary fromString(String s) { return null; }
            });
            if (!sections.isEmpty()) {
                Integer selectedId = LecturerCourseSectionContext.getSelectedCourseSectionId();
                LecturerCourseSectionSummary selected = sections.stream()
                        .filter(section -> selectedId != null && section.maLopHocPhan() == selectedId)
                        .findFirst()
                        .orElse(sections.get(0));
                cbSection.setValue(selected);
                handleRefresh();
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi tải lớp học phần: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;
        LecturerCourseSectionContext.setSelectedCourseSectionId(section.maLopHocPhan());

        try {
            allStudents = lecturerDashboardService.getClassStudents(maTaiKhoan, section.maLopHocPhan());
            applyStudentsFilter();
            updateStudentsStatistics();

            List<RegistrationResultRow> registered =
                    registrationResultService.findRegisteredByLop(section.maLopHocPhan());
            tableRegistered.setItems(FXCollections.observableArrayList(registered));
            lblRegisteredCount.setText("Đã có đề tài: " + registered.size() + " sinh viên");

            List<UnregisteredStudentRow> unregistered =
                    registrationResultService.findUnregisteredByLop(section.maLopHocPhan());
            tableUnregistered.setItems(FXCollections.observableArrayList(unregistered));
            cbUnregisteredStudent.setItems(FXCollections.observableArrayList(unregistered));
            lblUnregisteredCount.setText("Chưa có đề tài: " + unregistered.size() + " sinh viên");
            if (!unregistered.isEmpty()) {
                cbUnregisteredStudent.getSelectionModel().selectFirst();
            }

            List<AssignedTopicRow> topics =
                    registrationResultService.findAssignableTopicsByLop(section.maLopHocPhan());
            cbAssignableTopic.setItems(FXCollections.observableArrayList(topics));
            if (!topics.isEmpty()) {
                cbAssignableTopic.getSelectionModel().selectFirst();
            }
            updateAssignHint(unregistered, topics);
        } catch (Exception e) {
            MainApp.showError("Lỗi tải kết quả đăng ký/phân công: " + e.getMessage());
        }
    }

    private void applyStudentsFilter() {
        String keyword = txtSearchStudents.getText() == null ? "" : txtSearchStudents.getText().trim().toLowerCase();
        List<LecturerClassStudentRow> filtered = allStudents.stream()
                .filter(student -> keyword.isEmpty()
                        || contains(student.maSoSinhVien(), keyword)
                        || contains(student.hoTen(), keyword)
                        || contains(student.email(), keyword)
                        || contains(student.maDeTai(), keyword)
                        || contains(student.tenDeTai(), keyword))
                .toList();
        tableStudents.getItems().setAll(filtered);
    }

    private void updateStudentsStatistics() {
        long withTopic = allStudents.stream().filter(LecturerClassStudentRow::hasTopic).count();
        lblTotalStudents.setText("Tổng sinh viên: " + allStudents.size());
        lblWithTopicStudents.setText("Đã có đề tài: " + withTopic);
        lblWithoutTopicStudents.setText("Chưa có đề tài: " + (allStudents.size() - withTopic));
    }

    private ReadOnlyStringWrapper studentValue(String value) {
        return new ReadOnlyStringWrapper(value == null ? "" : value);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String assignmentModeText(String mode) {
        if (mode == null || mode.isBlank()) return "Chưa có đề tài";
        return switch (mode) {
            case "TU_DANG_KY" -> "Tự đăng ký";
            case "THU_CONG" -> "GV phân công";
            case "TU_DONG" -> "Tự động";
            default -> mode;
        };
    }

    private void updateAssignHint(List<UnregisteredStudentRow> students, List<AssignedTopicRow> topics) {
        if (students.isEmpty()) {
            lblAssignHint.setText("Tất cả sinh viên trong lớp đã có đề tài.");
        } else if (topics.isEmpty()) {
            lblAssignHint.setText("Còn " + students.size()
                    + " sinh viên chưa có đề tài nhưng không còn đề tài trống.");
        } else {
            int totalSlots = topics.stream().mapToInt(AssignedTopicRow::getSoChoConLai).sum();
            lblAssignHint.setText("Có " + students.size() + " sinh viên chưa có đề tài và "
                    + totalSlots + " chỗ trống.");
        }
    }

    @FXML
    private void handleManualAssign() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        UnregisteredStudentRow student = cbUnregisteredStudent.getValue();
        AssignedTopicRow topic = cbAssignableTopic.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        if (student == null) { MainApp.showError("Vui lòng chọn sinh viên."); return; }
        if (topic == null) { MainApp.showError("Vui lòng chọn đề tài còn chỗ."); return; }
        if (!confirm("Phân công " + student.getMaSoSinhVien() + " vào đề tài "
                + topic.getMaDeTaiHeThong() + "?")) {
            return;
        }
        try {
            registrationResultService.assignStudentManually(
                    maGiangVien, student.getMaSinhVien(), topic.getMaDeTaiLop(),
                    "Giảng viên phân công thủ công");
            MainApp.showInfo("Phân công thủ công thành công.");
            handleRefresh();
        } catch (Exception e) {
            MainApp.showError(e.getMessage());
        }
    }

    @FXML
    private void handleAutoAssign() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        if (!confirm("Phân công tự động các sinh viên chưa có đề tài vào chỗ trống còn lại?")) {
            return;
        }
        try {
            int count = registrationResultService.autoAssignUnregisteredStudents(
                    maGiangVien, section.maLopHocPhan());
            MainApp.showInfo("Đã phân công tự động " + count + " sinh viên.");
            handleRefresh();
        } catch (Exception e) {
            MainApp.showError(e.getMessage());
        }
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
    @FXML private void handleShowChangePassword() { MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW); }
    @FXML private void handleLogout() { MainApp.showLogin(); }
}
