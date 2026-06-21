package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegistrationHistoryEntry;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.CsvExporter;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Màn hình "Lịch sử đăng ký": sinh viên xem lại các lần đăng ký / hủy đề tài
 * của mình (đọc từ bảng lich_su_dang_ky). Chỉ xem, không sửa.
 */
public class RegistrationHistoryController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<RegistrationHistoryEntry> tblHistory;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colTime;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colAction;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colCode;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colName;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colClass;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colMode;

    @FXML
    private TableColumn<RegistrationHistoryEntry, String> colReason;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;

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

        setupTable();
        loadHistory();
    }

    private void setupTable() {
        colTime.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatTime(data.getValue())));
        colAction.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().hanhDongText()));
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maDeTaiHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenDeTai()));
        colClass.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maLop()));
        colMode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().hinhThucText()));
        colReason.setCellValueFactory(data -> new ReadOnlyStringWrapper(nullToDash(data.getValue().lyDo())));
    }

    private void loadHistory() {
        try {
            List<RegistrationHistoryEntry> history = topicRegistrationService.getRegistrationHistory(maTaiKhoan);
            tblHistory.getItems().setAll(history);
            if (history.isEmpty()) {
                showMessage("Chưa có lịch sử đăng ký nào.");
            } else {
                showMessage("Có " + history.size() + " lượt thao tác đăng ký / hủy.");
            }
        } catch (RuntimeException exception) {
            tblHistory.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadHistory();
    }

    @FXML
    private void handleExportCsv() {
        List<RegistrationHistoryEntry> data = tblHistory.getItems();
        if (data == null || data.isEmpty()) {
            showMessage("Không có lịch sử để xuất.");
            return;
        }

        File file = CsvExporter.chooseSaveFile(MainApp.getPrimaryStage(), "lich-su-dang-ky.csv");
        if (file == null) {
            return;
        }

        List<String> headers = List.of(
                "Thời điểm", "Hành động", "Mã ĐT", "Tên đề tài", "Lớp HP", "Hình thức", "Lý do");
        List<List<String>> rows = data.stream()
                .map(entry -> List.of(
                        formatTime(entry),
                        entry.hanhDongText(),
                        ns(entry.maDeTaiHeThong()),
                        ns(entry.tenDeTai()),
                        ns(entry.maLop()),
                        entry.hinhThucText(),
                        ns(entry.lyDo())))
                .toList();

        try {
            CsvExporter.write(file, headers, rows);
            showMessage("Đã xuất " + rows.size() + " dòng ra " + file.getName());
        } catch (IOException exception) {
            showMessage("Lỗi xuất CSV: " + exception.getMessage());
        }
    }

    @FXML
    private void handleShowTopicList() {
        MainApp.setRoot(MainApp.STUDENT_TOPIC_LIST_VIEW);
    }

    @FXML
    private void handleShowMyRegistration() {
        MainApp.setRoot(MainApp.STUDENT_MY_REGISTRATION_VIEW);
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.STUDENT_DASHBOARD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private String formatTime(RegistrationHistoryEntry entry) {
        return entry.thoiDiemThucHien() == null ? "—" : DATE_TIME_FORMATTER.format(entry.thoiDiemThucHien());
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private String ns(String value) {
        return value == null ? "" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
