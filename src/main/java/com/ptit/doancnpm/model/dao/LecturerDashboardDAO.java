package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.dto.LecturerClassStudentRow;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerDashboardDAO {

    public List<LecturerClassStudentRow> findClassStudents(
            int maTaiKhoan, int maLopHocPhan) {
        String sql = """
                SELECT
                    sv.ma_sinh_vien,
                    sv.ma_so_sinh_vien,
                    sv.ho_ten,
                    sv.email,
                    sv.lop_sinh_hoat,
                    ndt.ma_de_tai_he_thong,
                    ndt.ten_de_tai,
                    dk.hinh_thuc_phan_cong
                FROM dbo.lop_hoc_phan lhp
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                JOIN dbo.sinh_vien_lop svl
                  ON svl.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                 AND svl.trang_thai = N'DANG_HOC'
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                LEFT JOIN dbo.dang_ky_de_tai dk
                  ON dk.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                 AND dk.ma_sinh_vien = sv.ma_sinh_vien
                LEFT JOIN dbo.de_tai_lop dtl ON dtl.ma_de_tai_lop = dk.ma_de_tai_lop
                LEFT JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                WHERE gv.ma_tai_khoan = ?
                  AND lhp.ma_lop_hoc_phan = ?
                ORDER BY sv.ma_so_sinh_vien
                """;

        List<LecturerClassStudentRow> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);
            statement.setInt(2, maLopHocPhan);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(new LecturerClassStudentRow(
                            resultSet.getInt("ma_sinh_vien"),
                            resultSet.getString("ma_so_sinh_vien"),
                            resultSet.getString("ho_ten"),
                            resultSet.getString("email"),
                            resultSet.getString("lop_sinh_hoat"),
                            resultSet.getString("ma_de_tai_he_thong"),
                            resultSet.getString("ten_de_tai"),
                            resultSet.getString("hinh_thuc_phan_cong")));
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải sinh viên của lớp: " + e.getMessage(), e);
        }
    }

    public String findLecturerNameByAccountId(int maTaiKhoan) {
        String sql = "SELECT ho_ten FROM dbo.giang_vien WHERE ma_tai_khoan = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString("ho_ten") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thông tin giảng viên: " + e.getMessage(), e);
        }
    }

    public List<LecturerCourseSectionSummary> findCourseSectionsByAccountId(int maTaiKhoan) {
        String sql = """
                SELECT
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan,
                    mh.ten_mon_hoc,
                    hk.ten_hoc_ky,
                    hk.nam_hoc,
                    ISNULL(lhp.si_so_toi_da, 0) AS si_so_toi_da,
                    lhp.trang_thai,
                    lhp.che_do_phan_cong,
                    COUNT(DISTINCT svl.ma_sinh_vien) AS tong_so_sinh_vien,
                    COUNT(DISTINCT dtl.ma_de_tai_lop) AS tong_so_de_tai,
                    (SELECT TOP 1
                         CASE
                             WHEN ddk.trang_thai = N'DANG_MO'
                                  AND SYSDATETIME() >= ddk.thoi_gian_bat_dau
                                  AND SYSDATETIME() <= ddk.thoi_gian_ket_thuc
                             THEN N'DANG_MO'
                             WHEN ddk.trang_thai = N'DANG_MO'
                                  AND SYSDATETIME() < ddk.thoi_gian_bat_dau
                             THEN N'CHO_MO'
                             WHEN ddk.trang_thai = N'DA_DONG'
                             THEN N'DA_DONG'
                             ELSE N'CHUA_MO'
                         END
                     FROM dbo.dot_dang_ky ddk
                     WHERE ddk.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                     ORDER BY ddk.thoi_diem_tao DESC
                    ) AS trang_thai_dot_dk
                FROM dbo.lop_hoc_phan lhp
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                JOIN dbo.mon_hoc mh ON mh.ma_mon_hoc = lhp.ma_mon_hoc
                JOIN dbo.hoc_ky hk ON hk.ma_hoc_ky = lhp.ma_hoc_ky
                LEFT JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                    AND svl.trang_thai = N'DANG_HOC'
                LEFT JOIN dbo.de_tai_lop dtl
                    ON dtl.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                WHERE gv.ma_tai_khoan = ?
                GROUP BY
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan,
                    mh.ten_mon_hoc,
                    hk.ten_hoc_ky,
                    hk.nam_hoc,
                    lhp.si_so_toi_da,
                    lhp.trang_thai,
                    lhp.che_do_phan_cong
                ORDER BY hk.nam_hoc DESC, hk.ten_hoc_ky DESC, lhp.ma_lop
                """;

        List<LecturerCourseSectionSummary> sections = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    sections.add(new LecturerCourseSectionSummary(
                            resultSet.getInt("ma_lop_hoc_phan"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ten_lop_hoc_phan"),
                            resultSet.getString("ten_mon_hoc"),
                            resultSet.getString("ten_hoc_ky"),
                            resultSet.getString("nam_hoc"),
                            resultSet.getInt("si_so_toi_da"),
                            resultSet.getString("trang_thai"),
                            resultSet.getString("che_do_phan_cong"),
                            resultSet.getInt("tong_so_sinh_vien"),
                            resultSet.getInt("tong_so_de_tai"),
                            resultSet.getString("trang_thai_dot_dk")));
                }
            }

            return sections;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách lớp học phần của giảng viên: " + e.getMessage(), e);
        }
    }

    public int getTotalRegisteredStudents(int maTaiKhoan) {
        String sql = """
                SELECT COUNT(*)
                FROM dbo.dang_ky_de_tai dk
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dk.ma_lop_hoc_phan
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                WHERE gv.ma_tai_khoan = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maTaiKhoan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thống kê sinh viên đã đăng ký: " + e.getMessage(), e);
        }
    }

    public String getRegistrationGateStatus(int maTaiKhoan) {
        String sql = """
                SELECT TOP 1
                    CASE
                        WHEN ddk.trang_thai = N'DANG_MO'
                             AND SYSDATETIME() BETWEEN ddk.thoi_gian_bat_dau AND ddk.thoi_gian_ket_thuc
                        THEN N'DANG_MO'
                        WHEN ddk.trang_thai = N'DANG_MO'
                             AND SYSDATETIME() < ddk.thoi_gian_bat_dau
                        THEN N'CHO_MO'
                        ELSE N'DA_DONG'
                    END AS status
                FROM dbo.dot_dang_ky ddk
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = ddk.ma_lop_hoc_phan
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                WHERE gv.ma_tai_khoan = ?
                ORDER BY CASE
                    WHEN ddk.trang_thai = N'DANG_MO'
                         AND SYSDATETIME() BETWEEN ddk.thoi_gian_bat_dau AND ddk.thoi_gian_ket_thuc
                    THEN 0
                    WHEN ddk.trang_thai = N'DANG_MO'
                         AND SYSDATETIME() < ddk.thoi_gian_bat_dau
                    THEN 1
                    ELSE 2
                END
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maTaiKhoan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("status") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trạng thái cổng đăng ký: " + e.getMessage(), e);
        }
    }
}
