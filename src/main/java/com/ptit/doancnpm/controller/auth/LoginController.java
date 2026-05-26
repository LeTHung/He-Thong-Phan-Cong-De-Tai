package com.ptit.doancnpm.controller.auth;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.service.AuthService;
import com.ptit.doancnpm.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnLogin;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        clearMessage();
        if (!DatabaseConnection.testConnection()) {
            showMessage("Không kết nối được SQL Server. Kiểm tra db.properties và database phan_cong_de_tai_db.");
        }
    }

    @FXML
    private void handleLogin() {
        btnLogin.setDisable(true);
        clearMessage();

        try {
            User user = authService.login(txtUsername.getText(), txtPassword.getText());
            MainApp.showDashboardByRole(user.getVaiTro());

        } catch (Exception e) {
            showMessage(e.getMessage());
            txtPassword.clear();
            txtPassword.requestFocus();

        } finally {
            btnLogin.setDisable(false);
        }
    }

    private void showMessage(String message) {
        lblMessage.setVisible(true);
        lblMessage.setManaged(true);
        lblMessage.setText(message == null ? "Đăng nhập thất bại." : message);
    }

    private void clearMessage() {
        lblMessage.setText("");
        lblMessage.setVisible(false);
        lblMessage.setManaged(false);
    }
}
