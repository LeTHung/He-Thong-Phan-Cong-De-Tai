package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDAO {

    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT
                    ma_tai_khoan,
                    ten_dang_nhap,
                    mat_khau_ma_hoa,
                    vai_tro,
                    trang_thai,
                    email,
                    so_dien_thoai,
                    anh_dai_dien,
                    lan_dang_nhap_cuoi,
                    thoi_diem_tao,
                    thoi_diem_cap_nhat
                FROM dbo.tai_khoan
                WHERE ten_dang_nhap = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                User user = new User(
                        resultSet.getInt("ma_tai_khoan"),
                        resultSet.getString("ten_dang_nhap"),
                        resultSet.getString("mat_khau_ma_hoa"),
                        UserRole.fromDatabaseValue(resultSet.getString("vai_tro")),
                        UserStatus.fromDatabaseValue(resultSet.getString("trang_thai")),
                        resultSet.getString("email"),
                        resultSet.getString("so_dien_thoai"),
                        resultSet.getString("anh_dai_dien"),
                        toLocalDateTime(resultSet.getTimestamp("lan_dang_nhap_cuoi")),
                        toLocalDateTime(resultSet.getTimestamp("thoi_diem_tao")),
                        toLocalDateTime(resultSet.getTimestamp("thoi_diem_cap_nhat")));

                return Optional.of(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn tài khoản: " + e.getMessage(), e);
        }
    }

    public void updateLastLogin(int maTaiKhoan) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET lan_dang_nhap_cuoi = SYSDATETIME(),
                    thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);
            statement.executeUpdate();

        } catch (SQLException e) {
            // Không cho lỗi cập nhật thời điểm đăng nhập làm hỏng luồng đăng nhập.
            System.err.println("Không cập nhật được lần đăng nhập cuối: " + e.getMessage());
        }
    }

    public void updatePasswordHash(int accountId, String passwordHash) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET mat_khau_ma_hoa = ?, thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setInt(2, accountId);
            if (statement.executeUpdate() == 0) {
                throw new RuntimeException("Không tìm thấy tài khoản cần nâng cấp mật khẩu.");
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Lỗi nâng cấp mật khẩu: " + exception.getMessage(), exception);
        }
    }

    private LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}
