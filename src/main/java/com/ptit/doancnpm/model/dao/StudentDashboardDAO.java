package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.StudentCourseSection;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDashboardDAO {

    public Optional<StudentInfo> findStudentInfoByAccountId(int maTaiKhoan) {
        String sql = """
                SELECT TOP 1
                    sv.ma_sinh_vien,
                    sv.ma_so_sinh_vien,
                    sv.ho_ten,
                    sv.lop_sinh_hoat,
                    sv.email,
                    sv.khoa_hoc,
                    sv.nganh,
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan
                FROM dbo.sinh_vien sv
                LEFT JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_sinh_vien = sv.ma_sinh_vien
                    AND svl.trang_thai = N'DANG_HOC'
                LEFT JOIN dbo.lop_hoc_phan lhp
                    ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                WHERE sv.ma_tai_khoan = ?
                ORDER BY lhp.thoi_diem_tao DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                int maLopHocPhan = resultSet.getInt("ma_lop_hoc_phan");
                Integer nullableMaLopHocPhan = resultSet.wasNull() ? null : maLopHocPhan;

                return Optional.of(new StudentInfo(
                        resultSet.getInt("ma_sinh_vien"),
                        resultSet.getString("ma_so_sinh_vien"),
                        resultSet.getString("ho_ten"),
                        resultSet.getString("lop_sinh_hoat"),
                        resultSet.getString("email"),
                        resultSet.getString("khoa_hoc"),
                        resultSet.getString("nganh"),
                        nullableMaLopHocPhan,
                        resultSet.getString("ma_lop"),
                        resultSet.getString("ten_lop_hoc_phan")));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thông tin sinh viên: " + e.getMessage(), e);
        }
    }

    /**
     * Danh sách các lớp học phần mà sinh viên đang theo học (trạng thái DANG_HOC),
     * lớp mới nhất xếp trước. Dùng cho bộ chọn lớp ở trang chủ.
     */
    public List<StudentCourseSection> findCourseSectionsByAccountId(int maTaiKhoan) {
        String sql = """
                SELECT
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan,
                    mh.ten_mon_hoc
                FROM dbo.sinh_vien sv
                JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_sinh_vien = sv.ma_sinh_vien
                    AND svl.trang_thai = N'DANG_HOC'
                JOIN dbo.lop_hoc_phan lhp
                    ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                JOIN dbo.mon_hoc mh ON mh.ma_mon_hoc = lhp.ma_mon_hoc
                WHERE sv.ma_tai_khoan = ?
                ORDER BY lhp.thoi_diem_tao DESC, lhp.ma_lop
                """;

        List<StudentCourseSection> sections = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sections.add(new StudentCourseSection(
                            resultSet.getInt("ma_lop_hoc_phan"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ten_lop_hoc_phan"),
                            resultSet.getString("ten_mon_hoc")));
                }
            }

            return sections;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách lớp học phần của sinh viên: " + e.getMessage(), e);
        }
    }

    public List<StudentTopicSummary> findTopicsByAccountId(int maTaiKhoan) {
        String sql = """
                SELECT
                    v.ma_de_tai_lop,
                    v.ma_lop,
                    v.ma_de_tai_he_thong,
                    v.ten_de_tai,
                    v.so_luong_toi_da,
                    v.so_luong_hien_tai,
                    v.so_cho_con_lai,
                    v.trang_thai,
                    v.che_do_phan_cong,
                    gv.ho_ten AS ten_giang_vien,
                    CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM dbo.dang_ky_de_tai dk
                            WHERE dk.ma_de_tai_lop = v.ma_de_tai_lop
                              AND dk.ma_sinh_vien = sv.ma_sinh_vien
                        ) THEN 1
                        ELSE 0
                    END AS da_dang_ky
                FROM dbo.vw_de_tai_con_cho v
                JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_lop_hoc_phan = v.ma_lop_hoc_phan
                    AND svl.trang_thai = N'DANG_HOC'
                JOIN dbo.sinh_vien sv
                    ON sv.ma_sinh_vien = svl.ma_sinh_vien
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = v.ma_lop_hoc_phan
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                WHERE sv.ma_tai_khoan = ?
                ORDER BY v.ma_lop, v.ma_de_tai_he_thong
                """;

        List<StudentTopicSummary> topics = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    topics.add(new StudentTopicSummary(
                            resultSet.getInt("ma_de_tai_lop"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ma_de_tai_he_thong"),
                            resultSet.getString("ten_de_tai"),
                            resultSet.getInt("so_luong_toi_da"),
                            resultSet.getInt("so_luong_hien_tai"),
                            resultSet.getInt("so_cho_con_lai"),
                            resultSet.getString("trang_thai"),
                            resultSet.getString("che_do_phan_cong"),
                            resultSet.getString("ten_giang_vien"),
                            resultSet.getInt("da_dang_ky") == 1));
                }
            }

            return topics;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách đề tài của sinh viên: " + e.getMessage(), e);
        }
    }
}
