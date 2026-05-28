package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LecturerDashboardDAO {

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
                    COUNT(DISTINCT svl.ma_sinh_vien) AS tong_so_sinh_vien,
                    COUNT(DISTINCT dtl.ma_de_tai_lop) AS tong_so_de_tai
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
                    lhp.trang_thai
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
                            resultSet.getInt("tong_so_sinh_vien"),
                            resultSet.getInt("tong_so_de_tai")));
                }
            }

            return sections;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách lớp học phần của giảng viên: " + e.getMessage(), e);
        }
    }
}
