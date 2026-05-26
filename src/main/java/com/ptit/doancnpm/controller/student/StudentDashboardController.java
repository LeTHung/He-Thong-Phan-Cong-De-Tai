package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.User;
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

        lblWelcome.setText("Xin chào Sinh viên");
        lblUserInfo.setText(user.getTenDangNhap() + " - " + user.getVaiTro().getDisplayName());
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }
}
