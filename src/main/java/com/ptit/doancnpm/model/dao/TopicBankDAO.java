package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.TopicBankItem;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho ngân hàng đề tài (bảng ngan_hang_de_tai).
 * Chỉ làm việc với đề tài của chính giảng viên đang đăng nhập (lọc theo ma_giang_vien_tao).
 */
public class TopicBankDAO {

    public List<TopicBankItem> findByGiangVien(int maGiangVien) {
        String sql = """
                SELECT ma_de_tai, ma_de_tai_he_thong, ten_de_tai, mo_ta, yeu_cau,
                       so_luong_mac_dinh, trang_thai, thoi_diem_tao
                FROM dbo.ngan_hang_de_tai
                WHERE ma_giang_vien_tao = ?
                  AND trang_thai = N'DANG_SU_DUNG'
                ORDER BY thoi_diem_tao DESC
                """;
        List<TopicBankItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maGiangVien);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải ngân hàng đề tài: " + e.getMessage(), e);
        }
    }

    public List<TopicBankItem> searchByName(int maGiangVien, String keyword) {
        String sql = """
                SELECT ma_de_tai, ma_de_tai_he_thong, ten_de_tai, mo_ta, yeu_cau,
                       so_luong_mac_dinh, trang_thai, thoi_diem_tao
                FROM dbo.ngan_hang_de_tai
                WHERE ma_giang_vien_tao = ?
                  AND trang_thai = N'DANG_SU_DUNG'
                  AND (ten_de_tai LIKE ? OR ma_de_tai_he_thong LIKE ?)
                ORDER BY thoi_diem_tao DESC
                """;
        String pattern = "%" + keyword + "%";
        List<TopicBankItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maGiangVien);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tìm kiếm đề tài: " + e.getMessage(), e);
        }
    }

    public void insert(int maGiangVien, String maDeTaiHeThong, String tenDeTai,
                       String moTa, String yeuCau, int soLuongMacDinh) {
        String sql = """
                INSERT INTO dbo.ngan_hang_de_tai
                    (ma_de_tai_he_thong, ten_de_tai, mo_ta, yeu_cau, so_luong_mac_dinh, ma_giang_vien_tao, trang_thai)
                VALUES (?, ?, ?, ?, ?, ?, N'DANG_SU_DUNG')
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDeTaiHeThong);
            stmt.setString(2, tenDeTai);
            stmt.setString(3, moTa);
            stmt.setString(4, yeuCau);
            stmt.setInt(5, soLuongMacDinh);
            stmt.setInt(6, maGiangVien);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm đề tài: " + e.getMessage(), e);
        }
    }

    public void update(int maDeTai, int maGiangVien, String maDeTaiHeThong, String tenDeTai,
                       String moTa, String yeuCau, int soLuongMacDinh) {
        String sql = """
                UPDATE dbo.ngan_hang_de_tai
                SET ma_de_tai_he_thong = ?, ten_de_tai = ?, mo_ta = ?, yeu_cau = ?,
                    so_luong_mac_dinh = ?, thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_de_tai = ? AND ma_giang_vien_tao = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDeTaiHeThong);
            stmt.setString(2, tenDeTai);
            stmt.setString(3, moTa);
            stmt.setString(4, yeuCau);
            stmt.setInt(5, soLuongMacDinh);
            stmt.setInt(6, maDeTai);
            stmt.setInt(7, maGiangVien);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy đề tài hoặc bạn không có quyền sửa đề tài này.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi cập nhật đề tài: " + e.getMessage(), e);
        }
    }

    /** Xóa mềm đề tài thuộc chính giảng viên đang đăng nhập. */
    public void softDelete(int maDeTai, int maGiangVien) {
        String sql = """
                UPDATE dbo.ngan_hang_de_tai
                SET trang_thai = N'NGUNG_SU_DUNG', thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_de_tai = ? AND ma_giang_vien_tao = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDeTai);
            stmt.setInt(2, maGiangVien);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Không tìm thấy đề tài hoặc bạn không có quyền xóa đề tài này.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi xóa đề tài: " + e.getMessage(), e);
        }
    }

    public boolean isAssignedToClass(int maDeTai) {
        String sql = "SELECT COUNT(1) FROM dbo.de_tai_lop WHERE ma_de_tai = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maDeTai);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra đề tài: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra mã đề tài hệ thống đã tồn tại chưa (khi thêm mới) */
    public boolean existsByCode(String maDeTaiHeThong, Integer excludeId) {
        String sql = """
                SELECT COUNT(1) FROM dbo.ngan_hang_de_tai
                WHERE ma_de_tai_he_thong = ?
                  AND (? IS NULL OR ma_de_tai <> ?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maDeTaiHeThong);
            if (excludeId == null) {
                stmt.setNull(2, Types.INTEGER);
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(2, excludeId);
                stmt.setInt(3, excludeId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra mã đề tài: " + e.getMessage(), e);
        }
    }

    /** Lấy maGiangVien từ maTaiKhoan */
    public int findMaGiangVienByTaiKhoan(int maTaiKhoan) {
        String sql = "SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_tai_khoan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maTaiKhoan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ma_giang_vien");
                }
                throw new RuntimeException("Không tìm thấy giảng viên.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tra cứu giảng viên: " + e.getMessage(), e);
        }
    }

    private TopicBankItem mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("thoi_diem_tao");
        return new TopicBankItem(
                rs.getInt("ma_de_tai"),
                rs.getString("ma_de_tai_he_thong"),
                rs.getString("ten_de_tai"),
                rs.getString("mo_ta"),
                rs.getString("yeu_cau"),
                rs.getInt("so_luong_mac_dinh"),
                rs.getString("trang_thai"),
                ts == null ? null : ts.toLocalDateTime());
    }
}
