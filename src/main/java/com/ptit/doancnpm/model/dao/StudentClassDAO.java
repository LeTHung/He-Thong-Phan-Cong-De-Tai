package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentClassDAO {

    public List<OptionItem> findCourseSectionOptions() {
        String sql = """
                SELECT ma_lop_hoc_phan, ma_lop, ten_lop_hoc_phan
                FROM dbo.lop_hoc_phan
                WHERE trang_thai <> N'LUU_TRU'
                ORDER BY ma_lop
                """;
        return findOptions(sql);
    }

    public List<OptionItem> findAvailableStudentOptions() {
        String sql = """
                SELECT ma_sinh_vien, ma_so_sinh_vien, ho_ten
                FROM dbo.sinh_vien
                WHERE trang_thai = N'DANG_HOC'
                ORDER BY ma_so_sinh_vien
                """;
        return findOptions(sql);
    }

    public List<StudentClassMemberSummary> findStudentsByCourseSection(int courseSectionId) {
        String sql = """
                SELECT
                    svl.ma_lop_hoc_phan,
                    sv.ma_sinh_vien,
                    sv.ma_so_sinh_vien,
                    sv.ho_ten,
                    sv.email,
                    sv.lop_sinh_hoat,
                    svl.trang_thai,
                    svl.ghi_chu
                FROM dbo.sinh_vien_lop svl
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                WHERE svl.ma_lop_hoc_phan = ?
                ORDER BY
                    CASE WHEN svl.trang_thai = N'DANG_HOC' THEN 0 ELSE 1 END,
                    sv.ma_so_sinh_vien
                """;

        List<StudentClassMemberSummary> students = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách sinh viên lớp: " + e.getMessage(), e);
        }
    }

    public String findEnrollmentStatus(int courseSectionId, int studentId) {
        String sql = """
                SELECT trang_thai
                FROM dbo.sinh_vien_lop
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("trang_thai") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra sinh viên trong lớp: " + e.getMessage(), e);
        }
    }

    public void addStudentToCourseSection(int courseSectionId, int studentId, String note) {
        String status = findEnrollmentStatus(courseSectionId, studentId);
        if (status == null) {
            insertStudent(courseSectionId, studentId, note);
            return;
        }

        String sql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DANG_HOC',
                    ngay_tham_gia = CONVERT(DATE, SYSDATETIME()),
                    ghi_chu = ?
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, note);
            statement.setInt(2, courseSectionId);
            statement.setInt(3, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm lại sinh viên vào lớp: " + e.getMessage(), e);
        }
    }

    public void withdrawStudentFromCourseSection(int courseSectionId, int studentId) {
        String sql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DA_RUT'
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Không tìm thấy sinh viên trong lớp để rút.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi rút sinh viên khỏi lớp: " + e.getMessage(), e);
        }
    }

    private void insertStudent(int courseSectionId, int studentId, String note) {
        String sql = """
                INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            statement.setString(3, note);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm sinh viên vào lớp: " + e.getMessage(), e);
        }
    }

    private List<OptionItem> findOptions(String sql) {
        List<OptionItem> options = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                options.add(new OptionItem(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)));
            }
            return options;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải dữ liệu chọn: " + e.getMessage(), e);
        }
    }

    private StudentClassMemberSummary mapStudent(ResultSet resultSet) throws SQLException {
        return new StudentClassMemberSummary(
                resultSet.getInt("ma_lop_hoc_phan"),
                resultSet.getInt("ma_sinh_vien"),
                resultSet.getString("ma_so_sinh_vien"),
                resultSet.getString("ho_ten"),
                resultSet.getString("email"),
                resultSet.getString("lop_sinh_hoat"),
                resultSet.getString("trang_thai"),
                resultSet.getString("ghi_chu"));
    }
}
