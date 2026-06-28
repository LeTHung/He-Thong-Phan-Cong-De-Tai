package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.dto.StudentCourseSection;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.dto.TopicNotification;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.NotificationService;
import com.ptit.doancnpm.service.StudentDashboardService;
import com.ptit.doancnpm.util.RegistrationCountdown;
import com.ptit.doancnpm.util.SessionManager;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class StudentDashboardController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblCourseBadge;

    @FXML
    private Label lblPeriodBadge;

    @FXML
    private ComboBox<StudentCourseSection> cboClass;

    @FXML
    private Label lblStatTotal;

    @FXML
    private Label lblStatAvailable;

    @FXML
    private Label lblStatRegistered;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblStudentName;

    @FXML
    private Label lblStudentCode;

    @FXML
    private Label lblStudentClass;

    @FXML
    private Label lblStudentCourse;

    @FXML
    private Label lblRegisteredTopic;

    @FXML
    private Label lblRegisteredStatus;

    @FXML
    private Button btnNotifBell;

    @FXML
    private Label lblNotifCount;

    private static final DateTimeFormatter NOTIF_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final StudentDashboardService studentDashboardService = new StudentDashboardService();
    private final NotificationService notificationService = new NotificationService();

    private int maTaiKhoan;
    private List<StudentTopicSummary> allTopics = List.of();
    private List<RegisteredTopic> allRegistrations = List.of();
    private LocalDateTime notifLastSeen;

    // Popup thông báo gắn vào nút chuông (xây dựng trong code, không qua FXML).
    private final ListView<TopicNotification> notifList = new ListView<>();
    private ContextMenu notifMenu;
    private Button notifMarkBtn;

    private Timeline countdown;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.SINH_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình sinh viên.");
            MainApp.showLogin();
            return;
        }

        maTaiKhoan = user.getMaTaiKhoan();
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupClassSelector();
        setupNotifications();
        loadDashboard(maTaiKhoan);
        loadNotifications();
    }

    private void setupClassSelector() {
        cboClass.setConverter(new StringConverter<>() {
            @Override
            public String toString(StudentCourseSection section) {
                return section == null ? "" : section.hienThi();
            }

            @Override
            public StudentCourseSection fromString(String text) {
                return null;
            }
        });
    }

    private void setupNotifications() {
        notifList.setPrefSize(400, 300);
        notifList.setMaxHeight(360);
        notifList.setPlaceholder(new Label("Chưa có đề tài mới nào."));
        notifList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TopicNotification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                boolean unread = notificationService.isUnread(item, notifLastSeen);
                Label title = new Label((unread ? "● " : "")
                        + nullToDash(item.maDeTaiHeThong()) + " — " + nullToDash(item.tenDeTai()));
                title.getStyleClass().add(unread ? "notif-title-unread" : "notif-title");
                title.setWrapText(true);

                Label meta = new Label("Lớp " + nullToDash(item.maLop())
                        + " • GV " + nullToDash(item.tenGiangVien())
                        + " • " + formatNotifTime(item.thoiDiemTao()));
                meta.getStyleClass().add("body-muted");
                meta.setWrapText(true);

                VBox box = new VBox(2, title, meta);
                setText(null);
                setGraphic(box);
            }
        });

        // Nhấn đúp vào một thông báo để mở chi tiết đề tài.
        notifList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TopicNotification selected = notifList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    notifMenu.hide();
                    StudentTopicContext.setSelectedTopic(selected.maDeTaiLop());
                    MainApp.setRoot(MainApp.STUDENT_TOPIC_DETAIL_VIEW);
                }
            }
        });

        Label title = new Label("Thông báo đề tài mới");
        title.getStyleClass().add("card-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        notifMarkBtn = new Button("Đánh dấu đã đọc");
        notifMarkBtn.getStyleClass().add("btn-outline");
        notifMarkBtn.setOnAction(event -> handleMarkNotificationsRead());

        HBox header = new HBox(10, title, spacer, notifMarkBtn);
        header.setAlignment(Pos.CENTER_LEFT);

        Label hint = new Label("Giảng viên thêm đề tài mới vào lớp của bạn sẽ hiện ở đây. Nhấn đúp để xem chi tiết.");
        hint.getStyleClass().add("body-muted");
        hint.setWrapText(true);

        VBox panel = new VBox(10, header, hint, notifList);
        panel.getStyleClass().add("notif-popup");
        panel.setPrefWidth(420);

        CustomMenuItem item = new CustomMenuItem(panel);
        item.setHideOnClick(false);
        notifMenu = new ContextMenu(item);
        notifMenu.getStyleClass().add("notif-menu");
    }

    @FXML
    private void handleToggleNotifications() {
        if (notifMenu.isShowing()) {
            notifMenu.hide();
        } else {
            notifMenu.show(btnNotifBell, Side.BOTTOM, 0, 6);
        }
    }

    private void loadNotifications() {
        try {
            List<TopicNotification> topics = notificationService.getRecentTopics(maTaiKhoan);
            notifLastSeen = notificationService.getLastSeen(maTaiKhoan);
            notifList.getItems().setAll(topics);
            updateNotifBell(notificationService.countUnread(topics, notifLastSeen));
        } catch (RuntimeException exception) {
            notifList.getItems().clear();
            updateNotifBell(0);
        }
    }

    /** Cập nhật con số nhỏ trên nút chuông theo số thông báo chưa đọc. */
    private void updateNotifBell(long unread) {
        if (unread > 0) {
            lblNotifCount.setText(unread > 9 ? "9+" : String.valueOf(unread));
            lblNotifCount.setVisible(true);
            lblNotifCount.setManaged(true);
            notifMarkBtn.setDisable(false);
        } else {
            lblNotifCount.setText("");
            lblNotifCount.setVisible(false);
            lblNotifCount.setManaged(false);
            notifMarkBtn.setDisable(true);
        }
    }

    private void handleMarkNotificationsRead() {
        notificationService.markAllRead(maTaiKhoan);
        notifLastSeen = LocalDateTime.now();
        notifList.refresh();
        updateNotifBell(0);
    }

    private String formatNotifTime(LocalDateTime time) {
        return time == null ? "—" : NOTIF_TIME.format(time);
    }

    private void loadDashboard(int maTaiKhoan) {
        try {
            StudentDashboardData data = studentDashboardService.getDashboardData(maTaiKhoan);
            StudentInfo info = data.studentInfo();
            allTopics = data.topics();
            allRegistrations = data.registeredTopics();

            lblWelcome.setText("Chào " + nullToDash(info.hoTen()) + "!");
            lblStudentName.setText(nullToDash(info.hoTen()));
            lblStudentCode.setText("MSSV: " + nullToDash(info.maSoSinhVien()));
            lblStudentClass.setText("Lớp: " + nullToDash(info.lopSinhHoat()));

            List<StudentCourseSection> sections = data.courseSections();
            cboClass.getItems().setAll(sections);

            if (sections.isEmpty()) {
                cboClass.setDisable(true);
                renderForClass(null);
            } else {
                cboClass.setDisable(false);
                // Chọn lớp đầu tiên (mới nhất) -> kích hoạt handleSelectClass -> renderForClass.
                cboClass.getSelectionModel().selectFirst();
            }
        } catch (RuntimeException exception) {
            lblMessage.setText(exception.getMessage());
        }
    }

    @FXML
    private void handleSelectClass() {
        renderForClass(cboClass.getValue());
    }

    /**
     * Hiển thị toàn bộ phần tổng quan theo lớp học phần đang chọn: thống kê đề tài,
     * đợt đăng ký, đề tài đã đăng ký và các nhãn liên quan. {@code section == null}
     * nghĩa là sinh viên chưa được xếp vào lớp học phần nào.
     */
    private void renderForClass(StudentCourseSection section) {
        stopCountdown();
        cboClass.setTooltip(section == null ? null : new Tooltip(section.moTaDayDu()));

        if (section == null) {
            lblStudentCourse.setText("Chưa được xếp vào lớp học phần");
            lblCourseBadge.setText("Chưa có lớp học phần");
            lblCourseBadge.getStyleClass().setAll("badge", "badge-info");
            lblPeriodBadge.setText("Chưa có đợt đăng ký");
            lblPeriodBadge.getStyleClass().setAll("badge", "badge-warning");
            lblStatTotal.setText("0");
            lblStatAvailable.setText("0");
            lblStatRegistered.setText("0");
            showRegisteredTopic(null);
            lblMessage.setText("Bạn chưa được xếp vào lớp học phần nào nên chưa thể đăng ký đề tài.");
            return;
        }

        lblStudentCourse.setText("Lớp HP: " + nullToDash(section.tenLopHocPhan()));
        showCourseBadge(section);

        List<StudentTopicSummary> topics = topicsOf(section);
        List<RegisteredTopic> registrations = registrationsOf(section);

        long conCho = topics.stream()
                .filter(topic -> topic.soChoConLai() > 0 && "DANG_MO".equals(topic.trangThai()))
                .count();

        lblStatTotal.setText(String.valueOf(topics.size()));
        lblStatAvailable.setText(String.valueOf(conCho));
        lblStatRegistered.setText(String.valueOf(registrations.size()));

        showRegisteredTopic(registrations.isEmpty() ? null : registrations.get(0));
        showPeriodBadge(section);

        if (!registrations.isEmpty()) {
            lblMessage.setText("Bạn đã đăng ký đề tài trong lớp này. Vào \"Đề tài đã chọn\" để xem hoặc thay đổi.");
        } else {
            lblMessage.setText("Có " + topics.size() + " đề tài trong lớp, "
                    + conCho + " đề tài còn chỗ. Vào \"Danh sách đề tài\" để đăng ký.");
        }
    }

    private List<StudentTopicSummary> topicsOf(StudentCourseSection section) {
        return allTopics.stream()
                .filter(topic -> Objects.equals(topic.maLop(), section.maLop()))
                .toList();
    }

    private List<RegisteredTopic> registrationsOf(StudentCourseSection section) {
        return allRegistrations.stream()
                .filter(reg -> reg.maLopHocPhan() == section.maLopHocPhan())
                .toList();
    }

    private void showCourseBadge(StudentCourseSection section) {
        if (section.maLop() == null || section.maLop().isBlank()) {
            lblCourseBadge.setText("Chưa có mã lớp");
        } else {
            lblCourseBadge.setText(section.maLop());
        }
        lblCourseBadge.getStyleClass().setAll("badge", "badge-info");
    }

    private void showPeriodBadge(StudentCourseSection section) {
        RegistrationPeriodInfo period = studentDashboardService
                .getRegistrationPeriod(section.maLopHocPhan())
                .orElse(null);

        if (period == null) {
            lblPeriodBadge.setText("Chưa mở đợt đăng ký");
            lblPeriodBadge.getStyleClass().setAll("badge", "badge-info");
            return;
        }

        lblPeriodBadge.setText(period.moTaTrangThai());
        lblPeriodBadge.getStyleClass().setAll("badge",
                period.dangMo() ? (period.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");

        countdown = RegistrationCountdown.start(lblPeriodBadge, period,
                () -> lblMessage.setText("Đợt đăng ký đã hết hạn."));
    }

    private void showRegisteredTopic(RegisteredTopic topic) {
        if (topic == null) {
            lblRegisteredTopic.setText("Chưa đăng ký đề tài nào");
            lblRegisteredStatus.setText("Chưa đăng ký");
            lblRegisteredStatus.getStyleClass().setAll("badge", "badge-info");
            return;
        }
        lblRegisteredTopic.setText(nullToDash(topic.maDeTaiHeThong()) + " — " + nullToDash(topic.tenDeTai()));
        lblRegisteredStatus.setText("Đã đăng ký");
        lblRegisteredStatus.getStyleClass().setAll("badge", "badge-success");
    }

    private void stopCountdown() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
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
    private void handleShowHistory() {
        MainApp.setRoot("/views/student/registration-history.fxml");
    }

    @FXML
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot("/views/student/change-password.fxml");
    }
}
