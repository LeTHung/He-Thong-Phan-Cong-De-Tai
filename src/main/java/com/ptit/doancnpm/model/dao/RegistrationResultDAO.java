package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.model.dto.UnregisteredStudentRow;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho kết quả đăng ký đề tài trong một lớp học phần.
 */
public class RegistrationResultDAO {

    /** Danh sách sinh viên đã đăng ký đề tài trong lớp */
    public List<RegistrationResultRow> findRegisteredByLop(int maLopHocPhan) {
        String sql = """
                SELECT sv.ma_so_sinh_vien, sv.ho_ten, sv.lop_sinh_hoat,
                       ndt.ma_de_tai_he_thong, ndt.ten_de_tai,
                       dk.hinh_thuc_phan_cong, dk.thoi_diem_dang_ky
                FROM dbo.dang_ky_de_tai dk
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = dk.ma_sinh_vien
                JOIN dbo.de_tai_lop dtl ON dtl.ma_de_tai_lop = dk.ma_de_tai_lop
                JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                WHERE dk.ma_lop_hoc_phan = ?
                ORDER BY ndt.ma_de_tai_he_thong, sv.ma_so_sinh_vien
                """;
        List<RegistrationResultRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("thoi_diem_dang_ky");
                    list.add(new RegistrationResultRow(
                            rs.getString("ma_so_sinh_vien"),
                            rs.getString("ho_ten"),
                            rs.getString("lop_sinh_hoat"),
                            rs.getString("ma_de_tai_he_thong"),
                            rs.getString("ten_de_tai"),
                            rs.getString("hinh_thuc_phan_cong"),
                            ts == null ? null : ts.toLocalDateTime()));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải kết quả đăng ký: " + e.getMessage(), e);
        }
    }

    /** Danh sách sinh viên chưa có đề tài (dùng view vw_sinh_vien_chua_co_de_tai) */
    public List<UnregisteredStudentRow> findUnregisteredByLop(int maLopHocPhan) {
        String sql = """
                SELECT ma_so_sinh_vien, ho_ten, lop_sinh_hoat, email
                FROM dbo.vw_sinh_vien_chua_co_de_tai
                WHERE ma_lop_hoc_phan = ?
                ORDER BY ma_so_sinh_vien
                """;
        List<UnregisteredStudentRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new UnregisteredStudentRow(
                            rs.getString("ma_so_sinh_vien"),
                            rs.getString("ho_ten"),
                            rs.getString("lop_sinh_hoat"),
                            rs.getString("email")));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải sinh viên chưa đăng ký: " + e.getMessage(), e);
        }
    }
}
