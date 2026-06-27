package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dao.AssignTopicDAO;
import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dao.TopicBankDAO;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.TopicBankItem;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class AssignTopicToClassController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private ComboBox<TopicBankItem> cbTopic;
    @FXML private TextField txtMaxStudents;
    @FXML private ComboBox<String> cbMode;

    @FXML private TableView<AssignedTopicRow> tableAssigned;
    @FXML private TableColumn<AssignedTopicRow, String> colMaDeTai;
    @FXML private TableColumn<AssignedTopicRow, String> colTenDeTai;
    @FXML private TableColumn<AssignedTopicRow, Integer> colToiDa;
    @FXML private TableColumn<AssignedTopicRow, Integer> colHienTai;
    @FXML private TableColumn<AssignedTopicRow, String> colCheDoText;
    @FXML private TableColumn<AssignedTopicRow, String> colTrangThai;
    @FXML private Label lblSectionInfo;

    private final LecturerDashboardDAO dashboardDAO = new LecturerDashboardDAO();
    private final TopicBankDAO topicBankDAO = new TopicBankDAO();
    private final AssignTopicDAO assignDAO = new AssignTopicDAO();

    private int maGiangVien;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        try {
            maGiangVien = topicBankDAO.findMaGiangVienByTaiKhoan(user.getMaTaiKhoan());
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        cbMode.setItems(FXCollections.observableArrayList(
                "SINH_VIEN_TU_DANG_KY", "GIANG_VIEN_PHAN_CONG"));
        cbMode.setValue("SINH_VIEN_TU_DANG_KY");

        setupTable();
        loadSections(user.getMaTaiKhoan());
        loadTopics();

        cbSection.setOnAction(e -> onSectionSelected());
        tableAssigned.setPlaceholder(new Label("Chọn lớp học phần để xem danh sách đề tài đã gán."));
    }

    private void setupTable() {
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colToiDa.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getSoLuongToiDa()).asObject());
        colHienTai.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getSoLuongHienTai()).asObject());
        colCheDoText.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCheDoPhanCongText()));
        colTrangThai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai()));
    }

    private void loadSections(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = dashboardDAO.findCourseSectionsByAccountId(maTaiKhoan);
            cbSection.setItems(FXCollections.observableArrayList(sections));
            cbSection.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(LecturerCourseSectionSummary s) {
                    return s == null ? "" : s.maLop() + " - " + s.tenLopHocPhan();
                }
                @Override public LecturerCourseSectionSummary fromString(String s) { return null; }
            });
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách lớp: " + e.getMessage());
        }
    }

    private void loadTopics() {
        try {
            List<TopicBankItem> topics = topicBankDAO.findByGiangVien(maGiangVien);
            cbTopic.setItems(FXCollections.observableArrayList(topics));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách đề tài: " + e.getMessage());
        }
    }

    private void onSectionSelected() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;
        lblSectionInfo.setText(section.tenMonHoc() + " · " + section.tenHocKy() + " " + section.namHoc()
                + " · " + section.tongSoSinhVien() + " sinh viên");
        loadAssigned(section.maLopHocPhan());
    }

    private void loadAssigned(int maLopHocPhan) {
        try {
            List<AssignedTopicRow> rows = assignDAO.findByLop(maLopHocPhan);
            tableAssigned.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách đề tài đã gán: " + e.getMessage());
        }
    }

    @FXML
    private void handleAssign() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        TopicBankItem topic = cbTopic.getValue();
        String maxStr = txtMaxStudents.getText().trim();
        String mode = cbMode.getValue();

        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Không thể gán đề tài vào lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        if (topic == null) { MainApp.showError("Vui lòng chọn đề tài."); return; }
        if (maxStr.isEmpty()) { MainApp.showError("Vui lòng nhập số sinh viên tối đa."); return; }
        if (mode == null) { MainApp.showError("Vui lòng chọn chế độ phân công."); return; }

        int maxStudents;
        try {
            maxStudents = Integer.parseInt(maxStr);
            if (maxStudents <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            MainApp.showError("Số sinh viên tối đa phải là số nguyên dương.");
            return;
        }

        try {
            if (assignDAO.isAlreadyAssigned(section.maLopHocPhan(), topic.getMaDeTai())) {
                MainApp.showError("Đề tài \"" + topic.getTenDeTai() + "\" đã được gán vào lớp này rồi.");
                return;
            }
            assignDAO.assignTopic(section.maLopHocPhan(), topic.getMaDeTai(), maxStudents, mode);
            loadAssigned(section.maLopHocPhan());
            MainApp.showInfo("Gán đề tài thành công.");
        } catch (Exception e) {
            MainApp.showError("Lỗi gán đề tài: " + e.getMessage());
        }
    }

    @FXML
    private void handleRemove() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Không thể gỡ đề tài khỏi lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        AssignedTopicRow selected = tableAssigned.getSelectionModel().getSelectedItem();
        if (selected == null) { MainApp.showInfo("Vui lòng chọn một đề tài để gỡ."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận gỡ đề tài");
        confirm.setHeaderText(null);
        confirm.setContentText("Gỡ đề tài \"" + selected.getTenDeTai() + "\" khỏi lớp?\n(Chỉ được gỡ nếu chưa có sinh viên đăng ký)");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                assignDAO.removeAssignment(selected.getMaDeTaiLop());
                loadAssigned(section.maLopHocPhan());
            } catch (Exception e) {
                MainApp.showError("Lỗi gỡ đề tài: " + e.getMessage());
            }
        }
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
}
