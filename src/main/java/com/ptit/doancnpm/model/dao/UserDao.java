package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    public List<User> findAll() {
        String sql = """
                SELECT
                    tk.ma_tai_khoan AS id,
                    tk.ten_dang_nhap AS username,
                    COALESCE(gv.ho_ten, sv.ho_ten, tk.ten_dang_nhap) AS full_name,
                    COALESCE(gv.email, sv.email) AS email,
                    tk.ngay_tao AS created_at
                FROM dbo.tai_khoan tk
                LEFT JOIN dbo.giang_vien gv ON gv.ma_tai_khoan = tk.ma_tai_khoan
                LEFT JOIN dbo.sinh_vien sv ON sv.ma_tai_khoan = tk.ma_tai_khoan
                ORDER BY tk.ma_tai_khoan
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
            return users;
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot load users", exception);
        }
    }

    public Optional<User> findById(int id) {
        String sql = """
                SELECT
                    tk.ma_tai_khoan AS id,
                    tk.ten_dang_nhap AS username,
                    COALESCE(gv.ho_ten, sv.ho_ten, tk.ten_dang_nhap) AS full_name,
                    COALESCE(gv.email, sv.email) AS email,
                    tk.ngay_tao AS created_at
                FROM dbo.tai_khoan tk
                LEFT JOIN dbo.giang_vien gv ON gv.ma_tai_khoan = tk.ma_tai_khoan
                LEFT JOIN dbo.sinh_vien sv ON sv.ma_tai_khoan = tk.ma_tai_khoan
                WHERE tk.ma_tai_khoan = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot load user with id " + id, exception);
        }
    }

    public void create(User user) {
        String sql = """
                INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro)
                VALUES (?, ?, N'Sinh vien')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, "123456");
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot create user", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("full_name"),
                resultSet.getString("email"),
                resultSet.getTimestamp("created_at").toLocalDateTime());
    }
}
