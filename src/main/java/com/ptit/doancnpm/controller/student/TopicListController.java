package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
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
    private ComboBox<String> cboStatus;

    @FXML
    private ComboBox<String> cboLecturer;

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
    private TableColumn<StudentTopicSummary, String> colMode;

    @FXML
    private TableColumn<StudentTopicSummary, String> colRegistered;

    @FXML
    private Label lblPageInfo;

    @FXML
    private Button btnPrevPage;

    @FXML
    private Button btnNextPage;

    private static final int PAGE_SIZE = 8;

    private static final String SORT_DEFAULT = "Mặc định";
    private static final String SORT_MOST_AVAILABLE = "Còn nhiều chỗ nhất";
    private static final String SORT_LEAST_AVAILABLE = "Còn ít chỗ nhất";
    private static final String SORT_NAME = "Tên A → Z";
    private static final String SORT_CODE = "Mã đề tài A → Z";

    private static final String STATUS_ALL = "Tất cả trạng thái";

    private static final String LECTURER_ALL = "Tất cả giảng viên";

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private int maSinhVien;
    private Integer maLopHocPhan;
    private boolean dangMoDangKy;
    private List<StudentTopicSummary> allTopics = List.of();
    private List<StudentTopicSummary> filteredTopics = List.of();
    private int currentPage = 0;
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

        setupTable();
        setupSort();
        setupStatusFilter();
        setupLecturerFilter();
        loadStudentInfo();
        loadTopics();
    }

    private void setupSort() {
        cboSort.getItems().setAll(
                SORT_DEFAULT, SORT_MOST_AVAILABLE, SORT_LEAST_AVAILABLE, SORT_NAME, SORT_CODE);
        cboSort.setValue(SORT_DEFAULT);
    }

    private void setupStatusFilter() {
        cboStatus.getItems().setAll(
                STATUS_ALL,
                trangThaiText("DANG_MO"),
                trangThaiText("DA_DU"),
                trangThaiText("DA_DONG"));
        cboStatus.setValue(STATUS_ALL);
    }

    private void setupLecturerFilter() {
        cboLecturer.getItems().setAll(LECTURER_ALL);
        cboLecturer.setValue(LECTURER_ALL);
    }

    /**
     * Cập nhật danh sách giảng viên trong bộ lọc theo các đề tài đang có, giữ lại
     * lựa chọn hiện tại nếu giảng viên đó vẫn còn trong danh sách.
     */
    private void refreshLecturerOptions() {
        String current = cboLecturer.getValue();
        List<String> lecturers = allTopics.stream()
                .map(StudentTopicSummary::tenGiangVien)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> options = new java.util.ArrayList<>();
        options.add(LECTURER_ALL);
        options.addAll(lecturers);
        cboLecturer.getItems().setAll(options);
        cboLecturer.setValue(options.contains(current) ? current : LECTURER_ALL);
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
        colMode.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                cheDoText(data.getValue().cheDoPhanCong())));
        colRegistered.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().daDangKy() ? "Đã có đề tài" : ""));
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
        stopCountdown();
        if (maLopHocPhan == null) {
            lblPeriod.setText("Chưa có đợt đăng ký");
            lblPeriod.getStyleClass().setAll("badge", "badge-info");
            btnRegister.setDisable(true);
            return;
        }

        try {
            Optional<RegistrationPeriodInfo> period = topicRegistrationService.getRegistrationPeriod(maLopHocPhan);
            if (period.isEmpty()) {
                lblPeriod.setText("Chưa mở đợt đăng ký");
                lblPeriod.getStyleClass().setAll("badge", "badge-info");
                btnRegister.setDisable(true);
                return;
            }

            RegistrationPeriodInfo current = period.get();
            dangMoDangKy = current.dangMo();
            lblPeriod.setText(current.moTaTrangThai());
            lblPeriod.getStyleClass().setAll("badge",
                    dangMoDangKy ? (current.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");
            btnRegister.setDisable(!dangMoDangKy);

            countdown = RegistrationCountdown.start(lblPeriod, current, () -> {
                dangMoDangKy = false;
                btnRegister.setDisable(true);
                showMessage("Đợt đăng ký vừa hết hạn. Bạn không thể đăng ký thêm.");
            });
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void stopCountdown() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void loadTopics() {
        try {
            allTopics = topicRegistrationService.getRegistrableTopics(maTaiKhoan);
            refreshLecturerOptions();
            applyFilter();
            if (allTopics.isEmpty()) {
                showMessage("Chưa có đề tài nào trong lớp học phần của bạn.");
            } else {
                long selfRegistrationTopics = allTopics.stream()
                        .filter(topic -> "SINH_VIEN_TU_DANG_KY".equals(topic.cheDoPhanCong()))
                        .count();
                showMessage("Có " + allTopics.size() + " đề tài, trong đó "
                        + selfRegistrationTopics + " đề tài cho sinh viên tự đăng ký.");
            }
        } catch (RuntimeException exception) {
            tblTopics.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void applyFilter() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();
        boolean onlyAvailable = chkOnlyAvailable.isSelected();
        String status = cboStatus == null ? null : cboStatus.getValue();
        boolean allStatus = status == null || STATUS_ALL.equals(status);
        String lecturer = cboLecturer == null ? null : cboLecturer.getValue();
        boolean allLecturers = lecturer == null || LECTURER_ALL.equals(lecturer);

        filteredTopics = allTopics.stream()
                .filter(topic -> keyword.isEmpty()
                        || contains(topic.maDeTaiHeThong(), keyword)
                        || contains(topic.tenDeTai(), keyword))
                .filter(topic -> !onlyAvailable
                        || (topic.soChoConLai() > 0 && "DANG_MO".equals(topic.trangThai())))
                .filter(topic -> allStatus || status.equals(trangThaiText(topic.trangThai())))
                .filter(topic -> allLecturers || lecturer.equals(topic.tenGiangVien()))
                .sorted(currentComparator())
                .toList();
        currentPage = 0;
        renderPage();
    }

    /**
     * Tổng số trang theo {@link #PAGE_SIZE}, tối thiểu 1 trang kể cả khi rỗng.
     */
    private int totalPages() {
        return Math.max(1, (int) Math.ceil(filteredTopics.size() / (double) PAGE_SIZE));
    }

    /**
     * Hiển thị đúng trang hiện tại của danh sách đã lọc, cập nhật nhãn trang và
     * trạng thái bật/tắt của hai nút điều hướng.
     */
    private void renderPage() {
        int totalPages = totalPages();
        currentPage = Math.max(0, Math.min(currentPage, totalPages - 1));

        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, filteredTopics.size());
        List<StudentTopicSummary> pageItems = from >= to ? List.of() : filteredTopics.subList(from, to);
        tblTopics.getItems().setAll(pageItems);

        lblPageInfo.setText("Trang " + (currentPage + 1) + "/" + totalPages
                + " • " + filteredTopics.size() + " đề tài");
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
        cboStatus.setValue(STATUS_ALL);
        cboLecturer.setValue(LECTURER_ALL);
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
        if (!"SINH_VIEN_TU_DANG_KY".equals(selected.cheDoPhanCong())) {
            showMessage("Đề tài này do giảng viên phân công; sinh viên chỉ được xem thông tin.");
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
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
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

    private String cheDoText(String cheDo) {
        if (cheDo == null) {
            return "";
        }
        return switch (cheDo) {
            case "SINH_VIEN_TU_DANG_KY" -> "SV đăng ký";
            case "GIANG_VIEN_PHAN_CONG" -> "GV phân công";
            default -> cheDo;
        };
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
