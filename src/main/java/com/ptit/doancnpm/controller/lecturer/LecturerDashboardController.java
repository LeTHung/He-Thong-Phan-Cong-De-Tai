package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LecturerDashboardController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblWelcome;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();

        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình giảng viên.");
            MainApp.showLogin();
            return;
        }

        lblWelcome.setText("Xin chào Giảng viên");
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleShowCourseSections() {
        MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW);
    }

    @FXML
    private void handleShowTopicBank() {
        MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW);
    }

    @FXML
    private void handleShowAssignTopicToClass() {
        MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW);
    }

    @FXML
    private void handleShowRegistrationPeriod() {
        MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW);
    }

    @FXML
    private void handleShowRegistrationResult() {
        MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW);
    }

    @FXML
    private void handleShowFinalReport() {
        MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW);
    }

    @FXML
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này thuộc module Giảng viên, Cường sẽ phát triển tiếp.");
    }
}
