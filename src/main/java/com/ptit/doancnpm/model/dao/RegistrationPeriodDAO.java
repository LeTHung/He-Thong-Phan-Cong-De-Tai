package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DAO cho đợt đăng ký (bảng dot_dang_ky) — dùng cho giảng viên mở/xem cổng đăng ký.
 */
public class RegistrationPeriodDAO {

    /**
     * Mở hoặc cập nhật đợt đăng ký. Gọi stored procedure sp_mo_cong_dang_ky.
     */
    public void openPeriod(int maLopHocPhan, int maGiangVien,
                           LocalDateTime batDau, LocalDateTime ketThuc, String ghiChu) {
        String sql = "{call dbo.sp_mo_cong_dang_ky(?, ?, ?, ?, ?)}";
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, maLopHocPhan);
            stmt.setInt(2, maGiangVien);
            stmt.setTimestamp(3, Timestamp.valueOf(batDau));
            stmt.setTimestamp(4, Timestamp.valueOf(ketThuc));
            if (ghiChu == null || ghiChu.isBlank()) {
                stmt.setNull(5, Types.NVARCHAR);
            } else {
                stmt.setString(5, ghiChu);
            }
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** Đóng đợt đăng ký (chuyển trạng thái sang DA_DONG) */
    public void closePeriod(int maLopHocPhan, int maGiangVien) {
        String sql = """
                UPDATE ddk
                SET ddk.trang_thai = N'DA_DONG', ddk.thoi_diem_cap_nhat = SYSDATETIME()
                FROM dbo.dot_dang_ky ddk
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = ddk.ma_lop_hoc_phan
                WHERE ddk.ma_lop_hoc_phan = ?
                  AND lhp.ma_giang_vien = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            stmt.setInt(2, maGiangVien);
            if (stmt.executeUpdate() == 0) {
                throw new RuntimeException("Không tìm thấy đợt đăng ký hoặc bạn không phụ trách lớp này.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi đóng đợt đăng ký: " + e.getMessage(), e);
        }
    }

    /** Lấy thông tin đợt đăng ký hiện tại của lớp học phần */
    public Optional<RegistrationPeriodInfo> findCurrentByLop(int maLopHocPhan) {
        String sql = """
                SELECT thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai,
                       CASE
                           WHEN trang_thai = N'DANG_MO'
                                AND SYSDATETIME() >= thoi_gian_bat_dau
                                AND SYSDATETIME() <= thoi_gian_ket_thuc
                           THEN 1 ELSE 0
                       END AS dang_mo
                FROM dbo.dot_dang_ky
                WHERE ma_lop_hoc_phan = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maLopHocPhan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Timestamp batDau = rs.getTimestamp("thoi_gian_bat_dau");
                Timestamp ketThuc = rs.getTimestamp("thoi_gian_ket_thuc");
                return Optional.of(new RegistrationPeriodInfo(
                        batDau == null ? null : batDau.toLocalDateTime(),
                        ketThuc == null ? null : ketThuc.toLocalDateTime(),
                        rs.getString("trang_thai"),
                        rs.getInt("dang_mo") == 1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải đợt đăng ký: " + e.getMessage(), e);
        }
    }

    /** Kiểm tra đợt đăng ký đang mở */
    public boolean isCurrentlyOpen(int maLopHocPhan) {
        return findCurrentByLop(maLopHocPhan)
                .map(RegistrationPeriodInfo::dangMo)
                .orElse(false);
    }
}
