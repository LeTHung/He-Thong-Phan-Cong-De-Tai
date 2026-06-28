package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AccountSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;
import com.ptit.doancnpm.service.AccountManagementService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AccountManagementController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private TableView<AccountSummary> tblAccounts;

    @FXML
    private TableColumn<AccountSummary, String> colUsername;

    @FXML
    private TableColumn<AccountSummary, String> colRole;

    @FXML
    private TableColumn<AccountSummary, String> colStatus;

    @FXML
    private TableColumn<AccountSummary, String> colEmail;

    @FXML
    private TableColumn<AccountSummary, String> colPhone;

    @FXML
    private TableColumn<AccountSummary, String> colLastLogin;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnLock;

    @FXML
    private Button btnUnlock;

    @FXML
    private Button btnResetPassword;

    private final AccountManagementService accountManagementService = new AccountManagementService();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý tài khoản.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        loadAccounts();
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.ADMIN_DASHBOARD_VIEW);
    }

    @FXML
    private void handleShowSubjects() {
        MainApp.setRoot(MainApp.SUBJECT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowSemesters() {
        MainApp.setRoot(MainApp.SEMESTER_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowCourseSections() {
        MainApp.setRoot(MainApp.COURSE_SECTION_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowStudentImport() {
        MainApp.setRoot(MainApp.STUDENT_IMPORT_VIEW);
    }

    @FXML
    private void handleShowReports() {
        MainApp.setRoot(MainApp.ADMIN_REPORT_VIEW);
    }

    @FXML
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này sẽ làm ở ngày tiếp theo.");
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddAccount() {
        showAccountDialog(null);
    }

    @FXML
    private void handleUpdateAccount() {
        AccountSummary selectedAccount = getSelectedAccount();
        if (selectedAccount == null) {
            showMessage("Vui lòng chọn tài khoản cần sửa.");
            return;
        }
        showAccountDialog(selectedAccount);
    }

    @FXML
    private void handleLockAccount() {
        AccountSummary selectedAccount = getSelectedAccount();
        if (selectedAccount == null) {
            showMessage("Vui lòng chọn tài khoản cần khóa.");
            return;
        }

        try {
            accountManagementService.lockAccount(selectedAccount.getMaTaiKhoan());
            showMessage("Đã khóa tài khoản.");
            loadAccounts();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleUnlockAccount() {
        AccountSummary selectedAccount = getSelectedAccount();
        if (selectedAccount == null) {
            showMessage("Vui lòng chọn tài khoản cần mở khóa.");
            return;
        }

        try {
            accountManagementService.unlockAccount(selectedAccount.getMaTaiKhoan());
            showMessage("Đã mở khóa tài khoản.");
            loadAccounts();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleResetPassword() {
        AccountSummary selectedAccount = getSelectedAccount();
        if (selectedAccount == null) {
            showMessage("Vui lòng chọn tài khoản cần reset mật khẩu.");
            return;
        }

        try {
            String newPassword = accountManagementService.resetPassword(selectedAccount.getMaTaiKhoan());
            showMessage("Đã reset mật khẩu về: " + newPassword);
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleRefreshAccounts() {
        loadAccounts();
        showMessage("Đã làm mới danh sách tài khoản.");
    }

    private void setupTable() {
        tblAccounts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenDangNhap()));
        colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getVaiTroText()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getEmail())));
        colPhone.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getSoDienThoai())));
        colLastLogin.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatLastLogin(data.getValue().getLanDangNhapCuoi())));

        btnEdit.disableProperty().bind(tblAccounts.getSelectionModel().selectedItemProperty().isNull());
        btnLock.disableProperty().bind(tblAccounts.getSelectionModel().selectedItemProperty().isNull());
        btnUnlock.disableProperty().bind(tblAccounts.getSelectionModel().selectedItemProperty().isNull());
        btnResetPassword.disableProperty().bind(tblAccounts.getSelectionModel().selectedItemProperty().isNull());

        tblAccounts.setRowFactory(table -> {
            TableRow<AccountSummary> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    tblAccounts.getSelectionModel().select(row.getItem());
                    handleUpdateAccount();
                }
            });
            return row;
        });
    }

    private void loadAccounts() {
        try {
            List<AccountSummary> accounts = accountManagementService.getAllAccounts();
            tblAccounts.getItems().setAll(accounts);
        } catch (RuntimeException exception) {
            tblAccounts.getItems().clear();
            showMessage(exception.getMessage());
        }
    }

    private void showAccountDialog(AccountSummary account) {
        boolean isEdit = account != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Sửa tài khoản" : "Thêm tài khoản");
        dialog.setHeaderText(isEdit
                ? "Chỉnh sửa thông tin tài khoản " + account.getTenDangNhap()
                : "Nhập thông tin tài khoản mới");
        dialog.initOwner(tblAccounts.getScene().getWindow());

        TextField usernameField = new TextField(isEdit ? account.getTenDangNhap() : "");
        usernameField.setPromptText("VD: N23DCCN023");
        usernameField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("VD: 123456");

        ComboBox<UserRole> roleBox = new ComboBox<>();
        roleBox.getItems().setAll(UserRole.values());
        roleBox.setConverter(roleConverter());
        roleBox.setValue(isEdit ? account.getVaiTro() : UserRole.SINH_VIEN);
        roleBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<UserStatus> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(UserStatus.values());
        statusBox.setConverter(statusConverter());
        statusBox.setValue(isEdit ? account.getTrangThai() : UserStatus.HOAT_DONG);
        statusBox.setMaxWidth(Double.MAX_VALUE);

        TextField emailField = new TextField(isEdit ? emptyIfNull(account.getEmail()) : "");
        emailField.setPromptText("email@ptit.edu.vn");

        TextField phoneField = new TextField(isEdit ? emptyIfNull(account.getSoDienThoai()) : "");
        phoneField.setPromptText("VD: 0900000000");

        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: 700;");

        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.setPadding(new Insets(8, 4, 4, 4));
        int row = 0;
        addFormRow(form, row++, "Tên đăng nhập", usernameField);
        if (!isEdit) {
            addFormRow(form, row++, "Mật khẩu", passwordField);
        }
        addFormRow(form, row++, "Vai trò", roleBox);
        addFormRow(form, row++, "Trạng thái", statusBox);
        addFormRow(form, row++, "Email", emailField);
        addFormRow(form, row++, "Số điện thoại", phoneField);
        form.add(errorLabel, 0, row, 2, 1);

        ButtonType saveButtonType = new ButtonType(
                isEdit ? "Lưu thay đổi" : "Thêm tài khoản",
                ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setPrefWidth(520);
        dialog.getDialogPane().getStyleClass().add("account-form-dialog");
        dialog.getDialogPane().getStylesheets().setAll(tblAccounts.getScene().getStylesheets());

        boolean[] saved = {false};
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.getStyleClass().add("btn-primary");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("btn-outline");
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                if (isEdit) {
                    accountManagementService.updateAccount(
                            account.getMaTaiKhoan(),
                            usernameField.getText(),
                            roleBox.getValue(),
                            statusBox.getValue(),
                            emailField.getText(),
                            phoneField.getText());
                } else {
                    accountManagementService.createAccount(
                            usernameField.getText(),
                            passwordField.getText(),
                            roleBox.getValue(),
                            statusBox.getValue(),
                            emailField.getText(),
                            phoneField.getText());
                }
                saved[0] = true;
            } catch (RuntimeException exception) {
                errorLabel.setText(exception.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
        if (saved[0]) {
            loadAccounts();
            tblAccounts.getSelectionModel().clearSelection();
            showMessage(isEdit ? "Đã cập nhật tài khoản." : "Đã thêm tài khoản.");
        }
    }

    private void addFormRow(GridPane form, int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("caption-strong");
        form.add(label, 0, row);
        form.add(field, 1, row);
        GridPane.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);
    }

    private StringConverter<UserRole> roleConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(UserRole role) {
                return role == null ? "" : role.getDisplayName();
            }

            @Override
            public UserRole fromString(String value) {
                return null;
            }
        };
    }

    private StringConverter<UserStatus> statusConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(UserStatus status) {
                return formatStatus(status);
            }

            @Override
            public UserStatus fromString(String value) {
                return null;
            }
        };
    }

    private AccountSummary getSelectedAccount() {
        return tblAccounts.getSelectionModel().getSelectedItem();
    }

    private String formatLastLogin(LocalDateTime lastLogin) {
        return lastLogin == null ? "Chưa đăng nhập" : lastLogin.format(DATE_TIME_FORMATTER);
    }

    private String formatStatus(UserStatus status) {
        if (status == null) {
            return "";
        }
        return status == UserStatus.HOAT_DONG ? "Hoạt động" : "Bị khóa";
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
