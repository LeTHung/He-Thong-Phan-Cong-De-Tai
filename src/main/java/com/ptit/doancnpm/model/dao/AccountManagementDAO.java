package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.AccountSummary;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AccountManagementDAO {

    public List<AccountSummary> findAll() {
        String sql = """
                SELECT
                    ma_tai_khoan,
                    ten_dang_nhap,
                    vai_tro,
                    trang_thai,
                    email,
                    so_dien_thoai,
                    lan_dang_nhap_cuoi
                FROM dbo.tai_khoan
                ORDER BY ma_tai_khoan
                """;

        List<AccountSummary> accounts = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                accounts.add(mapAccount(resultSet));
            }

            return accounts;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách tài khoản: " + e.getMessage(), e);
        }
    }

    public boolean existsByUsername(String username, Integer excludedAccountId) {
        String sql = """
                SELECT COUNT(1)
                FROM dbo.tai_khoan
                WHERE ten_dang_nhap = ?
                  AND (? IS NULL OR ma_tai_khoan <> ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            if (excludedAccountId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, excludedAccountId);
                statement.setInt(3, excludedAccountId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra tên đăng nhập: " + e.getMessage(), e);
        }
    }

    public void create(String username, String password, UserRole role, UserStatus status, String email, String phone) {
        String sql = """
                INSERT INTO dbo.tai_khoan (
                    ten_dang_nhap,
                    mat_khau_ma_hoa,
                    vai_tro,
                    trang_thai,
                    email,
                    so_dien_thoai
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role.name());
            statement.setString(4, status.name());
            statement.setString(5, email);
            statement.setString(6, phone);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm tài khoản: " + e.getMessage(), e);
        }
    }

    public void update(int accountId, String username, UserRole role, UserStatus status, String email, String phone) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET ten_dang_nhap = ?,
                    vai_tro = ?,
                    trang_thai = ?,
                    email = ?,
                    so_dien_thoai = ?,
                    thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, role.name());
            statement.setString(3, status.name());
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setInt(6, accountId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật tài khoản: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int accountId, UserStatus status) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET trang_thai = ?,
                    thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setInt(2, accountId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đổi trạng thái tài khoản: " + e.getMessage(), e);
        }
    }

    public void resetPassword(int accountId, String newPassword) {
        String sql = """
                UPDATE dbo.tai_khoan
                SET mat_khau_ma_hoa = ?,
                    thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setInt(2, accountId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi reset mật khẩu: " + e.getMessage(), e);
        }
    }

    private AccountSummary mapAccount(ResultSet resultSet) throws SQLException {
        Timestamp lastLogin = resultSet.getTimestamp("lan_dang_nhap_cuoi");

        return new AccountSummary(
                resultSet.getInt("ma_tai_khoan"),
                resultSet.getString("ten_dang_nhap"),
                UserRole.fromDatabaseValue(resultSet.getString("vai_tro")),
                UserStatus.fromDatabaseValue(resultSet.getString("trang_thai")),
                resultSet.getString("email"),
                resultSet.getString("so_dien_thoai"),
                lastLogin == null ? null : lastLogin.toLocalDateTime());
    }
}
