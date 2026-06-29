package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.RegistrationResultRow;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho chức năng chốt danh sách / báo cáo cuối kỳ.
 */
public class FinalReportDAO {

    /**
     * Chốt danh sách: tự động phân công sinh viên chưa có đề tài vào chỗ trống,
     * sau đó đóng đợt đăng ký và khóa lớp học phần.
     *
     * @return số sinh viên được hệ thống tự động phân công
     */
    public int finalizeRegistration(int maGiangVien, int maLopHocPhan) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int soTuDong;
                try (CallableStatement stmt = conn.prepareCall(
                        "{call dbo.sp_phan_cong_tu_dong(?, ?, ?)}")) {
                    stmt.setInt(1, maGiangVien);
                    stmt.setInt(2, maLopHocPhan);
                    stmt.registerOutParameter(3, Types.INTEGER);
                    stmt.execute();
                    soTuDong = stmt.getInt(3);
                }

                int unassignedStudents = countUnassignedStudents(conn, maLopHocPhan);
                if (unassignedStudents > 0) {
                    throw new IllegalStateException(
                            "Còn " + unassignedStudents + " sinh viên chưa có đề tài. "
                                    + "Hãy tăng số lượng đề tài/chỗ trống trước khi chốt.");
                }

                try (CallableStatement stmt = conn.prepareCall(
                        "{call dbo.sp_chot_danh_sach(?, ?)}")) {
                    stmt.setInt(1, maGiangVien);
                    stmt.setInt(2, maLopHocPhan);
                    stmt.execute();
                }

                conn.commit();
                return soTuDong;
            } catch (SQLException | RuntimeException exception) {
                DatabaseConnection.rollbackQuietly(conn);
                throw exception;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi chốt danh sách: " + e.getMessage(), e);
        }
    }

    private int countUnassignedStudents(Connection connection, int courseSectionId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM dbo.sinh_vien_lop svl
                WHERE svl.ma_lop_hoc_phan = ?
                  AND svl.trang_thai = N'DANG_HOC'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM dbo.dang_ky_de_tai dk
                      WHERE dk.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                        AND dk.ma_sinh_vien = svl.ma_sinh_vien
                  )
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    /** Lấy báo cáo kết quả cuối (dùng view vw_bao_cao_nhom_de_tai) */
    public List<RegistrationResultRow> getFinalReport(int maLopHocPhan) {
        String sql = """
                SELECT ma_so_sinh_vien, ten_sinh_vien,
                       lop_sinh_hoat, ma_de_tai_he_thong, ten_de_tai,
                       hinh_thuc_phan_cong, thoi_diem_dang_ky
                FROM dbo.vw_bao_cao_nhom_de_tai
                WHERE ma_lop_hoc_phan = ?
                ORDER BY ma_de_tai_he_thong, ma_so_sinh_vien
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
                            rs.getString("ten_sinh_vien"),
                            rs.getString("lop_sinh_hoat"),
                            rs.getString("ma_de_tai_he_thong"),
                            rs.getString("ten_de_tai"),
                            rs.getString("hinh_thuc_phan_cong"),
                            ts == null ? null : ts.toLocalDateTime()));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải báo cáo cuối kỳ: " + e.getMessage(), e);
        }
    }

    public boolean hasPeriodForLop(int maLopHocPhan) {
        String sql = "SELECT COUNT(1) FROM dbo.dot_dang_ky WHERE ma_lop_hoc_phan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra đợt đăng ký: " + e.getMessage(), e);
        }
    }

    public boolean hasRegistrationStarted(int maLopHocPhan) {
        String sql = """
                SELECT COUNT(1)
                FROM dbo.dot_dang_ky
                WHERE ma_lop_hoc_phan = ?
                  AND SYSDATETIME() >= thoi_gian_bat_dau
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra thời gian đăng ký: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra lớp đã chốt danh sách chưa (trang_thai = DA_DONG) */
    public boolean isFinalized(int maLopHocPhan) {
        String sql = "SELECT trang_thai FROM dbo.lop_hoc_phan WHERE ma_lop_hoc_phan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "DA_DONG".equals(rs.getString("trang_thai"));
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra trạng thái lớp: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra đợt đăng ký còn đang mở không */
    public boolean isRegistrationOpen(int maLopHocPhan) {
        String sql = """
                SELECT COUNT(1) FROM dbo.dot_dang_ky
                WHERE ma_lop_hoc_phan = ?
                  AND trang_thai = N'DANG_MO'
                  AND SYSDATETIME() >= thoi_gian_bat_dau
                  AND SYSDATETIME() <= thoi_gian_ket_thuc
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra đợt đăng ký: " + e.getMessage(), e);
        }
    }

    /** Thống kê nhanh: tổng SV, SV đã có đề tài, tổng đề tài */
    public int[] getStats(int maLopHocPhan) {
        String sql = """
                SELECT tong_so_sinh_vien, so_sinh_vien_da_co_de_tai, tong_so_de_tai
                FROM dbo.vw_thong_ke_lop_hoc_phan
                WHERE ma_lop_hoc_phan = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt("tong_so_sinh_vien"),
                            rs.getInt("so_sinh_vien_da_co_de_tai"),
                            rs.getInt("tong_so_de_tai")};
                }
                return new int[]{0, 0, 0};
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thống kê lớp: " + e.getMessage(), e);
        }
    }
}
