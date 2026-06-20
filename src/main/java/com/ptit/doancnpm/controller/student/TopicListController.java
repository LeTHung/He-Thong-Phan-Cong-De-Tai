package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Optional;

/**
 * Màn hình danh sách đề tài cho sinh viên: xem các đề tài còn chỗ trong lớp
 * học phần đang học, tìm kiếm, xem chi tiết và tự đăng ký.
 */
public class TopicListController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblStudentName;

    @FXML
    private Label lblStudentCode;

    @FXML
    private Label lblStudentClass;

    @FXML
    private Label lblCourseName;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<StudentTopicSummary> tblTopics;

    @FXML
    private TableColumn<StudentTopicSummary, String> colCode;

    @FXML
    private TableColumn<StudentTopicSummary, String> colName;

    @FXML
    private TableColumn<StudentTopicSummary, String> colSlots;

    @FXML
    private TableColumn<StudentTopicSummary, String> colRemaining;

    @FXML
    private TableColumn<StudentTopicSummary, String> colStatus;

    @FXML
    private TableColumn<StudentTopicSummary, String> colRegistered;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private int maSinhVien;
    private List<StudentTopicSummary> allTopics = List.of();

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
        loadStudentInfo();
        loadTopics();
    }

    private void setupTable() {
        colCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().maDeTaiHeThong()));
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().tenDeTai()));
        colSlots.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().soLuongHienTai() + "/" + data.getValue().soLuongToiDa()));
        colRemaining.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                String.valueOf(data.getValue().soChoConLai())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                trangThaiText(data.getValue().trangThai())));
        colRegistered.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().daDangKy() ? "Đã đăng ký" : ""));
    }

    private void loadStudentInfo() {
        try {
            StudentInfo info = topicRegistrationService.getStudentInfo(maTaiKhoan);
            maSinhVien = info.maSinhVien();
            lblStudentName.setText(info.hoTen());
            lblStudentCode.setText("MSSV: " + info.maSoSinhVien());
            lblStudentClass.setText("Lớp: " + nullToDash(info.lopSinhHoat()));
            lblCourseName.setText(info.maLopHocPhan() == null
                    ? "Chưa được xếp vào lớp học phần"
                    : "Lớp HP: " + nullToDash(info.tenLopHocPhan()));
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void loadTopics() {
        try {
            allTopics = topicRegistrationService.getRegistrableTopics(maTaiKhoan);
            applyFilter();
            if (allTopics.isEmpty()) {
                showMessage("Chưa có đề tài nào trong lớp học phần của bạn.");
            } else {
                showMessage("Có " + allTopics.size() + " đề tài. Chọn một đề tài rồi bấm Đăng ký hoặc Xem chi tiết.");
            }
        } catch (RuntimeException exception) {
            tblTopics.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            tblTopics.getItems().setAll(allTopics);
            return;
        }

        List<StudentTopicSummary> filtered = allTopics.stream()
                .filter(topic -> contains(topic.maDeTaiHeThong(), keyword)
                        || contains(topic.tenDeTai(), keyword))
                .toList();
        tblTopics.getItems().setAll(filtered);
    }

    @FXML
    private void handleSearch() {
        applyFilter();
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadStudentInfo();
        loadTopics();
    }

    @FXML
    private void handleViewDetail() {
        StudentTopicSummary selected = tblTopics.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Vui lòng chọn một đề tài để xem chi tiết.");
            return;
        }
        StudentTopicContext.setSelectedTopic(selected.maDeTaiLop());
        MainApp.setRoot(MainApp.STUDENT_TOPIC_DETAIL_VIEW);
    }

    @FXML
    private void handleRegister() {
        StudentTopicSummary selected = tblTopics.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Vui lòng chọn một đề tài để đăng ký.");
            return;
        }
        if (selected.daDangKy()) {
            showMessage("Bạn đã đăng ký đề tài này rồi.");
            return;
        }

        boolean confirmed = confirm("Xác nhận đăng ký đề tài \"" + selected.tenDeTai() + "\"?");
        if (!confirmed) {
            return;
        }

        try {
            topicRegistrationService.register(maSinhVien, selected.maDeTaiLop());
            showMessage("Đăng ký đề tài thành công.");
            loadTopics();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.STUDENT_DASHBOARD_VIEW);
    }

    @FXML
    private void handleShowMyRegistration() {
        MainApp.setRoot(MainApp.STUDENT_MY_REGISTRATION_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String trangThaiText(String trangThai) {
        if (trangThai == null) {
            return "";
        }
        return switch (trangThai) {
            case "DANG_MO" -> "Đang mở";
            case "DA_DU" -> "Đã đủ";
            case "DA_DONG" -> "Đã đóng";
            default -> trangThai;
        };
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
