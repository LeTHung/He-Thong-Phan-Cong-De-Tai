package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentDashboardController {

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

        if (user.getVaiTro() != UserRole.SINH_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình sinh viên.");
            MainApp.showLogin();
            return;
        }

        lblWelcome.setText("Chào mừng bạn đến với cổng đăng ký đề tài");
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này thuộc module Sinh viên, Quốc sẽ phát triển tiếp.");
    }
}
