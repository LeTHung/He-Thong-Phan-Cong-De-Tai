package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Cập nhật mật khẩu cho một tài khoản (phục vụ chức năng người dùng tự đổi mật khẩu).
 */
public class ChangePasswordDAO {

    public void updatePassword(int maTaiKhoan, String matKhauMaHoaMoi) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET mat_khau_ma_hoa = ?,
                    thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matKhauMaHoaMoi);
            statement.setInt(2, maTaiKhoan);
            int affected = statement.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Không tìm thấy tài khoản để đổi mật khẩu.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đổi mật khẩu: " + e.getMessage(), e);
        }
    }
}
