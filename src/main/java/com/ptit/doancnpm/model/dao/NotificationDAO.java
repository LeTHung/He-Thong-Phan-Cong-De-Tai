package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.TopicNotification;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Truy xuất các đề tài mới được thêm vào (các) lớp học phần mà sinh viên đang
 * theo học, phục vụ chức năng thông báo "đề tài mới". Dựa vào cột
 * {@code de_tai_lop.thoi_diem_tao} nên không cần thay đổi cấu trúc database.
 */
public class NotificationDAO {

    public List<TopicNotification> findRecentTopicsForStudent(int maTaiKhoan, LocalDateTime cutoff) {
        String sql = """
                SELECT
                    dtl.ma_de_tai_lop,
                    lhp.ma_lop,
                    ndt.ma_de_tai_he_thong,
                    ndt.ten_de_tai,
                    lhp.ten_lop_hoc_phan,
                    gv.ho_ten AS ten_giang_vien,
                    dtl.thoi_diem_tao
                FROM dbo.de_tai_lop dtl
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
                JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
                    AND svl.trang_thai = N'DANG_HOC'
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                WHERE sv.ma_tai_khoan = ?
                  AND dtl.thoi_diem_tao >= ?
                ORDER BY dtl.thoi_diem_tao DESC, dtl.ma_de_tai_lop DESC
                """;

        List<TopicNotification> notifications = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);
            statement.setTimestamp(2, Timestamp.valueOf(cutoff));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp thoiDiem = resultSet.getTimestamp("thoi_diem_tao");
                    notifications.add(new TopicNotification(
                            resultSet.getInt("ma_de_tai_lop"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ma_de_tai_he_thong"),
                            resultSet.getString("ten_de_tai"),
                            resultSet.getString("ten_lop_hoc_phan"),
                            resultSet.getString("ten_giang_vien"),
                            thoiDiem == null ? null : thoiDiem.toLocalDateTime()));
                }
            }

            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thông báo đề tài mới: " + e.getMessage(), e);
        }
    }
}
