package com.ptit.doancnpm.controller;

import com.ptit.doancnpm.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeController {
    @FXML
    private Button testDbButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleTestConnection() {
        testDbButton.setDisable(true);
        statusLabel.setText("Dang kiem tra ket noi SQL Server...");

        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            statusLabel.setText("Ket noi SQL Server thanh cong.");
        } else {
            statusLabel.setText("Khong ket noi duoc DB. Hay kiem tra config/db.properties va SQL Server.");
        }

        testDbButton.setDisable(false);
    }
}
