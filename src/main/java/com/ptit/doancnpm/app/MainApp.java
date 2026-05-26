package com.ptit.doancnpm.app;

import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Scene mainScene;

    private static final String APP_TITLE = "UniTopics - Phân công đề tài sinh viên";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    public static final String LOGIN_VIEW = "/views/auth/login-view.fxml";
    public static final String ADMIN_DASHBOARD_VIEW = "/views/admin/admin-dashboard.fxml";
    public static final String LECTURER_DASHBOARD_VIEW = "/views/lecturer/lecturer-dashboard.fxml";
    public static final String STUDENT_DASHBOARD_VIEW = "/views/student/student-dashboard.fxml";

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Parent root = loadView(LOGIN_VIEW);
        mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        loadStylesheets(mainScene);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    /*
     * Load CSS trực tiếp từng file, không dùng app.css.
     * Nếu file CSS chưa tồn tại, app vẫn chạy và chỉ in cảnh báo.
     */
    private void loadStylesheets(Scene scene) {
        String[] cssFiles = {
                "/styles/globals/colors.css",
                "/styles/globals/base.css",
                "/styles/globals/typography.css",
                "/styles/globals/layout.css",
                "/styles/globals/components.css",
                "/styles/globals/topbar.css",

                "/styles/pages/login.css",
                "/styles/pages/admin-dashboard.css",
                "/styles/pages/student-topic-registration.css"
        };

        for (String cssFile : cssFiles) {
            URL cssUrl = MainApp.class.getResource(cssFile);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Không tìm thấy CSS: " + cssFile);
            }
        }
    }

    public static void showLogin() {
        SessionManager.clear();
        setRoot(LOGIN_VIEW);
    }

    public static void showDashboardByRole(UserRole role) {
        if (role == null) {
            showError("Không xác định được vai trò người dùng.");
            showLogin();
            return;
        }

        switch (role) {
            case QUAN_TRI_VIEN -> setRoot(ADMIN_DASHBOARD_VIEW);
            case GIANG_VIEN -> setRoot(LECTURER_DASHBOARD_VIEW);
            case SINH_VIEN -> setRoot(STUDENT_DASHBOARD_VIEW);
            default -> {
                showError("Vai trò không hợp lệ");
                showLogin();
            }
        }
    }

    public static void setRoot(String fxmlPath) {
        Parent root = loadView(fxmlPath);
        if (mainScene != null && root != null) {
            mainScene.setRoot(root);
        }
    }

    private static Parent loadView(String fxmlPath) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                throw new IOException("Không tìm thấy file FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            return loader.load();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể tải màn hình: " + fxmlPath + "\n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static Scene getMainScene() {
        return mainScene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
