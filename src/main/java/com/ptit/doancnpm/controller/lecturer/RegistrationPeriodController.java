package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AssignTopicService;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.service.RegistrationPeriodService;
import com.ptit.doancnpm.service.TopicBankService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    @FXML private Button btnClose;

    @FXML private TableView<AssignedTopicRow> tableTopicStatus;
    @FXML private TableColumn<AssignedTopicRow, Void> colStt;
    @FXML private TableColumn<AssignedTopicRow, String>  colTpMaDeTai;
    @FXML private TableColumn<AssignedTopicRow, String>  colTpTenDeTai;
    @FXML private TableColumn<AssignedTopicRow, Integer> colTpDaDangKy;
    @FXML private TableColumn<AssignedTopicRow, Integer> colTpToiDa;
    @FXML private TableColumn<AssignedTopicRow, String>  colTpTrangThai;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();
    private final RegistrationPeriodService periodService = new RegistrationPeriodService();
    private final TopicBankService topicBankService = new TopicBankService();
    private final AssignTopicService assignTopicService = new AssignTopicService();
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
            maGiangVien = topicBankService.findMaGiangVienByTaiKhoan(user.getMaTaiKhoan());
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        setupTopicTable();
        loadSections(user.getMaTaiKhoan());
        cbSection.setOnAction(e -> onSectionSelected());

        txtStartTime.setPromptText("HH:mm  (VD: 08:00)");
        txtEndTime.setPromptText("HH:mm  (VD: 23:59)");
    }

    private void setupTopicTable() {
        colStt.setCellFactory(TableCells.indexColumn());
        colTpMaDeTai.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTpTenDeTai.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colTpDaDangKy.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getSoLuongHienTai()).asObject());
        colTpToiDa.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getSoLuongToiDa()).asObject());
        colTpTrangThai.setCellValueFactory(cd -> {
            AssignedTopicRow r = cd.getValue();
            String text = r.getSoChoConLai() <= 0 ? "Hết chỗ" : "Còn " + r.getSoChoConLai() + " chỗ";
            return new SimpleStringProperty(text);
        });
        tableTopicStatus.setPlaceholder(new Label("Chọn lớp học phần để xem tình trạng đề tài."));
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
        refreshStatus(section.maLopHocPhan());
        loadTopicStatus(section.maLopHocPhan());
    }

    private void loadTopicStatus(int maLopHocPhan) {
        try {
            java.util.List<AssignedTopicRow> rows = assignTopicService.findByLop(maLopHocPhan);
            tableTopicStatus.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải tình trạng đề tài: " + e.getMessage());
        }
    }

    private void refreshStatus(int maLopHocPhan) {
        try {
            Optional<RegistrationPeriodInfo> opt = periodService.findCurrentByLop(maLopHocPhan);
            if (opt.isEmpty()) {
                lblStatus.setText("Chưa có đợt đăng ký");
                lblStatus.setTextFill(Color.GRAY);
                lblCurrentPeriodInfo.setText("—");
                btnOpen.setDisable(false);
                btnClose.setDisable(true);
            } else {
                RegistrationPeriodInfo p = opt.get();
                LocalDateTime now = LocalDateTime.now();
                if (p.dangMo()) {
                    lblStatus.setText("Đang mở");
                    lblStatus.setTextFill(Color.GREEN);
                } else if ("DA_DONG".equals(p.trangThai())) {
                    lblStatus.setText("Đã đóng — có thể mở lại");
                    lblStatus.setTextFill(Color.DARKORANGE);
                } else if (p.thoiGianBatDau() != null && now.isBefore(p.thoiGianBatDau())) {
                    lblStatus.setText("Chờ giờ mở");
                    lblStatus.setTextFill(Color.DARKGOLDENROD);
                } else if (p.thoiGianKetThuc() != null && now.isAfter(p.thoiGianKetThuc())) {
                    lblStatus.setText("Đã hết hạn");
                    lblStatus.setTextFill(Color.RED);
                } else {
                    lblStatus.setText("Chưa mở");
                    lblStatus.setTextFill(Color.DARKGOLDENROD);
                }
                String info = "";
                if (p.thoiGianBatDau() != null) info += "Từ: " + DISPLAY_FMT.format(p.thoiGianBatDau());
                if (p.thoiGianKetThuc() != null) info += "  →  Đến: " + DISPLAY_FMT.format(p.thoiGianKetThuc());
                lblCurrentPeriodInfo.setText(info.isBlank() ? "—" : info);
                btnOpen.setDisable(p.dangMo());
                btnClose.setDisable(!p.dangMo());
            }

            LecturerCourseSectionSummary selected = cbSection.getValue();
            if (selected != null && !"DANG_MO".equals(selected.trangThai())) {
                lblStatus.setText("Lớp học phần đã đóng");
                lblStatus.setTextFill(Color.RED);
                btnOpen.setDisable(true);
                btnClose.setDisable(true);
            } else if (selected != null && "GIANG_VIEN_PHAN_CONG".equals(selected.cheDoPhanCong())) {
                lblStatus.setText("Giảng viên phân công trực tiếp");
                lblStatus.setTextFill(Color.DARKGOLDENROD);
                lblCurrentPeriodInfo.setText("Không cần mở cổng đăng ký");
                btnOpen.setDisable(true);
                btnClose.setDisable(true);
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi tải thông tin đợt đăng ký: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpen() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Không thể mở cổng cho lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        if ("GIANG_VIEN_PHAN_CONG".equals(section.cheDoPhanCong())) {
            MainApp.showError("Lớp này đang ở chế độ Giảng viên phân công.\n"
                    + "Không cần mở cổng đăng ký; hãy phân công trực tiếp tại màn hình Kết quả & Phân công.");
            return;
        }

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
        if (!ketThuc.isAfter(LocalDateTime.now())) {
            MainApp.showError("Thời gian kết thúc phải lớn hơn thời điểm hiện tại.");
            return;
        }

        // Kiểm tra đợt đăng ký hiện tại và xác định đây là mở mới hay mở lại
        boolean isReopen;
        try {
            Optional<RegistrationPeriodInfo> existing =
                    periodService.findCurrentByLop(section.maLopHocPhan());
            if (existing.isPresent() && existing.get().dangMo()) {
                MainApp.showError("Lớp này đã có đợt đăng ký đang mở.\n"
                        + "Vui lòng đóng đợt hiện tại trước khi tạo đợt mới.");
                return;
            }
            // isReopen = true nếu đã từng có đợt (đã đóng hoặc hết hạn), false nếu lần đầu
            isReopen = existing.isPresent();
        } catch (Exception e) {
            MainApp.showError("Lỗi kiểm tra đợt đăng ký: " + e.getMessage());
            return;
        }

        String titleDialog    = isReopen ? "Xác nhận mở lại cổng đăng ký" : "Xác nhận mở cổng đăng ký";
        String actionLabel    = isReopen ? "Mở lại" : "Mở";
        String noteReopen     = isReopen
                ? "\n⚠ Các đăng ký cũ vẫn được giữ nguyên. Sinh viên có thể tiếp tục đăng ký hoặc hủy đề tài."
                : "";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(titleDialog);
        confirm.setHeaderText(null);
        confirm.setContentText(actionLabel + " cổng đăng ký cho lớp \"" + section.maLop() + "\"\n"
                + "Từ: " + DISPLAY_FMT.format(batDau) + "\nĐến: " + DISPLAY_FMT.format(ketThuc)
                + noteReopen + "\n\nXác nhận?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                periodService.openPeriod(section.maLopHocPhan(), maGiangVien, batDau, ketThuc, null);
                refreshStatus(section.maLopHocPhan());
                String successMsg = isReopen
                        ? "Cổng đăng ký đã được mở lại thành công."
                        : "Cổng đăng ký đã được mở thành công.";
                MainApp.showInfo(successMsg);
            } catch (Exception e) {
                MainApp.showError("Lỗi mở cổng đăng ký: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClosePeriod() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận đóng cổng đăng ký");
        confirm.setHeaderText(null);
        confirm.setContentText("Đóng cổng đăng ký sớm cho lớp \"" + section.maLop()
                + "\"?\nSinh viên sẽ không thể đăng ký thêm sau khi đóng.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                periodService.closePeriod(section.maLopHocPhan(), maGiangVien);
                refreshStatus(section.maLopHocPhan());
                MainApp.showInfo("Đã đóng cổng đăng ký cho lớp \"" + section.maLop() + "\".");
            } catch (Exception e) {
                MainApp.showError("Lỗi đóng cổng đăng ký: " + e.getMessage());
            }
        }
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
    @FXML private void handleShowChangePassword() { MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW); }
    @FXML private void handleLogout() { MainApp.showLogin(); }
}
