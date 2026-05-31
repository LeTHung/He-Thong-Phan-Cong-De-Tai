package com.ptit.doancnpm.app;

import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Scene mainScene;
    private static StackPane contentHost;
    private static Button maximizeButton;
    private static double dragOffsetX;
    private static double dragOffsetY;

    private static final String APP_TITLE = "UniTopics - Phân công đề tài sinh viên";
    private static final int LOGIN_WINDOW_WIDTH = 540;
    private static final int LOGIN_WINDOW_HEIGHT = 500;
    private static final int DASHBOARD_WINDOW_WIDTH = 1280;
    private static final int DASHBOARD_WINDOW_HEIGHT = 820;

    public static final String LOGIN_VIEW = "/views/auth/login-view.fxml";
    public static final String ADMIN_DASHBOARD_VIEW = "/views/admin/admin-dashboard.fxml";
    public static final String ACCOUNT_MANAGEMENT_VIEW = "/views/admin/account-management.fxml";
    public static final String SUBJECT_MANAGEMENT_VIEW = "/views/admin/subject-management.fxml";
    public static final String SEMESTER_MANAGEMENT_VIEW = "/views/admin/semester-management.fxml";
    public static final String COURSE_SECTION_MANAGEMENT_VIEW = "/views/admin/course-section-management.fxml";
    public static final String STUDENT_IMPORT_VIEW = "/views/admin/student-import.fxml";
    public static final String ADMIN_REPORT_VIEW = "/views/admin/report.fxml";
    public static final String LECTURER_DASHBOARD_VIEW = "/views/lecturer/lecturer-dashboard.fxml";
    public static final String LECTURER_COURSE_SECTIONS_VIEW = "/views/lecturer/my-course-sections.fxml";
    public static final String LECTURER_TOPIC_BANK_VIEW = "/views/lecturer/topic-bank.fxml";
    public static final String LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW = "/views/lecturer/assign-topic-to-class.fxml";
    public static final String LECTURER_REGISTRATION_PERIOD_VIEW = "/views/lecturer/registration-period.fxml";
    public static final String LECTURER_REGISTRATION_RESULT_VIEW = "/views/lecturer/registration-result.fxml";
    public static final String LECTURER_FINAL_REPORT_VIEW = "/views/lecturer/final-report.fxml";
    public static final String STUDENT_DASHBOARD_VIEW = "/views/student/student-dashboard.fxml";
    public static final String STUDENT_TOPIC_LIST_VIEW = "/views/student/topic-list.fxml";
    public static final String STUDENT_TOPIC_DETAIL_VIEW = "/views/student/topic-detail.fxml";
    public static final String STUDENT_MY_REGISTRATION_VIEW = "/views/student/my-registration.fxml";
    public static final String STUDENT_PROFILE_VIEW = "/views/student/student-profile.fxml";

    @Override
    public void start(Stage stage) {
        showLoginWindow(stage);
    }

    /*
     * Không dùng app.css.
     * MainApp import trực tiếp từng file CSS.
     */
    private static void loadStylesheets(Scene scene) {
        String[] cssFiles = {
                "/styles/globals/colors.css",
                "/styles/globals/base.css",
                "/styles/globals/typography.css",
                "/styles/globals/layout.css",
                "/styles/globals/components.css",
                "/styles/globals/topbar.css",

                "/styles/pages/login.css",
                "/styles/pages/admin-dashboard.css",
                "/styles/pages/lecturer-dashboard.css",
                "/styles/pages/student-dashboard.css",
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
        Stage oldStage = primaryStage;
        showLoginWindow(new Stage());
        if (oldStage != null) {
            oldStage.close();
        }
    }

    public static void showDashboardByRole(UserRole role) {
        if (role == null) {
            showError("Không xác định được vai trò người dùng.");
            showLogin();
            return;
        }

        switch (role) {
            case QUAN_TRI_VIEN -> showDashboard(ADMIN_DASHBOARD_VIEW);
            case GIANG_VIEN -> showDashboard(LECTURER_DASHBOARD_VIEW);
            case SINH_VIEN -> showDashboard(STUDENT_DASHBOARD_VIEW);
            default -> {
                showError("Vai trò không hợp lệ: " + role);
                showLogin();
            }
        }
    }

    public static void setRoot(String fxmlPath) {
        Parent root = loadView(fxmlPath);
        if (contentHost != null && root != null) {
            contentHost.getChildren().setAll(root);
        } else if (mainScene != null && root != null) {
            mainScene.setRoot(root);
        }
    }

    private static BorderPane createWindowShell() {
        BorderPane shell = new BorderPane();
        shell.getStyleClass().add("app-window-shell");
        shell.setTop(createTitleBar());
        shell.setCenter(contentHost);

        Rectangle clip = new Rectangle();
        clip.setArcWidth(28);
        clip.setArcHeight(28);
        clip.widthProperty().bind(shell.widthProperty());
        clip.heightProperty().bind(shell.heightProperty());
        shell.setClip(clip);

        return shell;
    }

    private static HBox createTitleBar() {
        Label title = new Label(APP_TITLE);
        title.getStyleClass().add("window-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeButton = createWindowButton("_");
        minimizeButton.setOnAction(event -> primaryStage.setIconified(true));

        maximizeButton = createWindowButton("□");
        maximizeButton.setOnAction(event -> primaryStage.setMaximized(!primaryStage.isMaximized()));

        Button closeButton = createWindowButton("X");
        closeButton.getStyleClass().add("window-close-button");
        closeButton.setOnAction(event -> primaryStage.close());

        HBox titleBar = new HBox(title, spacer, minimizeButton, maximizeButton, closeButton);
        titleBar.getStyleClass().add("window-title-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);

        titleBar.setOnMousePressed(event -> {
            if (!primaryStage.isMaximized()) {
                dragOffsetX = event.getSceneX();
                dragOffsetY = event.getSceneY();
            }
        });
        titleBar.setOnMouseDragged(event -> {
            if (!primaryStage.isMaximized()) {
                primaryStage.setX(event.getScreenX() - dragOffsetX);
                primaryStage.setY(event.getScreenY() - dragOffsetY);
            }
        });
        titleBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && primaryStage.isResizable()) {
                primaryStage.setMaximized(!primaryStage.isMaximized());
            }
        });

        return titleBar;
    }

    private static Button createWindowButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("window-control-button");
        return button;
    }

    private static void showDashboard(String fxmlPath) {
        Parent root = loadView(fxmlPath);
        Stage oldStage = primaryStage;
        Stage dashboardStage = new Stage();
        Scene dashboardScene = new Scene(root, DASHBOARD_WINDOW_WIDTH, DASHBOARD_WINDOW_HEIGHT);
        loadStylesheets(dashboardScene);

        primaryStage = dashboardStage;
        mainScene = dashboardScene;
        contentHost = null;
        maximizeButton = null;

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        if (oldStage != null) {
            oldStage.close();
        }
    }

    private static void showLoginWindow(Stage loginStage) {
        primaryStage = loginStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        Parent root = loadView(LOGIN_VIEW);
        contentHost = new StackPane(root);
        mainScene = new Scene(createWindowShell(), LOGIN_WINDOW_WIDTH, LOGIN_WINDOW_HEIGHT);
        mainScene.setFill(Color.TRANSPARENT);
        loadStylesheets(mainScene);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(false);
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(LOGIN_WINDOW_WIDTH);
        primaryStage.setMinHeight(LOGIN_WINDOW_HEIGHT);
        primaryStage.setWidth(LOGIN_WINDOW_WIDTH);
        primaryStage.setHeight(LOGIN_WINDOW_HEIGHT);
        if (maximizeButton != null) {
            maximizeButton.setDisable(true);
        }
        primaryStage.centerOnScreen();
        primaryStage.show();
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
