package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.AssignedTopicRow;
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
                SELECT ma_sinh_vien, ma_so_sinh_vien, ho_ten, lop_sinh_hoat, email
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
                            rs.getInt("ma_sinh_vien"),
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

    public List<AssignedTopicRow> findAssignableTopicsByLop(int maLopHocPhan) {
        String sql = """
                SELECT dtl.ma_de_tai_lop,
                       ndt.ma_de_tai_he_thong,
                       ndt.ten_de_tai,
                       dtl.so_luong_toi_da,
                       dtl.so_luong_hien_tai,
                       lhp.che_do_phan_cong,
                       dtl.trang_thai
                FROM dbo.de_tai_lop dtl
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
                JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                WHERE dtl.ma_lop_hoc_phan = ?
                  AND dtl.trang_thai <> N'DA_DONG'
                  AND dtl.so_luong_hien_tai < dtl.so_luong_toi_da
                ORDER BY
                    dtl.so_luong_hien_tai,
                    ndt.ma_de_tai_he_thong
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
            throw new RuntimeException("Lỗi tải đề tài còn chỗ để phân công: " + e.getMessage(), e);
        }
    }

    public void assignStudentManually(int maGiangVien, int maSinhVien, int maDeTaiLop, String ghiChu) {
        String sql = "{call dbo.sp_phan_cong_thu_cong(?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, maGiangVien);
            stmt.setInt(2, maSinhVien);
            stmt.setInt(3, maDeTaiLop);
            if (ghiChu == null || ghiChu.isBlank()) {
                stmt.setNull(4, Types.NVARCHAR);
            } else {
                stmt.setString(4, ghiChu);
            }
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi phân công thủ công: " + e.getMessage(), e);
        }
    }

    public int autoAssignUnregisteredStudents(int maGiangVien, int maLopHocPhan) {
        String sql = "{call dbo.sp_phan_cong_tu_dong(?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, maGiangVien);
            stmt.setInt(2, maLopHocPhan);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(3);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi phân công tự động: " + e.getMessage(), e);
        }
    }
}
