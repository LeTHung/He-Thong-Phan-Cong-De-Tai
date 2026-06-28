package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class LecturerDashboardController {

    @FXML private Label lblUserInfo;
    @FXML private Label lblWelcome;
    @FXML private Label lblStatSections;
    @FXML private Label lblStatSectionNote;
    @FXML private Label lblStatTopics;
    @FXML private Label lblStatRegistered;
    @FXML private Label lblStatGate;
    @FXML private TableView<LecturerCourseSectionSummary> tableSections;
    @FXML private TableColumn<LecturerCourseSectionSummary, Void> colStt;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colDashMaLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colDashTenLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, Integer> colDashSoDeTai;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colDashTrangThai;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình giảng viên.");
            MainApp.showLogin();
            return;
        }

        setupTable();
        loadDashboard(user.getMaTaiKhoan());
    }

    private void setupTable() {
        colStt.setCellFactory(TableCells.indexColumn());
        colDashMaLop.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().maLop()));
        colDashTenLop.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().tenLopHocPhan()));
        colDashSoDeTai.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().tongSoDeTai()).asObject());
        colDashTrangThai.setCellValueFactory(cd ->
                new SimpleStringProperty(formatTrangThai(cd.getValue().trangThai())));
        tableSections.setPlaceholder(new Label("Bạn chưa được phân công lớp học phần nào."));
    }

    private void loadDashboard(int maTaiKhoan) {
        try {
            String lecturerName = dashboardService.getLecturerName(maTaiKhoan);
            if (lecturerName == null || lecturerName.isBlank()) {
                lecturerName = SessionManager.getCurrentUser().getTenDangNhap();
            }
            lblWelcome.setText("Xin chào, " + lecturerName);
            lblUserInfo.setText(lecturerName + " • Giảng viên");

            List<LecturerCourseSectionSummary> sections = dashboardService.getCourseSections(maTaiKhoan);
            int totalTopics = sections.stream()
                    .mapToInt(LecturerCourseSectionSummary::tongSoDeTai)
                    .sum();

            lblStatSections.setText(String.valueOf(sections.size()));
            if (sections.isEmpty()) {
                lblStatSectionNote.setText("Chưa có lớp nào");
            } else if (sections.size() == 1) {
                lblStatSectionNote.setText(sections.get(0).maLop());
            } else {
                lblStatSectionNote.setText(sections.size() + " lớp học phần");
            }
            lblStatTopics.setText(String.valueOf(totalTopics));
            lblStatRegistered.setText(String.valueOf(dashboardService.getTotalRegisteredStudents(maTaiKhoan)));
            lblStatGate.setText(formatGateStatus(dashboardService.getRegistrationGateStatus(maTaiKhoan)));
            tableSections.setItems(FXCollections.observableArrayList(sections));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải dữ liệu dashboard: " + e.getMessage());
        }
    }

    private String formatTrangThai(String trangThai) {
        return switch (trangThai == null ? "" : trangThai) {
            case "DANG_MO" -> "Đang mở";
            case "DA_DONG" -> "Đã đóng";
            case "LUU_TRU" -> "Lưu trữ";
            default -> trangThai;
        };
    }

    private String formatGateStatus(String status) {
        return switch (status == null ? "" : status) {
            case "DANG_MO" -> "Đang mở";
            case "CHO_MO" -> "Chờ giờ mở";
            case "DA_DONG" -> "Đã đóng";
            default -> "Chưa mở";
        };
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleShowCourseSections() {
        MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW);
    }

    @FXML
    private void handleShowTopicBank() {
        MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW);
    }

    @FXML
    private void handleShowAssignTopicToClass() {
        MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW);
    }

    @FXML
    private void handleShowRegistrationPeriod() {
        MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW);
    }

    @FXML
    private void handleShowRegistrationResult() {
        MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW);
    }

    @FXML
    private void handleShowFinalReport() {
        MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW);
    }

}
