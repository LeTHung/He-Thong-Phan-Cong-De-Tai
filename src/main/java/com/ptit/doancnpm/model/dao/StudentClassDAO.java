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

    public List<OptionItem> findAvailableStudentOptions(int courseSectionId) {
        String sql = """
                SELECT sv.ma_sinh_vien, sv.ma_so_sinh_vien, sv.ho_ten
                FROM dbo.sinh_vien sv
                WHERE sv.trang_thai = N'DANG_HOC'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM dbo.sinh_vien_lop svl
                      WHERE svl.ma_lop_hoc_phan = ?
                        AND svl.ma_sinh_vien = sv.ma_sinh_vien
                        AND svl.trang_thai = N'DANG_HOC'
                  )
                ORDER BY sv.ma_so_sinh_vien
                """;

        List<OptionItem> options = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    options.add(new OptionItem(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3)));
                }
            }
            return options;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách sinh viên có thể thêm: " + e.getMessage(), e);
        }
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

    public void addStudentsToCourseSection(int courseSectionId, List<Integer> studentIds, String note) {
        String updateSql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DANG_HOC',
                    ngay_tham_gia = CONVERT(DATE, SYSDATETIME()),
                    ghi_chu = ?
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;
        String insertSql = """
                INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (int studentId : studentIds) {
                    updateStatement.setString(1, note);
                    updateStatement.setInt(2, courseSectionId);
                    updateStatement.setInt(3, studentId);
                    int affectedRows = updateStatement.executeUpdate();

                    if (affectedRows == 0) {
                        insertStatement.setInt(1, courseSectionId);
                        insertStatement.setInt(2, studentId);
                        insertStatement.setString(3, note);
                        insertStatement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm danh sách sinh viên vào lớp: " + e.getMessage(), e);
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
