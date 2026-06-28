package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.FinalReportService;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.service.TopicBankService;
import com.ptit.doancnpm.util.CsvExporter;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FinalReportController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblWithTopic;
    @FXML private Label lblWithoutTopic;
    @FXML private Label lblTotalTopics;
    @FXML private Label lblStatusLabel;
    @FXML private Button btnFinalize;
    @FXML private Button btnExportCsv;

    @FXML private TableView<RegistrationResultRow> tableReport;
    @FXML private TableColumn<RegistrationResultRow, String> colMaSV;
    @FXML private TableColumn<RegistrationResultRow, String> colTenSV;
    @FXML private TableColumn<RegistrationResultRow, String> colLopSH;
    @FXML private TableColumn<RegistrationResultRow, String> colMaDeTai;
    @FXML private TableColumn<RegistrationResultRow, String> colTenDeTai;
    @FXML private TableColumn<RegistrationResultRow, String> colHinhThuc;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();
    private final FinalReportService finalReportService = new FinalReportService();
    private final TopicBankService topicBankService = new TopicBankService();
    private int maGiangVien;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        try {
            maGiangVien = topicBankService.findMaGiangVienByTaiKhoan(user.getMaTaiKhoan());
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        setupTable();
        loadSections(user.getMaTaiKhoan());
        cbSection.setOnAction(e -> onSectionSelected());
        tableReport.setPlaceholder(new Label("Chọn lớp học phần và chốt danh sách để xem báo cáo."));
    }

    private void setupTable() {
        colMaSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaSoSinhVien()));
        colTenSV.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenSinhVien()));
        colLopSH.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getLopSinhHoat()));
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colHinhThuc.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHinhThucPhanCongText()));
    }

    private void loadSections(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = dashboardService.getCourseSections(maTaiKhoan);
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

    private void onSectionSelected() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;

        try {
            int[] stats = finalReportService.getStats(section.maLopHocPhan());
            lblTotalStudents.setText(String.valueOf(stats[0]));
            lblWithTopic.setText(String.valueOf(stats[1]));
            lblWithoutTopic.setText(String.valueOf(stats[0] - stats[1]));
            lblTotalTopics.setText(String.valueOf(stats[2]));

            boolean finalized = finalReportService.isFinalized(section.maLopHocPhan());
            if (finalized) {
                lblStatusLabel.setText("Đã chốt danh sách");
                btnFinalize.setDisable(true);
                loadReport(section.maLopHocPhan());
            } else {
                lblStatusLabel.setText("Chưa chốt");
                btnFinalize.setDisable(false);
                tableReport.getItems().clear();
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi tải thông tin: " + e.getMessage());
        }
    }

    @FXML
    private void handleFinalize() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }

        try {
            if (!finalReportService.hasPeriodForLop(section.maLopHocPhan())) {
                MainApp.showError("Không thể chốt vì lớp chưa có đợt đăng ký.\n"
                        + "Vui lòng mở cổng đăng ký trước.");
                return;
            }
            if (!finalReportService.hasRegistrationStarted(section.maLopHocPhan())) {
                MainApp.showError("Không thể chốt vì đợt đăng ký chưa đến giờ bắt đầu.");
                return;
            }
            if (finalReportService.isRegistrationOpen(section.maLopHocPhan())) {
                MainApp.showError("Không thể chốt danh sách khi cổng đăng ký vẫn đang mở.\n"
                        + "Vui lòng đóng cổng hoặc đợi đến khi hết thời gian đăng ký.");
                return;
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi kiểm tra đợt đăng ký: " + e.getMessage());
            return;
        }

        // Kiểm tra sinh viên chưa có đề tài
        int[] stats;
        try {
            stats = finalReportService.getStats(section.maLopHocPhan());
        } catch (Exception e) {
            MainApp.showError("Lỗi tải thống kê: " + e.getMessage());
            return;
        }
        int chuaDangKy = stats[0] - stats[1];
        String warning = chuaDangKy > 0
                ? "\n\nCẢNH BÁO: Còn " + chuaDangKy + " sinh viên chưa có đề tài!"
                : "";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận chốt danh sách");
        confirm.setHeaderText("Sau khi chốt, sinh viên KHÔNG thể thay đổi đề tài.");
        confirm.setContentText("Bạn có chắc chắn muốn chốt danh sách cho lớp \""
                + section.maLop() + "\"?" + warning);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int soTuDong = finalReportService.finalizeRegistration(maGiangVien, section.maLopHocPhan());
                lblStatusLabel.setText("Đã chốt danh sách");
                btnFinalize.setDisable(true);
                onSectionSelected();
                String msg = "Đã chốt danh sách thành công cho lớp \"" + section.maLop() + "\".";
                if (soTuDong > 0) {
                    msg += "\nHệ thống tự động phân công thêm " + soTuDong + " sinh viên vào các chỗ trống.";
                }
                MainApp.showInfo(msg);
            } catch (Exception e) {
                MainApp.showError("Lỗi chốt danh sách: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExportCsv() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        List<RegistrationResultRow> rows = tableReport.getItems();
        if (rows.isEmpty()) {
            MainApp.showError("Bảng báo cáo trống. Vui lòng chốt danh sách trước khi xuất CSV.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo CSV");
        fileChooser.setInitialFileName("phan_cong_" + section.maLop() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
        File file = fileChooser.showSaveDialog(MainApp.getPrimaryStage());
        if (file == null) return;

        try {
            new CsvExporter().export(rows, file);
            MainApp.showInfo("Xuất CSV thành công:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            MainApp.showError("Lỗi xuất CSV: " + e.getMessage());
        }
    }

    private void loadReport(int maLopHocPhan) {
        try {
            List<RegistrationResultRow> rows = finalReportService.getFinalReport(maLopHocPhan);
            tableReport.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải báo cáo: " + e.getMessage());
        }
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
}
