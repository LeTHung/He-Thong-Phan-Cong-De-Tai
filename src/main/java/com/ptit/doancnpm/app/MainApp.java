package com.ptit.doancnpm.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private static Stage primaryStage;
    private static Scene mainScene;

    private static final String APP_TITLE = "UniTopics - Phân công đề tài sinh viên";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    /*
     * Màn hình đầu tiên khi chạy app.
     * Nếu sau này bạn có login-view.fxml thì đổi thành:
     * private static final String START_VIEW = "/views/login-view.fxml";
     */
    private static final String START_VIEW = "/views/home-view.fxml";

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        Parent root = loadViewOrDefault(START_VIEW);

        mainScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        loadStylesheets(mainScene);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    /*
     * Load toàn bộ CSS trực tiếp, không dùng app.css.
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
                "/styles/pages/student-topic-registration.css",
                "/styles/pages/admin-dashboard.css"
        };

        for (String cssFile : cssFiles) {
            URL cssUrl = getClass().getResource(cssFile);

            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Không tìm thấy file CSS: " + cssFile);
            }
        }
    }

    /*
     * Load FXML. Nếu chưa có file FXML thì app vẫn chạy bằng màn hình tạm.
     */
    private Parent loadViewOrDefault(String fxmlPath) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Không tìm thấy FXML: " + fxmlPath);
                return createDefaultView();
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            return loader.load();

        } catch (IOException e) {
            e.printStackTrace();
            return createDefaultView();
        }
    }

    /*
     * Hàm dùng để chuyển màn hình từ các Controller.
     * Ví dụ trong Controller gọi:
     * MainApp.setRoot("/views/login-view.fxml");
     */
    public static void setRoot(String fxmlPath) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("Không tìm thấy FXML: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            mainScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Màn hình tạm nếu chưa có FXML.
     */
    private Parent createDefaultView() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("page-root");

        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.setSpacing(16);
        content.setPadding(new Insets(40));
        content.getStyleClass().add("card-large");

        Label title = new Label("UniTopics");
        title.getStyleClass().add("h1");

        Label subtitle = new Label("Ứng dụng phân công đề tài cho sinh viên");
        subtitle.getStyleClass().add("body-muted");

        Label note = new Label(
                "Chưa tìm thấy file FXML khởi động. Hãy tạo /views/home-view.fxml hoặc đổi START_VIEW trong MainApp.java.");
        note.getStyleClass().add("caption");
        note.setWrapText(true);

        content.getChildren().addAll(title, subtitle, note);

        root.setCenter(content);
        BorderPane.setMargin(content, new Insets(60));

        return root;
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