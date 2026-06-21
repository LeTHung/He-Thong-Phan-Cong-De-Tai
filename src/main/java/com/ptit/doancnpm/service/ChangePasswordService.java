package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.ChangePasswordDAO;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.util.PasswordUtil;

/**
 * Nghiệp vụ người dùng tự đổi mật khẩu của chính mình.
 * Tái sử dụng {@link PasswordUtil} (của module xác thực) để kiểm tra mật khẩu
 * cũ và băm mật khẩu mới; không sửa đổi code module đó.
 */
public class ChangePasswordService {

    private static final int DO_DAI_TOI_THIEU = 6;

    private final ChangePasswordDAO changePasswordDAO;

    public ChangePasswordService() {
        this(new ChangePasswordDAO());
    }

    public ChangePasswordService(ChangePasswordDAO changePasswordDAO) {
        this.changePasswordDAO = changePasswordDAO;
    }

    /**
     * Đổi mật khẩu cho người dùng đang đăng nhập. Sau khi đổi thành công, cập nhật
     * luôn mật khẩu băm trong đối tượng {@code user} của phiên để các lần kiểm tra
     * tiếp theo trong phiên vẫn đúng.
     */
    public void changePassword(User user, String matKhauCu, String matKhauMoi, String xacNhanMatKhau) {
        if (user == null) {
            throw new IllegalStateException("Chưa đăng nhập.");
        }

        String cu = matKhauCu == null ? "" : matKhauCu.trim();
        String moi = matKhauMoi == null ? "" : matKhauMoi.trim();
        String xacNhan = xacNhanMatKhau == null ? "" : xacNhanMatKhau.trim();

        if (cu.isEmpty() || moi.isEmpty() || xacNhan.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ các ô mật khẩu.");
        }
        if (!PasswordUtil.matches(cu, user.getMatKhauMaHoa())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }
        if (moi.length() < DO_DAI_TOI_THIEU) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất " + DO_DAI_TOI_THIEU + " ký tự.");
        }
        if (!moi.equals(xacNhan)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu mới không khớp.");
        }
        if (PasswordUtil.matches(moi, user.getMatKhauMaHoa())) {
            throw new IllegalArgumentException("Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        String matKhauMaHoaMoi = PasswordUtil.sha256(moi);
        changePasswordDAO.updatePassword(user.getMaTaiKhoan(), matKhauMaHoaMoi);
        user.setMatKhauMaHoa(matKhauMaHoaMoi);
    }
}
