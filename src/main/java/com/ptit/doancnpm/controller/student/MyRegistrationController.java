package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.RegistrationCountdown;
import com.ptit.doancnpm.util.SessionManager;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * Màn hình "Đề tài đã chọn": liệt kê các đề tài sinh viên đã đăng ký,
 * xem chi tiết và hủy đăng ký khi cổng đăng ký còn mở.
 */
public class MyRegistrationController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblPeriod;

    @FXML
    private TableView<RegisteredTopic> tblRegistrations;

    @FXML
    private TableColumn<RegisteredTopic, String> colCode;

    @FXML
    private TableColumn<RegisteredTopic, String> colName;

    @FXML
    private TableColumn<RegisteredTopic, String> colClass;

    @FXML
    private TableColumn<RegisteredTopic, String> colSubject;

    @FXML
    private TableColumn<RegisteredTopic, String> colLecturer;

    @FXML
    private TableColumn<RegisteredTopic, String> colMode;

    @FXML
    private TableColumn<RegisteredTopic, String> colTime;

    @FXML
    private Label lblDetailTitle;

    @FXML
    private Label lblDetailClass;

    @FXML
    private Label lblDetailLecturer;

    @FXML
    private Label lblDetailMode;

    @FXML
    private Label lblDetailTime;

    @FXML
    private Label lblDetailDescription;

    @FXML
    private Button btnChange;

    @FXML
    private Button btnCancel;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private int maSinhVien;
    private Integer maLopHocPhan;
    private boolean dangMoDangKy;
    private Timeline countdown;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.SINH_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình đăng ký đề tài.");
            MainApp.showLogin();
            return;
        }

        maTaiKhoan = user.getMaTaiKhoan();
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());

        try {
            StudentInfo info = topicRegistrationService.getStudentInfo(maTaiKhoan);
            maSinhVien = info.maSinhVien();
            maLopHocPhan = info.maLopHocPhan();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }

        setupTable();
        loadPeriod();
        loadRegistrations();
    }

    private void loadPeriod() {
        dangMoDangKy = false;
        stopCountdown();
        if (maLopHocPhan == null) {
            lblPeriod.setText("Chưa có đợt đăng ký");
            lblPeriod.getStyleClass().setAll("badge", "badge-info");
            return;
        }

        try {
            Optional<RegistrationPeriodInfo> period = topicRegistrationService.getRegistrationPeriod(maLopHocPhan);
            if (period.isEmpty()) {
                lblPeriod.setText("Chưa mở đợt đăng ký");
                lblPeriod.getStyleClass().setAll("badge", "badge-info");
                return;
            }

            RegistrationPeriodInfo current = period.get();
            dangMoDangKy = current.dangMo();
            lblPeriod.setText(current.moTaTrangThai());
            lblPeriod.getStyleClass().setAll("badge",
                    dangMoDangKy ? (current.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");

            countdown = RegistrationCountdown.start(lblPeriod, current, () -> {
                dangMoDangKy = false;
                btnCancel.setDisable(true);
                btnChange.setDisable(true);
                showMessage("Đợt đăng ký vừa hết hạn nên không thể hủy hoặc đổi đề tài.");
            });
        } catch (RuntimeException exception) {
            lblPeriod.setText("");
        }
    }

    private void stopCountdown() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maDeTaiHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenDeTai()));
        colClass.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maLop()));
        colSubject.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenMonHoc()));
        colLecturer.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenGiangVien()));
        colMode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().hinhThucPhanCongText()));
        colTime.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatTime(data.getValue())));

        tblRegistrations.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                fillDetail(newValue);
                updateActionButtons(newValue);
            }
        });
    }

    private void loadRegistrations() {
        try {
            List<RegisteredTopic> registrations = topicRegistrationService.getMyRegistrations(maTaiKhoan);
            tblRegistrations.getItems().setAll(registrations);

            if (registrations.isEmpty()) {
                clearDetail();
                btnCancel.setDisable(true);
                btnChange.setDisable(true);
                showMessage("Bạn chưa đăng ký đề tài nào. Vào \"Danh sách đề tài\" để đăng ký.");
            } else {
                tblRegistrations.getSelectionModel().selectFirst();
                updateActionButtons(tblRegistrations.getSelectionModel().getSelectedItem());
                if (dangMoDangKy) {
                    showMessage("Bạn đã đăng ký " + registrations.size() + " đề tài.");
                } else {
                    showMessage("Bạn đã đăng ký " + registrations.size()
                            + " đề tài. Cổng đăng ký đã đóng nên không thể hủy.");
                }
            }
        } catch (RuntimeException exception) {
            tblRegistrations.getItems().clear();
            clearDetail();
            btnCancel.setDisable(true);
            btnChange.setDisable(true);
            showMessage(exception.getMessage());
        }
    }

    private void updateActionButtons(RegisteredTopic topic) {
        boolean canStudentModify = topic != null
                && dangMoDangKy
                && "TU_DANG_KY".equals(topic.hinhThucPhanCong());
        btnCancel.setDisable(!canStudentModify);
        btnChange.setDisable(!canStudentModify);
    }

    private void fillDetail(RegisteredTopic topic) {
        lblDetailTitle.setText(topic.maDeTaiHeThong() + " — " + topic.tenDeTai());
        lblDetailClass.setText("Lớp học phần: " + nullToDash(topic.tenLopHocPhan())
                + " (" + nullToDash(topic.tenMonHoc()) + ")");
        lblDetailLecturer.setText("Giảng viên: " + nullToDash(topic.tenGiangVien()));
        lblDetailMode.setText("Hình thức: " + topic.hinhThucPhanCongText());
        lblDetailTime.setText("Thời điểm đăng ký: " + formatTime(topic));
        lblDetailDescription.setText(nullToDash(topic.moTa()));
    }

    private void clearDetail() {
        lblDetailTitle.setText("Chưa có đề tài");
        lblDetailClass.setText("Lớp học phần: —");
        lblDetailLecturer.setText("Giảng viên: —");
        lblDetailMode.setText("Hình thức: —");
        lblDetailTime.setText("Thời điểm đăng ký: —");
        lblDetailDescription.setText("—");
    }

    @FXML
    private void handleChange() {
        RegisteredTopic selected = tblRegistrations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Vui lòng chọn đề tài cần đổi.");
            return;
        }
        if (!"TU_DANG_KY".equals(selected.hinhThucPhanCong())) {
            showMessage("Đề tài này do giảng viên/hệ thống phân công nên sinh viên không được tự đổi.");
            return;
        }
        if (!dangMoDangKy) {
            showMessage("Cổng đăng ký đã đóng nên không thể đổi đề tài.");
            return;
        }

        List<StudentTopicSummary> options = topicRegistrationService.getRegistrableTopics(maTaiKhoan).stream()
                .filter(topic -> topic.maDeTaiLop() != selected.maDeTaiLop()
                        && "DANG_MO".equals(topic.trangThai())
                        && "SINH_VIEN_TU_DANG_KY".equals(topic.cheDoPhanCong())
                        && topic.soChoConLai() > 0
                        && !topic.daDangKy())
                .toList();
        if (options.isEmpty()) {
            showMessage("Không có đề tài nào khác còn chỗ để đổi.");
            return;
        }

        LinkedHashMap<String, StudentTopicSummary> labelToTopic = new LinkedHashMap<>();
        for (StudentTopicSummary topic : options) {
            labelToTopic.put(
                    topic.maDeTaiHeThong() + " - " + topic.tenDeTai() + " (còn " + topic.soChoConLai() + " chỗ)",
                    topic);
        }
        List<String> labels = new ArrayList<>(labelToTopic.keySet());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.get(0), labels);
        dialog.setTitle("Đổi đề tài");
        dialog.setHeaderText("Hủy đề tài hiện tại và đăng ký đề tài mới.");
        dialog.setContentText("Chọn đề tài mới:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        StudentTopicSummary chosen = labelToTopic.get(result.get());
        try {
            topicRegistrationService.changeTopic(
                    maSinhVien, selected.maLopHocPhan(), selected.maDeTaiLop(), chosen.maDeTaiLop());
            showMessage("Đã đổi sang đề tài " + chosen.maDeTaiHeThong() + " - " + chosen.tenDeTai() + ".");
            loadPeriod();
            loadRegistrations();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
            loadRegistrations();
        }
    }

    @FXML
    private void handleCancel() {
        RegisteredTopic selected = tblRegistrations.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Vui lòng chọn đề tài cần hủy đăng ký.");
            return;
        }
        if (!"TU_DANG_KY".equals(selected.hinhThucPhanCong())) {
            showMessage("Đề tài này do giảng viên/hệ thống phân công nên sinh viên không được tự hủy.");
            return;
        }
        if (!dangMoDangKy) {
            showMessage("Cổng đăng ký đã đóng nên không thể hủy đăng ký.");
            return;
        }

        boolean confirmed = confirm("Bạn chắc chắn muốn hủy đăng ký đề tài \"" + selected.tenDeTai() + "\"?");
        if (!confirmed) {
            return;
        }

        String lyDo = askReason();

        try {
            topicRegistrationService.cancel(maSinhVien, selected.maLopHocPhan(), lyDo);
            showMessage("Đã hủy đăng ký đề tài.");
            loadRegistrations();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadRegistrations();
    }

    @FXML
    private void handleShowTopicList() {
        MainApp.setRoot(MainApp.STUDENT_TOPIC_LIST_VIEW);
    }

    @FXML
    private void handleShowHistory() {
        MainApp.setRoot("/views/student/registration-history.fxml");
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot("/views/student/change-password.fxml");
    }

    @FXML
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.STUDENT_DASHBOARD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private String askReason() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Hủy đăng ký");
        dialog.setHeaderText(null);
        dialog.setContentText("Lý do hủy (có thể bỏ trống):");
        Optional<String> result = dialog.showAndWait();
        return result.map(String::trim).filter(value -> !value.isEmpty()).orElse(null);
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String formatTime(RegisteredTopic topic) {
        return topic.thoiDiemDangKy() == null ? "—" : DATE_TIME_FORMATTER.format(topic.thoiDiemDangKy());
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
