package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class MyCourseSectionsController {

    @FXML private TableView<LecturerCourseSectionSummary> tableView;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMaLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colTenLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMonHoc;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colHocKy;
    @FXML private TableColumn<LecturerCourseSectionSummary, Integer> colSoSV;
    @FXML private Label lblTotalSections;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

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
        tableView.setPlaceholder(new Label("Bạn chưa được phân công lớp học phần nào."));

        loadData(user.getMaTaiKhoan());
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

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
}
