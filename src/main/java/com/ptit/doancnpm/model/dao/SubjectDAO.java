package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.SubjectSummary;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<SubjectSummary> findAll() {
        String sql = """
                SELECT ma_mon_hoc, ma_mon_hoc_he_thong, ten_mon_hoc, so_tin_chi, mo_ta, trang_thai
                FROM dbo.mon_hoc
                ORDER BY ma_mon_hoc_he_thong
                """;

        List<SubjectSummary> subjects = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                subjects.add(mapSubject(resultSet));
            }
            return subjects;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách môn học: " + e.getMessage(), e);
        }
    }

    public boolean existsByCode(String code, Integer excludedId) {
        String sql = """
                SELECT COUNT(1)
                FROM dbo.mon_hoc
                WHERE ma_mon_hoc_he_thong = ?
                  AND (? IS NULL OR ma_mon_hoc <> ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            if (excludedId == null) {
                statement.setNull(2, java.sql.Types.INTEGER);
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(2, excludedId);
                statement.setInt(3, excludedId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra mã môn học: " + e.getMessage(), e);
        }
    }

    public void create(String code, String name, int credits, String description, String status) {
        String sql = """
                INSERT INTO dbo.mon_hoc (ma_mon_hoc_he_thong, ten_mon_hoc, so_tin_chi, mo_ta, trang_thai)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, name);
            statement.setInt(3, credits);
            statement.setString(4, description);
            statement.setString(5, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm môn học: " + e.getMessage(), e);
        }
    }

    public void update(int id, String code, String name, int credits, String description, String status) {
        String sql = """
                UPDATE dbo.mon_hoc
                SET ma_mon_hoc_he_thong = ?,
                    ten_mon_hoc = ?,
                    so_tin_chi = ?,
                    mo_ta = ?,
                    trang_thai = ?
                WHERE ma_mon_hoc = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            statement.setString(2, name);
            statement.setInt(3, credits);
            statement.setString(4, description);
            statement.setString(5, status);
            statement.setInt(6, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật môn học: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.mon_hoc SET trang_thai = ? WHERE ma_mon_hoc = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đổi trạng thái môn học: " + e.getMessage(), e);
        }
    }

    private SubjectSummary mapSubject(ResultSet resultSet) throws SQLException {
        return new SubjectSummary(
                resultSet.getInt("ma_mon_hoc"),
                resultSet.getString("ma_mon_hoc_he_thong"),
                resultSet.getString("ten_mon_hoc"),
                resultSet.getInt("so_tin_chi"),
                resultSet.getString("mo_ta"),
                resultSet.getString("trang_thai"));
    }
}
