package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dao.RegistrationResultDAO;
import com.ptit.doancnpm.model.dao.TopicBankDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.model.dto.UnregisteredStudentRow;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class RegistrationResultController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private TabPane tabPane;

    // Tab 1 — Đã đăng ký
    @FXML private TableView<RegistrationResultRow> tableRegistered;
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
    @FXML private TableColumn<UnregisteredStudentRow, String> colUMaSV;
    @FXML private TableColumn<UnregisteredStudentRow, String> colUTenSV;
    @FXML private TableColumn<UnregisteredStudentRow, String> colULopSH;
    @FXML private TableColumn<UnregisteredStudentRow, String> colUEmail;
    @FXML private Label lblUnregisteredCount;

    private final LecturerDashboardDAO dashboardDAO = new LecturerDashboardDAO();
    private final RegistrationResultDAO resultDAO = new RegistrationResultDAO();
    private final TopicBankDAO topicBankDAO = new TopicBankDAO();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        setupRegisteredTable();
        setupUnregisteredTable();
        loadSections(user.getMaTaiKhoan());
        cbSection.setOnAction(e -> handleRefresh());
    }

    private void setupRegisteredTable() {
        colMaSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaSoSinhVien()));
        colTenSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenSinhVien()));
        colLopSH.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLopSinhHoat()));
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colHinhThuc.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHinhThucPhanCongText()));
        colThoiGian.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getThoiGianDangKyText()));
    }

    private void setupUnregisteredTable() {
        colUMaSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaSoSinhVien()));
        colUTenSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenSinhVien()));
        colULopSH.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLopSinhHoat()));
        colUEmail.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail()));
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
            MainApp.showError("Lỗi tải lớp học phần: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;

        try {
            List<RegistrationResultRow> registered = resultDAO.findRegisteredByLop(section.maLopHocPhan());
            tableRegistered.setItems(FXCollections.observableArrayList(registered));
            lblRegisteredCount.setText("Đã đăng ký: " + registered.size() + " sinh viên");

            List<UnregisteredStudentRow> unregistered = resultDAO.findUnregisteredByLop(section.maLopHocPhan());
            tableUnregistered.setItems(FXCollections.observableArrayList(unregistered));
            lblUnregisteredCount.setText("Chưa đăng ký: " + unregistered.size() + " sinh viên");
        } catch (Exception e) {
            MainApp.showError("Lỗi tải kết quả đăng ký: " + e.getMessage());
        }
    }

    @FXML private void handleBack() {
        MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW);
    }
}
