package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.entity.Semester;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SemesterDAO {

    public List<Semester> findAll() {
        String sql = """
                SELECT ma_hoc_ky, ma_hoc_ky_he_thong, ten_hoc_ky, nam_hoc,
                       ngay_bat_dau, ngay_ket_thuc, trang_thai, thoi_diem_tao
                FROM dbo.hoc_ky
                ORDER BY nam_hoc DESC, ma_hoc_ky_he_thong
                """;

        List<Semester> semesters = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                semesters.add(mapSemester(resultSet));
            }
            return semesters;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách học kỳ: " + e.getMessage(), e);
        }
    }

    public boolean existsByCode(String code, Integer excludedId) {
        String sql = """
                SELECT COUNT(1)
                FROM dbo.hoc_ky
                WHERE ma_hoc_ky_he_thong = ?
                  AND (? IS NULL OR ma_hoc_ky <> ?)
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
            throw new RuntimeException("Lỗi kiểm tra mã học kỳ: " + e.getMessage(), e);
        }
    }

    public void create(Semester semester) {
        String sql = """
                INSERT INTO dbo.hoc_ky (
                    ma_hoc_ky_he_thong, ten_hoc_ky, nam_hoc,
                    ngay_bat_dau, ngay_ket_thuc, trang_thai
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, semester);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm học kỳ: " + e.getMessage(), e);
        }
    }

    public void update(Semester semester) {
        String sql = """
                UPDATE dbo.hoc_ky
                SET ma_hoc_ky_he_thong = ?,
                    ten_hoc_ky = ?,
                    nam_hoc = ?,
                    ngay_bat_dau = ?,
                    ngay_ket_thuc = ?,
                    trang_thai = ?
                WHERE ma_hoc_ky = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, semester);
            statement.setInt(7, semester.getMaHocKy());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật học kỳ: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.hoc_ky SET trang_thai = ? WHERE ma_hoc_ky = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đổi trạng thái học kỳ: " + e.getMessage(), e);
        }
    }

    private void fillStatement(
            PreparedStatement statement,
            Semester semester) throws SQLException {
        statement.setString(1, semester.getMaHocKyHeThong());
        statement.setString(2, semester.getTenHocKy());
        statement.setString(3, semester.getNamHoc());
        statement.setDate(4, semester.getNgayBatDau() == null ? null : Date.valueOf(semester.getNgayBatDau()));
        statement.setDate(5, semester.getNgayKetThuc() == null ? null : Date.valueOf(semester.getNgayKetThuc()));
        statement.setString(6, semester.getTrangThai());
    }

    private Semester mapSemester(ResultSet resultSet) throws SQLException {
        Date startDate = resultSet.getDate("ngay_bat_dau");
        Date endDate = resultSet.getDate("ngay_ket_thuc");

        var createdAt = resultSet.getTimestamp("thoi_diem_tao");
        return new Semester(
                resultSet.getInt("ma_hoc_ky"),
                resultSet.getString("ma_hoc_ky_he_thong"),
                resultSet.getString("ten_hoc_ky"),
                resultSet.getString("nam_hoc"),
                startDate == null ? null : startDate.toLocalDate(),
                endDate == null ? null : endDate.toLocalDate(),
                resultSet.getString("trang_thai"),
                createdAt == null ? null : createdAt.toLocalDateTime());
    }
}
