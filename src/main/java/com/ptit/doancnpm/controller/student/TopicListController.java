package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Comparator;
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
    private Label lblPeriod;

    @FXML
    private Button btnRegister;

    @FXML
    private TextField txtSearch;

    @FXML
    private CheckBox chkOnlyAvailable;

    @FXML
    private ComboBox<String> cboSort;

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

    private static final String SORT_DEFAULT = "Mặc định";
    private static final String SORT_MOST_AVAILABLE = "Còn nhiều chỗ nhất";
    private static final String SORT_LEAST_AVAILABLE = "Còn ít chỗ nhất";
    private static final String SORT_NAME = "Tên A → Z";
    private static final String SORT_CODE = "Mã đề tài A → Z";

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private int maSinhVien;
    private Integer maLopHocPhan;
    private boolean dangMoDangKy;
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
        setupSort();
        loadStudentInfo();
        loadTopics();
    }

    private void setupSort() {
        cboSort.getItems().setAll(
                SORT_DEFAULT, SORT_MOST_AVAILABLE, SORT_LEAST_AVAILABLE, SORT_NAME, SORT_CODE);
        cboSort.setValue(SORT_DEFAULT);
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
            maLopHocPhan = info.maLopHocPhan();
            lblStudentName.setText(info.hoTen());
            lblStudentCode.setText("MSSV: " + info.maSoSinhVien());
            lblStudentClass.setText("Lớp: " + nullToDash(info.lopSinhHoat()));
            lblCourseName.setText(maLopHocPhan == null
                    ? "Chưa được xếp vào lớp học phần"
                    : "Lớp HP: " + nullToDash(info.tenLopHocPhan()));
            loadPeriod();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void loadPeriod() {
        dangMoDangKy = false;
        if (maLopHocPhan == null) {
            lblPeriod.setText("Chưa có đợt đăng ký");
            lblPeriod.getStyleClass().setAll("badge", "badge-info");
            btnRegister.setDisable(true);
            return;
        }

        try {
            Optional<RegistrationPeriod> period = topicRegistrationService.getRegistrationPeriod(maLopHocPhan);
            if (period.isEmpty()) {
                lblPeriod.setText("Chưa mở đợt đăng ký");
                lblPeriod.getStyleClass().setAll("badge", "badge-info");
                btnRegister.setDisable(true);
                return;
            }

            RegistrationPeriod current = period.get();
            dangMoDangKy = current.dangMo();
            lblPeriod.setText(current.moTaTrangThai());
            lblPeriod.getStyleClass().setAll("badge",
                    dangMoDangKy ? (current.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");
            btnRegister.setDisable(!dangMoDangKy);
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
        boolean onlyAvailable = chkOnlyAvailable.isSelected();

        List<StudentTopicSummary> filtered = allTopics.stream()
                .filter(topic -> keyword.isEmpty()
                        || contains(topic.maDeTaiHeThong(), keyword)
                        || contains(topic.tenDeTai(), keyword))
                .filter(topic -> !onlyAvailable
                        || (topic.soChoConLai() > 0 && "DANG_MO".equals(topic.trangThai())))
                .sorted(currentComparator())
                .toList();
        tblTopics.getItems().setAll(filtered);
    }

    /**
     * Bộ so sánh tương ứng với lựa chọn sắp xếp hiện tại. "Mặc định" giữ nguyên
     * thứ tự trả về từ database (sort của Java ổn định nên trả về 0 là đủ).
     */
    private Comparator<StudentTopicSummary> currentComparator() {
        String option = cboSort == null ? null : cboSort.getValue();
        if (option == null) {
            return (a, b) -> 0;
        }
        Comparator<String> byTextAsc = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
        return switch (option) {
            case SORT_MOST_AVAILABLE -> Comparator
                    .comparingInt(StudentTopicSummary::soChoConLai).reversed()
                    .thenComparing(StudentTopicSummary::maDeTaiHeThong, byTextAsc);
            case SORT_LEAST_AVAILABLE -> Comparator
                    .comparingInt(StudentTopicSummary::soChoConLai)
                    .thenComparing(StudentTopicSummary::maDeTaiHeThong, byTextAsc);
            case SORT_NAME -> Comparator.comparing(StudentTopicSummary::tenDeTai, byTextAsc);
            case SORT_CODE -> Comparator.comparing(StudentTopicSummary::maDeTaiHeThong, byTextAsc);
            default -> (a, b) -> 0;
        };
    }

    @FXML
    private void handleSearch() {
        applyFilter();
    }

    @FXML
    private void handleSort() {
        applyFilter();
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        chkOnlyAvailable.setSelected(false);
        cboSort.setValue(SORT_DEFAULT);
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
        if (!dangMoDangKy) {
            showMessage("Cổng đăng ký hiện không mở nên không thể đăng ký.");
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
    private void handleShowHistory() {
        MainApp.setRoot("/views/student/registration-history.fxml");
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot("/views/student/change-password.fxml");
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
