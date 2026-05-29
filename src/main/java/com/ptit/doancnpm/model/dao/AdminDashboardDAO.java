package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.AdminDashboardSummary;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardDAO {

    public AdminDashboardSummary getSummary() {
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM dbo.tai_khoan) AS total_accounts,
                    (SELECT COUNT(*) FROM dbo.tai_khoan WHERE trang_thai = N'HOAT_DONG') AS active_accounts,
                    (SELECT COUNT(*) FROM dbo.sinh_vien) AS total_students,
                    (SELECT COUNT(*) FROM dbo.sinh_vien WHERE trang_thai = N'DANG_HOC') AS active_students,
                    (SELECT COUNT(*) FROM dbo.giang_vien) AS total_lecturers,
                    (SELECT COUNT(*) FROM dbo.giang_vien WHERE trang_thai = N'DANG_CONG_TAC') AS active_lecturers,
                    (SELECT COUNT(*) FROM dbo.lop_hoc_phan) AS total_course_sections,
                    (SELECT COUNT(*) FROM dbo.lop_hoc_phan WHERE trang_thai = N'DANG_MO') AS open_course_sections
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                return new AdminDashboardSummary(0, 0, 0, 0, 0, 0, 0, 0);
            }

            return new AdminDashboardSummary(
                    resultSet.getInt("total_accounts"),
                    resultSet.getInt("active_accounts"),
                    resultSet.getInt("total_students"),
                    resultSet.getInt("active_students"),
                    resultSet.getInt("total_lecturers"),
                    resultSet.getInt("active_lecturers"),
                    resultSet.getInt("total_course_sections"),
                    resultSet.getInt("open_course_sections"));
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thống kê dashboard admin: " + e.getMessage(), e);
        }
    }
}
