package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.RegisteredTopic;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.StudentTopicSummary;
import com.ptit.doancnpm.model.dto.TopicDetail;
import com.ptit.doancnpm.util.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Truy xuất dữ liệu cho chức năng đăng ký đề tài của sinh viên.
 * Việc đăng ký / hủy đăng ký gọi trực tiếp stored procedure để giữ
 * đúng ràng buộc nghiệp vụ đã định nghĩa trong database.
 */
public class TopicRegistrationDAO {

    /**
     * Lấy thông tin sinh viên và lớp học phần đang học theo mã tài khoản đăng nhập.
     */
    public Optional<StudentInfo> findStudentInfo(int maTaiKhoan) {
        String sql = """
                SELECT TOP 1
                    sv.ma_sinh_vien,
                    sv.ma_so_sinh_vien,
                    sv.ho_ten,
                    sv.lop_sinh_hoat,
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan
                FROM dbo.sinh_vien sv
                LEFT JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_sinh_vien = sv.ma_sinh_vien
                    AND svl.trang_thai = N'DANG_HOC'
                LEFT JOIN dbo.lop_hoc_phan lhp
                    ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                WHERE sv.ma_tai_khoan = ?
                ORDER BY lhp.thoi_diem_tao DESC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                int maLopHocPhan = resultSet.getInt("ma_lop_hoc_phan");
                Integer nullableMaLopHocPhan = resultSet.wasNull() ? null : maLopHocPhan;

                return Optional.of(new StudentInfo(
                        resultSet.getInt("ma_sinh_vien"),
                        resultSet.getString("ma_so_sinh_vien"),
                        resultSet.getString("ho_ten"),
                        resultSet.getString("lop_sinh_hoat"),
                        nullableMaLopHocPhan,
                        resultSet.getString("ma_lop"),
                        resultSet.getString("ten_lop_hoc_phan")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải thông tin sinh viên: " + e.getMessage(), e);
        }
    }

    /**
     * Danh sách đề tài trong (các) lớp học phần mà sinh viên đang theo học,
     * kèm cờ đánh dấu đề tài sinh viên đã đăng ký.
     */
    public List<StudentTopicSummary> findRegistrableTopics(int maTaiKhoan) {
        String sql = """
                SELECT
                    v.ma_de_tai_lop,
                    v.ma_lop,
                    v.ma_de_tai_he_thong,
                    v.ten_de_tai,
                    v.so_luong_toi_da,
                    v.so_luong_hien_tai,
                    v.so_cho_con_lai,
                    v.trang_thai,
                    v.che_do_phan_cong,
                    CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM dbo.dang_ky_de_tai dk
                            WHERE dk.ma_de_tai_lop = v.ma_de_tai_lop
                              AND dk.ma_sinh_vien = sv.ma_sinh_vien
                        ) THEN 1
                        ELSE 0
                    END AS da_dang_ky
                FROM dbo.vw_de_tai_con_cho v
                JOIN dbo.sinh_vien_lop svl
                    ON svl.ma_lop_hoc_phan = v.ma_lop_hoc_phan
                    AND svl.trang_thai = N'DANG_HOC'
                JOIN dbo.sinh_vien sv
                    ON sv.ma_sinh_vien = svl.ma_sinh_vien
                WHERE sv.ma_tai_khoan = ?
                ORDER BY v.ma_lop, v.ma_de_tai_he_thong
                """;

        List<StudentTopicSummary> topics = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    topics.add(new StudentTopicSummary(
                            resultSet.getInt("ma_de_tai_lop"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ma_de_tai_he_thong"),
                            resultSet.getString("ten_de_tai"),
                            resultSet.getInt("so_luong_toi_da"),
                            resultSet.getInt("so_luong_hien_tai"),
                            resultSet.getInt("so_cho_con_lai"),
                            resultSet.getString("trang_thai"),
                            resultSet.getString("che_do_phan_cong"),
                            resultSet.getInt("da_dang_ky") == 1));
                }
            }

            return topics;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách đề tài: " + e.getMessage(), e);
        }
    }

    /**
     * Chi tiết một đề tài theo mã đề tài lớp.
     */
    public Optional<TopicDetail> findTopicDetail(int maDeTaiLop) {
        String sql = """
                SELECT
                    v.ma_de_tai_lop,
                    v.ma_lop_hoc_phan,
                    v.ma_lop,
                    v.ma_de_tai_he_thong,
                    v.ten_de_tai,
                    v.mo_ta,
                    v.yeu_cau,
                    v.so_luong_toi_da,
                    v.so_luong_hien_tai,
                    v.so_cho_con_lai,
                    v.trang_thai,
                    v.che_do_phan_cong
                FROM dbo.vw_de_tai_con_cho v
                WHERE v.ma_de_tai_lop = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maDeTaiLop);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new TopicDetail(
                        resultSet.getInt("ma_de_tai_lop"),
                        resultSet.getInt("ma_lop_hoc_phan"),
                        resultSet.getString("ma_lop"),
                        resultSet.getString("ma_de_tai_he_thong"),
                        resultSet.getString("ten_de_tai"),
                        resultSet.getString("mo_ta"),
                        resultSet.getString("yeu_cau"),
                        resultSet.getInt("so_luong_toi_da"),
                        resultSet.getInt("so_luong_hien_tai"),
                        resultSet.getInt("so_cho_con_lai"),
                        resultSet.getString("trang_thai"),
                        resultSet.getString("che_do_phan_cong")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải chi tiết đề tài: " + e.getMessage(), e);
        }
    }

    /**
     * Các đề tài mà sinh viên đã đăng ký (thường là 1 đề tài / lớp học phần).
     */
    public List<RegisteredTopic> findMyRegistrations(int maTaiKhoan) {
        String sql = """
                SELECT
                    dk.ma_dang_ky,
                    dk.ma_sinh_vien,
                    dk.ma_lop_hoc_phan,
                    dk.ma_de_tai_lop,
                    lhp.ma_lop,
                    lhp.ten_lop_hoc_phan,
                    mh.ten_mon_hoc,
                    hk.ten_hoc_ky,
                    hk.nam_hoc,
                    gv.ho_ten AS ten_giang_vien,
                    ndt.ma_de_tai_he_thong,
                    ndt.ten_de_tai,
                    ndt.mo_ta,
                    ndt.yeu_cau,
                    dk.hinh_thuc_phan_cong,
                    dk.thoi_diem_dang_ky,
                    dtl.so_luong_toi_da,
                    dtl.so_luong_hien_tai,
                    dtl.trang_thai AS trang_thai_de_tai
                FROM dbo.dang_ky_de_tai dk
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = dk.ma_sinh_vien
                JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dk.ma_lop_hoc_phan
                JOIN dbo.mon_hoc mh ON mh.ma_mon_hoc = lhp.ma_mon_hoc
                JOIN dbo.hoc_ky hk ON hk.ma_hoc_ky = lhp.ma_hoc_ky
                JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
                JOIN dbo.de_tai_lop dtl ON dtl.ma_de_tai_lop = dk.ma_de_tai_lop
                JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
                WHERE sv.ma_tai_khoan = ?
                ORDER BY dk.thoi_diem_dang_ky DESC
                """;

        List<RegisteredTopic> registrations = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maTaiKhoan);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Timestamp thoiDiem = resultSet.getTimestamp("thoi_diem_dang_ky");
                    registrations.add(new RegisteredTopic(
                            resultSet.getInt("ma_dang_ky"),
                            resultSet.getInt("ma_sinh_vien"),
                            resultSet.getInt("ma_lop_hoc_phan"),
                            resultSet.getInt("ma_de_tai_lop"),
                            resultSet.getString("ma_lop"),
                            resultSet.getString("ten_lop_hoc_phan"),
                            resultSet.getString("ten_mon_hoc"),
                            resultSet.getString("ten_hoc_ky"),
                            resultSet.getString("nam_hoc"),
                            resultSet.getString("ten_giang_vien"),
                            resultSet.getString("ma_de_tai_he_thong"),
                            resultSet.getString("ten_de_tai"),
                            resultSet.getString("mo_ta"),
                            resultSet.getString("yeu_cau"),
                            resultSet.getString("hinh_thuc_phan_cong"),
                            thoiDiem == null ? null : thoiDiem.toLocalDateTime(),
                            resultSet.getInt("so_luong_toi_da"),
                            resultSet.getInt("so_luong_hien_tai"),
                            resultSet.getString("trang_thai_de_tai")));
                }
            }

            return registrations;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải đề tài đã đăng ký: " + e.getMessage(), e);
        }
    }

    /**
     * Sinh viên tự đăng ký đề tài. Gọi stored procedure để giữ đúng ràng buộc
     * (đợt đăng ký đang mở, còn chỗ, chưa đăng ký đề tài khác...).
     */
    public void registerTopic(int maSinhVien, int maDeTaiLop) {
        String sql = "{call dbo.sp_dang_ky_de_tai(?, ?)}";

        try (
                Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {
            statement.setInt(1, maSinhVien);
            statement.setInt(2, maDeTaiLop);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Sinh viên hủy đăng ký đề tài trong một lớp học phần.
     */
    public void cancelRegistration(int maSinhVien, int maLopHocPhan, String lyDo) {
        String sql = "{call dbo.sp_huy_dang_ky_de_tai(?, ?, ?)}";

        try (
                Connection connection = DatabaseConnection.getConnection();
                CallableStatement statement = connection.prepareCall(sql)) {
            statement.setInt(1, maSinhVien);
            statement.setInt(2, maLopHocPhan);
            if (lyDo == null || lyDo.isBlank()) {
                statement.setNull(3, java.sql.Types.NVARCHAR);
            } else {
                statement.setString(3, lyDo);
            }
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
