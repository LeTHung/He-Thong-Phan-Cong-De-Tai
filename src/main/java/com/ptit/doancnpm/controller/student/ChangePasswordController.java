package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.service.ChangePasswordService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Màn hình người dùng tự đổi mật khẩu. Dùng được cho mọi vai trò đang đăng nhập;
 * điều hướng quay lại đúng dashboard theo vai trò qua MainApp (không sửa MainApp).
 */
public class ChangePasswordController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private PasswordField txtOld;

    @FXML
    private PasswordField txtNew;

    @FXML
    private PasswordField txtConfirm;

    private final ChangePasswordService changePasswordService = new ChangePasswordService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
    }

    @FXML
    private void handleSave() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        try {
            changePasswordService.changePassword(
                    user, txtOld.getText(), txtNew.getText(), txtConfirm.getText());
            clearFields();
            MainApp.showInfo("Đổi mật khẩu thành công. Lần đăng nhập sau hãy dùng mật khẩu mới.");
            showMessage("Đã đổi mật khẩu.");
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }
        MainApp.showDashboardByRole(user.getVaiTro());
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private void clearFields() {
        txtOld.clear();
        txtNew.clear();
        txtConfirm.clear();
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
