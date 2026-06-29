package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.model.dto.StudentCourseSection;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.dto.StudentNotification;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.NotificationService;
import com.ptit.doancnpm.service.StudentDashboardService;
import com.ptit.doancnpm.util.DateTimeFormatters;
import com.ptit.doancnpm.util.RegistrationCountdown;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TextFormat;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.Duration;
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
    private Label lblNotifCount;

    @FXML
    private ListView<StudentNotification> notifList;

    @FXML
    private Button notifMarkBtn;

    private final StudentDashboardService studentDashboardService = new StudentDashboardService();
    private final NotificationService notificationService = new NotificationService();

    private int maTaiKhoan;
    private List<StudentTopicSummary> allTopics = List.of();
    private List<RegisteredTopic> allRegistrations = List.of();
    private LocalDateTime notifLastSeen;

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
        notifList.setPlaceholder(new Label("Bạn chưa có thông báo mới."));
        notifList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(StudentNotification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                boolean unread = notificationService.isUnread(item, notifLastSeen);
                Label title = new Label((unread ? "● " : "") + notificationTitle(item));
                title.getStyleClass().add(unread ? "notif-title-unread" : "notif-title");
                title.setWrapText(true);
                title.setMaxWidth(260);

                Label meta = new Label(notificationMessage(item));
                meta.getStyleClass().add("body-muted");
                meta.setWrapText(true);
                meta.setMaxWidth(260);

                VBox box = new VBox(2, title, meta);
                box.setMaxWidth(270);
                setPrefWidth(0);
                setText(null);
                setGraphic(box);
            }
        });

        // Nhấn đúp vào thông báo để mở danh sách đề tài của sinh viên.
        notifList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                StudentNotification selected = notifList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    MainApp.setRoot(MainApp.STUDENT_TOPIC_LIST_VIEW);
                }
            }
        });
    }

    private void loadNotifications() {
        try {
            List<StudentNotification> notifications = notificationService.getNotifications(maTaiKhoan);
            notifLastSeen = notificationService.getLastSeen(maTaiKhoan);
            notifList.getItems().setAll(notifications);
            updateNotificationCard(notificationService.countUnread(notifications, notifLastSeen));
        } catch (RuntimeException exception) {
            notifList.getItems().clear();
            updateNotificationCard(0);
        }
    }

    /** Cập nhật trạng thái chưa đọc trên card thông báo bên phải. */
    private void updateNotificationCard(long unread) {
        lblNotifCount.getStyleClass().removeAll(
                "notification-count-chip-unread", "notification-count-chip-read");
        if (unread > 0) {
            lblNotifCount.setText((unread > 9 ? "9+" : String.valueOf(unread)) + " mới");
            lblNotifCount.getStyleClass().add("notification-count-chip-unread");
            notifMarkBtn.setDisable(false);
        } else {
            lblNotifCount.setText("Đã đọc");
            lblNotifCount.getStyleClass().add("notification-count-chip-read");
            notifMarkBtn.setDisable(true);
        }
    }

    @FXML
    private void handleMarkNotificationsRead() {
        notificationService.markAllRead(maTaiKhoan);
        notifLastSeen = LocalDateTime.now();
        notifList.refresh();
        updateNotificationCard(0);
    }

    private String formatNotifTime(LocalDateTime time) {
        return time == null ? "—" : DateTimeFormatters.DATE_TIME.format(time);
    }

    private String notificationTitle(StudentNotification notification) {
        if (notification.type() == StudentNotification.Type.ADDED_TO_CLASS) {
            return "Bạn đã được thêm vào lớp " + TextFormat.orDash(notification.maLop());
        }
        return remainingTimeText(notification.registrationDeadline()) + " để đăng ký đề tài";
    }

    private String notificationMessage(StudentNotification notification) {
        if (notification.type() == StudentNotification.Type.ADDED_TO_CLASS) {
            return "GV " + TextFormat.orDash(notification.tenGiangVien())
                    + " • " + formatNotifTime(notification.eventTime());
        }
        return "Lớp " + TextFormat.orDash(notification.maLop())
                + " • đóng lúc " + formatNotifTime(notification.registrationDeadline())
                + " • nhấn đúp để xem đề tài";
    }

    private String remainingTimeText(LocalDateTime deadline) {
        if (deadline == null || !deadline.isAfter(LocalDateTime.now())) {
            return "Đã hết hạn";
        }
        long hours = Math.max(1, (Duration.between(LocalDateTime.now(), deadline).toMinutes() + 59) / 60);
        if (hours >= 24) {
            long days = (hours + 23) / 24;
            return "Còn " + days + " ngày";
        }
        return "Còn " + hours + " giờ";
    }

    private void loadDashboard(int maTaiKhoan) {
        try {
            StudentDashboardData data = studentDashboardService.getDashboardData(maTaiKhoan);
            StudentInfo info = data.studentInfo();
            allTopics = data.topics();
            allRegistrations = data.registeredTopics();

            lblWelcome.setText("Chào " + TextFormat.orDash(info.hoTen()) + "!");
            lblStudentName.setText(TextFormat.orDash(info.hoTen()));
            lblStudentCode.setText("MSSV: " + TextFormat.orDash(info.maSoSinhVien()));
            lblStudentClass.setText("Lớp: " + TextFormat.orDash(info.lopSinhHoat()));

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

        lblStudentCourse.setText("Lớp HP: " + TextFormat.orDash(section.tenLopHocPhan()));
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
        lblRegisteredTopic.setText(TextFormat.orDash(topic.maDeTaiHeThong()) + " — " + TextFormat.orDash(topic.tenDeTai()));
        lblRegisteredStatus.setText("Đã đăng ký");
        lblRegisteredStatus.getStyleClass().setAll("badge", "badge-success");
    }

    private void stopCountdown() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
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
        MainApp.setRoot(MainApp.STUDENT_REGISTRATION_HISTORY_VIEW);
    }

    @FXML
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }
}
