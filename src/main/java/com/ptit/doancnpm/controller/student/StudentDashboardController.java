package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.dto.StudentDashboardData;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.StudentDashboardService;
import com.ptit.doancnpm.util.RegistrationCountdown;
import com.ptit.doancnpm.util.SessionManager;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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

    private final StudentDashboardService studentDashboardService = new StudentDashboardService();

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

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        loadDashboard(user.getMaTaiKhoan());
    }

    private void loadDashboard(int maTaiKhoan) {
        try {
            StudentDashboardData data = studentDashboardService.getDashboardData(maTaiKhoan);
            StudentInfo info = data.studentInfo();

            lblWelcome.setText("Chào " + nullToDash(info.hoTen()) + ", chào mừng đến cổng đăng ký đề tài");

            lblStudentName.setText(nullToDash(info.hoTen()));
            lblStudentCode.setText("MSSV: " + nullToDash(info.maSoSinhVien()));
            lblStudentClass.setText("Lớp: " + nullToDash(info.lopSinhHoat()));
            lblStudentCourse.setText(info.maLopHocPhan() == null
                    ? "Chưa được xếp vào lớp học phần"
                    : "Lớp HP: " + nullToDash(info.tenLopHocPhan()));

            showCourseBadge(info);
            showPeriodBadge(info, data.registrationPeriod());

            lblStatTotal.setText(String.valueOf(data.soDeTai()));
            lblStatAvailable.setText(String.valueOf(data.soDeTaiConCho()));
            lblStatRegistered.setText(String.valueOf(data.soDeTaiDaDangKy()));

            showRegisteredTopic(data.deTaiDaDangKyMoiNhat());

            if (info.maLopHocPhan() == null) {
                lblMessage.setText("Bạn chưa được xếp vào lớp học phần nào nên chưa thể đăng ký đề tài.");
            } else if (data.soDeTaiDaDangKy() > 0) {
                lblMessage.setText("Bạn đã đăng ký đề tài. Vào \"Đề tài đã chọn\" để xem hoặc thay đổi.");
            } else {
                lblMessage.setText("Có " + data.soDeTai() + " đề tài trong lớp, "
                        + data.soDeTaiConCho() + " đề tài còn chỗ. Vào \"Danh sách đề tài\" để đăng ký.");
            }
        } catch (RuntimeException exception) {
            lblMessage.setText(exception.getMessage());
        }
    }

    private void showCourseBadge(StudentInfo info) {
        if (info.maLop() == null || info.maLop().isBlank()) {
            lblCourseBadge.setText("Chưa có lớp học phần");
        } else {
            lblCourseBadge.setText(info.maLop());
        }
        lblCourseBadge.getStyleClass().setAll("badge", "badge-info");
    }

    private void showPeriodBadge(StudentInfo info, RegistrationPeriod period) {
        if (info.maLopHocPhan() == null) {
            lblPeriodBadge.setText("Chưa có đợt đăng ký");
            lblPeriodBadge.getStyleClass().setAll("badge", "badge-warning");
            return;
        }
        if (period == null) {
            lblPeriodBadge.setText("Chưa mở đợt đăng ký");
            lblPeriodBadge.getStyleClass().setAll("badge", "badge-info");
            return;
        }
        lblPeriodBadge.setText(period.moTaTrangThai());
        lblPeriodBadge.getStyleClass().setAll("badge",
                period.dangMo() ? (period.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");

        if (countdown != null) {
            countdown.stop();
        }
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
