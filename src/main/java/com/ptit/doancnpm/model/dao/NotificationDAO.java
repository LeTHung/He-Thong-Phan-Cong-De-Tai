package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.StudentNotification;
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
 * Truy xuất các sự kiện cần nhắc sinh viên: vừa được thêm vào lớp và cổng đăng
 * ký sắp đóng trong vòng hai ngày.
 */
public class NotificationDAO {

    public List<StudentNotification> findForStudent(int maTaiKhoan, LocalDateTime cutoff) {
        String sql = """
                WITH notifications AS (
                    SELECT
                        CONCAT(N'CLASS:', svl.ma_lop_hoc_phan, N':',
                               CONVERT(NVARCHAR(30), svl.thoi_diem_tham_gia, 126)) AS notification_id,
                        N'ADDED_TO_CLASS' AS notification_type,
                        lhp.ma_lop_hoc_phan,
                        lhp.ma_lop,
                        lhp.ten_lop_hoc_phan,
                        gv.ho_ten AS ten_giang_vien,
                        svl.thoi_diem_tham_gia AS event_time,
                        CAST(NULL AS DATETIME2(0)) AS registration_deadline
                    FROM dbo.sinh_vien_lop svl
                    JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                    JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                    JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                    WHERE sv.ma_tai_khoan = ?
                      AND svl.trang_thai = N'DANG_HOC'
                      AND svl.thoi_diem_tham_gia >= ?

                    UNION ALL

                    SELECT
                        CONCAT(N'DEADLINE:', ddk.ma_dot_dang_ky, N':',
                               CONVERT(NVARCHAR(30), ddk.thoi_gian_ket_thuc, 126)),
                        N'REGISTRATION_DEADLINE',
                        lhp.ma_lop_hoc_phan,
                        lhp.ma_lop,
                        lhp.ten_lop_hoc_phan,
                        gv.ho_ten,
                        CASE
                            WHEN svl.thoi_diem_tham_gia > ddk.thoi_gian_bat_dau
                             AND svl.thoi_diem_tham_gia > DATEADD(DAY, -2, ddk.thoi_gian_ket_thuc)
                                THEN svl.thoi_diem_tham_gia
                            WHEN ddk.thoi_gian_bat_dau > DATEADD(DAY, -2, ddk.thoi_gian_ket_thuc)
                                THEN ddk.thoi_gian_bat_dau
                            ELSE DATEADD(DAY, -2, ddk.thoi_gian_ket_thuc)
                        END,
                        ddk.thoi_gian_ket_thuc
                    FROM dbo.dot_dang_ky ddk
                    JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = ddk.ma_lop_hoc_phan
                    JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                    JOIN dbo.sinh_vien_lop svl
                      ON svl.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                     AND svl.trang_thai = N'DANG_HOC'
                    JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                    WHERE sv.ma_tai_khoan = ?
                      AND lhp.che_do_phan_cong = N'SINH_VIEN_TU_DANG_KY'
                      AND ddk.trang_thai = N'DANG_MO'
                      AND ddk.thoi_gian_bat_dau <= SYSDATETIME()
                      AND ddk.thoi_gian_ket_thuc > SYSDATETIME()
                      AND ddk.thoi_gian_ket_thuc <= DATEADD(DAY, 2, SYSDATETIME())
                      AND NOT EXISTS (
                          SELECT 1 FROM dbo.dang_ky_de_tai dk
                          WHERE dk.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                            AND dk.ma_sinh_vien = sv.ma_sinh_vien
                      )
                      AND EXISTS (
                          SELECT 1 FROM dbo.de_tai_lop dtl
                          WHERE dtl.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
                            AND dtl.trang_thai = N'DANG_MO'
                            AND dtl.so_luong_hien_tai < dtl.so_luong_toi_da
                      )
                )
                SELECT notification_id, notification_type, ma_lop_hoc_phan, ma_lop,
                       ten_lop_hoc_phan, ten_giang_vien, event_time, registration_deadline
                FROM notifications
                ORDER BY event_time DESC, notification_id DESC
                """;

        List<StudentNotification> notifications = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);
            statement.setTimestamp(2, Timestamp.valueOf(cutoff));
            statement.setInt(3, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp eventTime = resultSet.getTimestamp("event_time");
                    Timestamp deadline = resultSet.getTimestamp("registration_deadline");
                    notifications.add(new StudentNotification(
                            resultSet.getString("notification_id"),
                            StudentNotification.Type.valueOf(resultSet.getString("notification_type")),
                            resultSet.getInt("ma_lop_hoc_phan"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ten_lop_hoc_phan"),
                            resultSet.getString("ten_giang_vien"),
                            eventTime == null ? null : eventTime.toLocalDateTime(),
                            deadline == null ? null : deadline.toLocalDateTime()));
                }
            }

            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thông báo sinh viên: " + e.getMessage(), e);
        }
    }
}
