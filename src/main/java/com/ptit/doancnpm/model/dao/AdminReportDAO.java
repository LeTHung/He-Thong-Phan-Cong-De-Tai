package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.AdminCourseSectionReport;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminReportDAO {

    public List<AdminCourseSectionReport> findCourseSectionReports() {
        String sql = """
                SELECT
                    ma_lop_hoc_phan,
                    ma_lop,
                    ten_lop_hoc_phan,
                    tong_so_sinh_vien,
                    so_sinh_vien_da_co_de_tai,
                    so_sinh_vien_chua_co_de_tai,
                    tong_so_de_tai
                FROM dbo.vw_thong_ke_lop_hoc_phan
                ORDER BY ma_lop
                """;

        List<AdminCourseSectionReport> reports = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                reports.add(new AdminCourseSectionReport(
                        resultSet.getInt("ma_lop_hoc_phan"),
                        resultSet.getString("ma_lop"),
                        resultSet.getString("ten_lop_hoc_phan"),
                        resultSet.getInt("tong_so_sinh_vien"),
                        resultSet.getInt("so_sinh_vien_da_co_de_tai"),
                        resultSet.getInt("so_sinh_vien_chua_co_de_tai"),
                        resultSet.getInt("tong_so_de_tai")));
            }
            return reports;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải báo cáo lớp học phần: " + e.getMessage(), e);
        }
    }
}
