package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.CourseSectionSummary;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseSectionDAO {

    public List<CourseSectionSummary> findAll() {
        String sql = """
                SELECT
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan,
                    lhp.ma_mon_hoc,
                    mh.ten_mon_hoc,
                    lhp.ma_hoc_ky,
                    hk.ten_hoc_ky,
                    hk.nam_hoc,
                    lhp.ma_giang_vien,
                    gv.ho_ten AS ten_giang_vien,
                    lhp.si_so_toi_da,
                    lhp.ghi_chu,
                    lhp.trang_thai
                FROM dbo.lop_hoc_phan lhp
                JOIN dbo.mon_hoc mh ON mh.ma_mon_hoc = lhp.ma_mon_hoc
                JOIN dbo.hoc_ky hk ON hk.ma_hoc_ky = lhp.ma_hoc_ky
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                ORDER BY hk.nam_hoc DESC, lhp.ma_lop
                """;

        List<CourseSectionSummary> sections = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                sections.add(mapSection(resultSet));
            }
            return sections;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách lớp học phần: " + e.getMessage(), e);
        }
    }

    public List<OptionItem> findSubjectOptions() {
        String sql = """
                SELECT ma_mon_hoc, ma_mon_hoc_he_thong, ten_mon_hoc
                FROM dbo.mon_hoc
                WHERE trang_thai = N'DANG_SU_DUNG'
                ORDER BY ma_mon_hoc_he_thong
                """;
        return findOptions(sql);
    }

    public List<OptionItem> findSemesterOptions() {
        String sql = """
                SELECT ma_hoc_ky, ma_hoc_ky_he_thong, ten_hoc_ky + N' ' + nam_hoc AS ten_hoc_ky
                FROM dbo.hoc_ky
                WHERE trang_thai = N'DANG_MO'
                ORDER BY nam_hoc DESC, ma_hoc_ky_he_thong
                """;
        return findOptions(sql);
    }

    public List<OptionItem> findLecturerOptions() {
        String sql = """
                SELECT ma_giang_vien, ma_so_giang_vien, ho_ten
                FROM dbo.giang_vien
                WHERE trang_thai = N'DANG_CONG_TAC'
                ORDER BY ho_ten
                """;
        return findOptions(sql);
    }

    public boolean existsByCode(String code, Integer excludedId) {
        String sql = """
                SELECT COUNT(1)
                FROM dbo.lop_hoc_phan
                WHERE ma_lop = ?
                  AND (? IS NULL OR ma_lop_hoc_phan <> ?)
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
            throw new RuntimeException("Lỗi kiểm tra mã lớp học phần: " + e.getMessage(), e);
        }
    }

    public void create(
            String code,
            String name,
            int subjectId,
            int semesterId,
            int lecturerId,
            Integer maxSize,
            String note,
            String status) {
        String sql = """
                INSERT INTO dbo.lop_hoc_phan (
                    ma_lop, ten_lop_hoc_phan, ma_mon_hoc, ma_hoc_ky,
                    ma_giang_vien, si_so_toi_da, ghi_chu, trang_thai
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, code, name, subjectId, semesterId, lecturerId, maxSize, note, status);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm lớp học phần: " + e.getMessage(), e);
        }
    }

    public void update(
            int id,
            String code,
            String name,
            int subjectId,
            int semesterId,
            int lecturerId,
            Integer maxSize,
            String note,
            String status) {
        String sql = """
                UPDATE dbo.lop_hoc_phan
                SET ma_lop = ?,
                    ten_lop_hoc_phan = ?,
                    ma_mon_hoc = ?,
                    ma_hoc_ky = ?,
                    ma_giang_vien = ?,
                    si_so_toi_da = ?,
                    ghi_chu = ?,
                    trang_thai = ?
                WHERE ma_lop_hoc_phan = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, code, name, subjectId, semesterId, lecturerId, maxSize, note, status);
            statement.setInt(9, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Không tìm thấy lớp học phần cần cập nhật.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật lớp học phần: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.lop_hoc_phan SET trang_thai = ? WHERE ma_lop_hoc_phan = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Không tìm thấy lớp học phần cần đổi trạng thái.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đổi trạng thái lớp học phần: " + e.getMessage(), e);
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

    private void fillStatement(
            PreparedStatement statement,
            String code,
            String name,
            int subjectId,
            int semesterId,
            int lecturerId,
            Integer maxSize,
            String note,
            String status) throws SQLException {
        statement.setString(1, code);
        statement.setString(2, name);
        statement.setInt(3, subjectId);
        statement.setInt(4, semesterId);
        statement.setInt(5, lecturerId);
        if (maxSize == null) {
            statement.setNull(6, java.sql.Types.INTEGER);
        } else {
            statement.setInt(6, maxSize);
        }
        statement.setString(7, note);
        statement.setString(8, status);
    }

    private CourseSectionSummary mapSection(ResultSet resultSet) throws SQLException {
        int maxSize = resultSet.getInt("si_so_toi_da");
        Integer nullableMaxSize = resultSet.wasNull() ? null : maxSize;

        return new CourseSectionSummary(
                resultSet.getInt("ma_lop_hoc_phan"),
                resultSet.getString("ma_lop"),
                resultSet.getString("ten_lop_hoc_phan"),
                resultSet.getInt("ma_mon_hoc"),
                resultSet.getString("ten_mon_hoc"),
                resultSet.getInt("ma_hoc_ky"),
                resultSet.getString("ten_hoc_ky"),
                resultSet.getString("nam_hoc"),
                resultSet.getInt("ma_giang_vien"),
                resultSet.getString("ten_giang_vien"),
                nullableMaxSize,
                resultSet.getString("ghi_chu"),
                resultSet.getString("trang_thai"));
    }
}
