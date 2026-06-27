package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho việc gán đề tài vào lớp học phần (bảng de_tai_lop).
 */
public class AssignTopicDAO {

    /** Danh sách đề tài đã gán vào một lớp học phần */
    public List<AssignedTopicRow> findByLop(int maLopHocPhan) {
        String sql = """
                SELECT dtl.ma_de_tai_lop,
                       ndt.ma_de_tai_he_thong,
                       ndt.ten_de_tai,
                       dtl.so_luong_toi_da,
                       dtl.so_luong_hien_tai,
                       dtl.che_do_phan_cong,
                       dtl.trang_thai
                FROM dbo.de_tai_lop dtl
                JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                WHERE dtl.ma_lop_hoc_phan = ?
                ORDER BY dtl.thoi_diem_tao
                """;
        List<AssignedTopicRow> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new AssignedTopicRow(
                            rs.getInt("ma_de_tai_lop"),
                            rs.getString("ma_de_tai_he_thong"),
                            rs.getString("ten_de_tai"),
                            rs.getInt("so_luong_toi_da"),
                            rs.getInt("so_luong_hien_tai"),
                            rs.getString("che_do_phan_cong"),
                            rs.getString("trang_thai")));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách đề tài lớp: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra đề tài đã được gán vào lớp hay chưa */
    public boolean isAlreadyAssigned(int maLopHocPhan, int maDeTai) {
        String sql = """
                SELECT COUNT(1) FROM dbo.de_tai_lop
                WHERE ma_lop_hoc_phan = ? AND ma_de_tai = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            stmt.setInt(2, maDeTai);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra đề tài đã gán: " + e.getMessage(), e);
        }
    }

    /** Gán đề tài vào lớp */
    public void assignTopic(int maLopHocPhan, int maDeTai, int soLuongToiDa, String cheDoPhancong) {
        String sql = """
                INSERT INTO dbo.de_tai_lop
                    (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong, trang_thai)
                VALUES (?, ?, ?, ?, N'DANG_MO')
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            stmt.setInt(2, maDeTai);
            stmt.setInt(3, soLuongToiDa);
            stmt.setString(4, cheDoPhancong);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi gán đề tài vào lớp: " + e.getMessage(), e);
        }
    }

    /** Xóa gán đề tài khỏi lớp (chỉ được khi chưa có sinh viên đăng ký) */
    public void removeAssignment(int maDeTaiLop) {
        String sql = "DELETE FROM dbo.de_tai_lop WHERE ma_de_tai_lop = ? AND so_luong_hien_tai = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDeTaiLop);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Không thể xóa: đề tài đã có sinh viên đăng ký hoặc không tồn tại.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa gán đề tài: " + e.getMessage(), e);
        }
    }
}
