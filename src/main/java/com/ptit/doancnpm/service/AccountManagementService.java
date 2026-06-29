package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.AccountManagementDAO;
import com.ptit.doancnpm.model.dto.AccountSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;
import com.ptit.doancnpm.util.PasswordUtil;

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
            String phone,
            String hoTen,
            String lop) {
        String cleanUsername = cleanRequired(username, "Tên đăng nhập không được để trống.");
        String cleanPassword = cleanRequired(password, "Mật khẩu không được để trống.");
        UserRole cleanRole = requireRole(role);
        UserStatus cleanStatus = requireStatus(status);
        validateProfileCode(cleanUsername, cleanRole);

        if (accountManagementDAO.existsByUsername(cleanUsername, null)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        if (accountManagementDAO.existsProfileCode(cleanUsername, cleanRole)) {
            throw new IllegalArgumentException("Mã số \"" + cleanUsername + "\" đã tồn tại trong hồ sơ "
                    + cleanRole.getDisplayName().toLowerCase() + ". Hãy dùng tên đăng nhập khác.");
        }

        accountManagementDAO.create(new User(
                0, cleanUsername, PasswordUtil.hash(cleanPassword), cleanRole, cleanStatus,
                cleanOptional(email), cleanOptional(phone)),
                cleanOptional(hoTen), profileClass(cleanRole, lop));
    }

    public void updateAccount(
            int accountId,
            String username,
            UserRole role,
            UserStatus status,
            String email,
            String phone,
            String hoTen,
            String lop) {
        String cleanUsername = cleanRequired(username, "Tên đăng nhập không được để trống.");
        UserRole cleanRole = requireRole(role);
        UserStatus cleanStatus = requireStatus(status);
        validateProfileCode(cleanUsername, cleanRole);

        UserRole currentRole = accountManagementDAO.findRoleById(accountId);
        if (currentRole != cleanRole) {
            throw new IllegalArgumentException(
                    "Không thể thay đổi vai trò của tài khoản đã tạo. Hãy tạo tài khoản mới đúng vai trò.");
        }

        if (accountManagementDAO.existsByUsername(cleanUsername, accountId)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }

        accountManagementDAO.update(new User(
                accountId, cleanUsername, null, cleanRole, cleanStatus,
                cleanOptional(email), cleanOptional(phone)),
                cleanOptional(hoTen), profileClass(cleanRole, lop));
    }

    /** Lớp chỉ áp dụng cho sinh viên; vai trò khác bỏ qua giá trị nhập. */
    private String profileClass(UserRole role, String lop) {
        return role == UserRole.SINH_VIEN ? cleanOptional(lop) : null;
    }

    public void lockAccount(int accountId) {
        accountManagementDAO.updateStatus(accountId, UserStatus.BI_KHOA);
    }

    public void unlockAccount(int accountId) {
        accountManagementDAO.updateStatus(accountId, UserStatus.HOAT_DONG);
    }

    public String resetPassword(int accountId) {
        accountManagementDAO.resetPassword(accountId, PasswordUtil.hash(DEFAULT_RESET_PASSWORD));
        return DEFAULT_RESET_PASSWORD;
    }

    private void validateProfileCode(String username, UserRole role) {
        if (role != UserRole.QUAN_TRI_VIEN && username.length() > 30) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập của sinh viên/giảng viên không được vượt quá 30 ký tự.");
        }
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
