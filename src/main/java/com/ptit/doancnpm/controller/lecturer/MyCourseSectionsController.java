package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;

import java.util.List;

public class MyCourseSectionsController {

    @FXML private TableView<LecturerCourseSectionSummary> tableView;
    @FXML private TableColumn<LecturerCourseSectionSummary, Void> colStt;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMaLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colTenLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMonHoc;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colHocKy;
    @FXML private TableColumn<LecturerCourseSectionSummary, Integer> colSoSV;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colTrangThaiDot;
    @FXML private Label lblTotalSections;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        colStt.setCellFactory(TableCells.indexColumn());
        colMaLop.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().maLop()));
        colTenLop.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().tenLopHocPhan()));
        colMonHoc.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().tenMonHoc()));
        colHocKy.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().tenHocKy() + " " + cd.getValue().namHoc()));
        colSoSV.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().tongSoSinhVien()).asObject());
        colTrangThaiDot.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(
                        formatTrangThaiDot(cd.getValue().trangThaiDotDangKy())));
        tableView.setPlaceholder(new Label("Bạn chưa được phân công lớp học phần nào."));
        setupOpenStudentsOnDoubleClick();

        loadData(user.getMaTaiKhoan());
    }

    private void setupOpenStudentsOnDoubleClick() {
        tableView.setRowFactory(tv -> {
            TableRow<LecturerCourseSectionSummary> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openStudents(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleViewStudents() {
        LecturerCourseSectionSummary selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MainApp.showError("Vui lòng chọn một lớp học phần để xem danh sách sinh viên.");
            return;
        }
        openStudents(selected);
    }

    private void openStudents(LecturerCourseSectionSummary section) {
        if (section == null) return;
        LecturerCourseSectionContext.setSelectedCourseSectionId(section.maLopHocPhan());
        MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW);
    }

    private void loadData(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = dashboardService.getCourseSections(maTaiKhoan);
            tableView.setItems(FXCollections.observableArrayList(sections));
            lblTotalSections.setText("Tổng: " + sections.size() + " lớp học phần");
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách lớp học phần: " + e.getMessage());
        }
    }

    private String formatTrangThaiDot(String trangThai) {
        if (trangThai == null) return "Chưa mở";
        return switch (trangThai) {
            case "DANG_MO" -> "Đang mở";
            case "CHO_MO"  -> "Chờ giờ mở";
            case "DA_DONG" -> "Đã đóng";
            default        -> "Chưa mở";
        };
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
    @FXML private void handleShowChangePassword() { MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW); }
    @FXML private void handleLogout() { MainApp.showLogin(); }
}
