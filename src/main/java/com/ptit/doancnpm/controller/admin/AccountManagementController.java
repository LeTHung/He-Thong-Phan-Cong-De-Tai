package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AccountSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;
import com.ptit.doancnpm.service.AccountManagementService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<UserRole> cboRole;

    @FXML
    private ComboBox<UserStatus> cboStatus;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

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
        setupForm();
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
    private void handleNotImplemented() {
        MainApp.showInfo("Chức năng này sẽ làm ở ngày tiếp theo.");
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddAccount() {
        try {
            accountManagementService.createAccount(
                    txtUsername.getText(),
                    txtPassword.getText(),
                    cboRole.getValue(),
                    cboStatus.getValue(),
                    txtEmail.getText(),
                    txtPhone.getText());
            showMessage("Đã thêm tài khoản.");
            clearForm();
            loadAccounts();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleUpdateAccount() {
        AccountSummary selectedAccount = getSelectedAccount();
        if (selectedAccount == null) {
            showMessage("Vui lòng chọn tài khoản cần sửa.");
            return;
        }

        try {
            accountManagementService.updateAccount(
                    selectedAccount.getMaTaiKhoan(),
                    txtUsername.getText(),
                    cboRole.getValue(),
                    cboStatus.getValue(),
                    txtEmail.getText(),
                    txtPhone.getText());
            showMessage("Đã cập nhật tài khoản.");
            loadAccounts();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
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

    @FXML
    private void handleClearForm() {
        clearForm();
        showMessage("Đã xóa trắng dữ liệu đang nhập.");
    }

    private void setupTable() {
        colUsername.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTenDangNhap()));
        colRole.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getVaiTroText()));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getEmail())));
        colPhone.setCellValueFactory(data -> new ReadOnlyStringWrapper(emptyIfNull(data.getValue().getSoDienThoai())));
        colLastLogin.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatLastLogin(data.getValue().getLanDangNhapCuoi())));

        tblAccounts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillForm(newValue);
            }
        });
    }

    private void setupForm() {
        cboRole.getItems().setAll(UserRole.values());
        cboRole.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserRole role) {
                return role == null ? "" : role.getDisplayName();
            }

            @Override
            public UserRole fromString(String value) {
                return null;
            }
        });

        cboStatus.getItems().setAll(UserStatus.values());
        cboStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(UserStatus status) {
                return formatStatus(status);
            }

            @Override
            public UserStatus fromString(String value) {
                return null;
            }
        });

        cboRole.setValue(UserRole.SINH_VIEN);
        cboStatus.setValue(UserStatus.HOAT_DONG);
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

    private void fillForm(AccountSummary account) {
        txtUsername.setText(account.getTenDangNhap());
        txtPassword.clear();
        cboRole.setValue(account.getVaiTro());
        cboStatus.setValue(account.getTrangThai());
        txtEmail.setText(emptyIfNull(account.getEmail()));
        txtPhone.setText(emptyIfNull(account.getSoDienThoai()));
        showMessage("Đang chọn tài khoản " + account.getTenDangNhap() + ".");
    }

    private void clearForm() {
        tblAccounts.getSelectionModel().clearSelection();
        txtUsername.clear();
        txtPassword.clear();
        cboRole.setValue(UserRole.SINH_VIEN);
        cboStatus.setValue(UserStatus.HOAT_DONG);
        txtEmail.clear();
        txtPhone.clear();
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
