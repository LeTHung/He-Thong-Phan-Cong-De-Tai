package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegistrationHistoryEntry;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.DateTimeFormatters;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import com.ptit.doancnpm.util.TextFormat;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Màn hình "Lịch sử đăng ký": sinh viên xem lại các lần đăng ký / hủy đề tài
 * của mình (đọc từ bảng lich_su_dang_ky). Chỉ xem, không sửa.
 */
public class RegistrationHistoryController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<RegistrationHistoryEntry> tblHistory;

    @FXML
    private TableColumn<RegistrationHistoryEntry, Void> colStt;

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

    @FXML
    private Label lblPageInfo;

    @FXML
    private Button btnPrevPage;

    @FXML
    private Button btnNextPage;

    private static final int PAGE_SIZE = 10;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private List<RegistrationHistoryEntry> allHistory = List.of();
    private int currentPage = 0;

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
        colStt.setCellFactory(TableCells.indexColumn());
        colTime.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatTime(data.getValue())));
        colAction.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().hanhDongText()));
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maDeTaiHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenDeTai()));
        colClass.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maLop()));
        colMode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().hinhThucText()));
        colReason.setCellValueFactory(data -> new ReadOnlyStringWrapper(TextFormat.orDash(data.getValue().lyDo())));

        // Hiển thị đầy đủ chữ (xuống dòng) thay vì cắt bớt "..." ở các cột dài.
        colName.setCellFactory(TableCells.wrapping());
        colMode.setCellFactory(TableCells.wrapping());
        colReason.setCellFactory(TableCells.wrapping());
    }

    private void loadHistory() {
        try {
            allHistory = topicRegistrationService.getRegistrationHistory(maTaiKhoan);
            currentPage = 0;
            renderPage();
            if (allHistory.isEmpty()) {
                showMessage("Chưa có lịch sử đăng ký nào.");
            } else {
                showMessage("Có " + allHistory.size() + " lượt thao tác đăng ký / hủy.");
            }
        } catch (RuntimeException exception) {
            allHistory = List.of();
            renderPage();
            showMessage(exception.getMessage());
        }
    }

    private int totalPages() {
        return Math.max(1, (int) Math.ceil(allHistory.size() / (double) PAGE_SIZE));
    }

    /**
     * Hiển thị trang hiện tại của lịch sử, cập nhật nhãn trang và trạng thái nút.
     */
    private void renderPage() {
        int totalPages = totalPages();
        currentPage = Math.max(0, Math.min(currentPage, totalPages - 1));

        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, allHistory.size());
        List<RegistrationHistoryEntry> pageItems = from >= to ? List.of() : allHistory.subList(from, to);
        tblHistory.getItems().setAll(pageItems);

        lblPageInfo.setText("Trang " + (currentPage + 1) + "/" + totalPages
                + " • " + allHistory.size() + " lượt");
        btnPrevPage.setDisable(currentPage <= 0);
        btnNextPage.setDisable(currentPage >= totalPages - 1);
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            renderPage();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages() - 1) {
            currentPage++;
            renderPage();
        }
    }

    @FXML
    private void handleRefresh() {
        loadHistory();
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
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private String formatTime(RegistrationHistoryEntry entry) {
        return entry.thoiDiemThucHien() == null ? "—" : DateTimeFormatters.DATE_TIME.format(entry.thoiDiemThucHien());
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
