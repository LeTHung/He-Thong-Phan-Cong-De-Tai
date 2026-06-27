package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dao.RegistrationPeriodDAO;
import com.ptit.doancnpm.model.dao.TopicBankDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class RegistrationPeriodController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private Label lblStatus;
    @FXML private Label lblCurrentPeriodInfo;
    @FXML private DatePicker dpStart;
    @FXML private TextField txtStartTime;
    @FXML private DatePicker dpEnd;
    @FXML private TextField txtEndTime;
    @FXML private Button btnOpen;

    private final LecturerDashboardDAO dashboardDAO = new LecturerDashboardDAO();
    private final RegistrationPeriodDAO periodDAO = new RegistrationPeriodDAO();
    private final TopicBankDAO topicBankDAO = new TopicBankDAO();
    private int maGiangVien;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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

        loadSections(user.getMaTaiKhoan());
        cbSection.setOnAction(e -> onSectionSelected());

        txtStartTime.setPromptText("HH:mm  (VD: 08:00)");
        txtEndTime.setPromptText("HH:mm  (VD: 23:59)");
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

    private void onSectionSelected() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;
        refreshStatus(section.maLopHocPhan());
    }

    private void refreshStatus(int maLopHocPhan) {
        try {
            Optional<RegistrationPeriod> opt = periodDAO.findCurrentByLop(maLopHocPhan);
            if (opt.isEmpty()) {
                lblStatus.setText("Chưa có đợt đăng ký");
                lblStatus.setTextFill(Color.GRAY);
                lblCurrentPeriodInfo.setText("—");
                btnOpen.setDisable(false);
            } else {
                RegistrationPeriod p = opt.get();
                if (p.dangMo()) {
                    lblStatus.setText("Đang mở");
                    lblStatus.setTextFill(Color.GREEN);
                } else if ("DA_DONG".equals(p.trangThai())) {
                    lblStatus.setText("Đã đóng");
                    lblStatus.setTextFill(Color.RED);
                } else {
                    lblStatus.setText("Nhập / Chờ");
                    lblStatus.setTextFill(Color.DARKGOLDENROD);
                }
                String info = "";
                if (p.thoiGianBatDau() != null) info += "Từ: " + DISPLAY_FMT.format(p.thoiGianBatDau());
                if (p.thoiGianKetThuc() != null) info += "  →  Đến: " + DISPLAY_FMT.format(p.thoiGianKetThuc());
                lblCurrentPeriodInfo.setText(info.isBlank() ? "—" : info);
                btnOpen.setDisable("DA_DONG".equals(p.trangThai()));
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi tải thông tin đợt đăng ký: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpen() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }

        LocalDate dStart = dpStart.getValue();
        LocalDate dEnd = dpEnd.getValue();
        String tStart = txtStartTime.getText().trim();
        String tEnd = txtEndTime.getText().trim();

        if (dStart == null || tStart.isEmpty()) { MainApp.showError("Vui lòng nhập ngày và giờ bắt đầu."); return; }
        if (dEnd == null || tEnd.isEmpty()) { MainApp.showError("Vui lòng nhập ngày và giờ kết thúc."); return; }

        LocalTime timeStart, timeEnd;
        try {
            timeStart = LocalTime.parse(tStart, TIME_FMT);
        } catch (DateTimeParseException ex) {
            MainApp.showError("Giờ bắt đầu không hợp lệ. Dùng định dạng HH:mm (VD: 08:00).");
            return;
        }
        try {
            timeEnd = LocalTime.parse(tEnd, TIME_FMT);
        } catch (DateTimeParseException ex) {
            MainApp.showError("Giờ kết thúc không hợp lệ. Dùng định dạng HH:mm (VD: 23:59).");
            return;
        }

        LocalDateTime batDau = LocalDateTime.of(dStart, timeStart);
        LocalDateTime ketThuc = LocalDateTime.of(dEnd, timeEnd);

        if (!ketThuc.isAfter(batDau)) {
            MainApp.showError("Thời gian kết thúc phải sau thời gian bắt đầu.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận mở cổng đăng ký");
        confirm.setHeaderText(null);
        confirm.setContentText("Mở cổng đăng ký cho lớp \"" + section.maLop() + "\"\n"
                + "Từ: " + DISPLAY_FMT.format(batDau) + "\nĐến: " + DISPLAY_FMT.format(ketThuc) + "\n\nXác nhận?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                periodDAO.openPeriod(section.maLopHocPhan(), maGiangVien, batDau, ketThuc, null);
                refreshStatus(section.maLopHocPhan());
                MainApp.showInfo("Cổng đăng ký đã được mở thành công.");
            } catch (Exception e) {
                MainApp.showError("Lỗi mở cổng đăng ký: " + e.getMessage());
            }
        }
    }

    @FXML private void handleBack() {
        MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW);
    }
}
