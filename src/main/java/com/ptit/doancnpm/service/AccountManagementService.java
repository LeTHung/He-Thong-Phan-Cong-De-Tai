package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AccountManagementDAO;
import com.ptit.doancnpm.model.dto.AccountSummary;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;

import java.util.List;

public class AccountManagementService {

    private static final String DEFAULT_RESET_PASSWORD = "123456";

    private final AccountManagementDAO accountManagementDAO = new AccountManagementDAO();

    public List<AccountSummary> getAllAccounts() {
        return accountManagementDAO.findAll();
    }

    public void createAccount(
            String username,
            String password,
            UserRole role,
            UserStatus status,
            String email,
            String phone) {
        String cleanUsername = cleanRequired(username, "Tên đăng nhập không được để trống.");
        String cleanPassword = cleanRequired(password, "Mật khẩu không được để trống.");
        UserRole cleanRole = requireRole(role);
        UserStatus cleanStatus = requireStatus(status);

        if (accountManagementDAO.existsByUsername(cleanUsername, null)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        accountManagementDAO.create(
                cleanUsername,
                cleanPassword,
                cleanRole,
                cleanStatus,
                cleanOptional(email),
                cleanOptional(phone));
    }

    public void updateAccount(
            int accountId,
            String username,
            UserRole role,
            UserStatus status,
            String email,
            String phone) {
        String cleanUsername = cleanRequired(username, "Tên đăng nhập không được để trống.");
        UserRole cleanRole = requireRole(role);
        UserStatus cleanStatus = requireStatus(status);

        if (accountManagementDAO.existsByUsername(cleanUsername, accountId)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        accountManagementDAO.update(
                accountId,
                cleanUsername,
                cleanRole,
                cleanStatus,
                cleanOptional(email),
                cleanOptional(phone));
    }

    public void lockAccount(int accountId) {
        accountManagementDAO.updateStatus(accountId, UserStatus.BI_KHOA);
    }

    public void unlockAccount(int accountId) {
        accountManagementDAO.updateStatus(accountId, UserStatus.HOAT_DONG);
    }

    public String resetPassword(int accountId) {
        accountManagementDAO.resetPassword(accountId, DEFAULT_RESET_PASSWORD);
        return DEFAULT_RESET_PASSWORD;
    }

    private String cleanRequired(String value, String errorMessage) {
        String cleanValue = value == null ? "" : value.trim();
        if (cleanValue.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        return cleanValue;
    }

    private String cleanOptional(String value) {
        String cleanValue = value == null ? "" : value.trim();
        return cleanValue.isBlank() ? null : cleanValue;
    }

    private UserRole requireRole(UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        }
        return role;
    }

    private UserStatus requireStatus(UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Vui lòng chọn trạng thái.");
        }
        return status;
    }
}
