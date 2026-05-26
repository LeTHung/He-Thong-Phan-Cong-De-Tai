package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.UserDAO;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.util.PasswordUtil;
import com.ptit.doancnpm.util.SessionManager;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        String cleanUsername = username == null ? "" : username.trim();
        String cleanPassword = password == null ? "" : password.trim();

        if (cleanUsername.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập tên đăng nhập.");
        }

        if (cleanPassword.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mật khẩu.");
        }

        User user = userDAO.findByUsername(cleanUsername)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại."));

        if (user.getTrangThai() == null || !user.getTrangThai().isActive()) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa, vui lòng liên hệ quản trị viên.");
        }

        if (!PasswordUtil.matches(cleanPassword, user.getMatKhauMaHoa())) {
            throw new IllegalArgumentException("Mật khẩu không đúng.");
        }

        userDAO.updateLastLogin(user.getMaTaiKhoan());
        SessionManager.setCurrentUser(user);

        return user;
    }

    public void logout() {
        SessionManager.clear();
    }
}
