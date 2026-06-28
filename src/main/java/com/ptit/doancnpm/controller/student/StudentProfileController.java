package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TextFormat;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Màn hình thông tin cá nhân của sinh viên: hiển thị hồ sơ lấy từ database
 * (họ tên, MSSV, email, lớp sinh hoạt, khóa học, ngành, lớp học phần đang học).
 */
public class StudentProfileController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblName;

    @FXML
    private Label lblCode;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblClass;

    @FXML
    private Label lblCourseYear;

    @FXML
    private Label lblMajor;

    @FXML
    private Label lblCourseSection;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

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
        loadProfile(user.getMaTaiKhoan());
    }

    private void loadProfile(int maTaiKhoan) {
        try {
            StudentInfo info = topicRegistrationService.getStudentInfo(maTaiKhoan);
            lblName.setText(TextFormat.orDash(info.hoTen()));
            lblCode.setText(TextFormat.orDash(info.maSoSinhVien()));
            lblEmail.setText(TextFormat.orDash(info.email()));
            lblClass.setText(TextFormat.orDash(info.lopSinhHoat()));
            lblCourseYear.setText(TextFormat.orDash(info.khoaHoc()));
            lblMajor.setText(TextFormat.orDash(info.nganh()));
            lblCourseSection.setText(info.maLopHocPhan() == null
                    ? "Chưa được xếp vào lớp học phần"
                    : TextFormat.orDash(info.tenLopHocPhan()));
        } catch (RuntimeException exception) {
            lblMessage.setText(exception.getMessage());
        }
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.STUDENT_DASHBOARD_VIEW);
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
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

}
