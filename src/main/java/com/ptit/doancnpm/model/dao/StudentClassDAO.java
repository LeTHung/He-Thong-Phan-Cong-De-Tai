package com.ptit.doancnpm.model.dao;

import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;
import com.ptit.doancnpm.model.dto.StudentExcelImportResult;
import com.ptit.doancnpm.model.dto.StudentExcelRow;
import com.ptit.doancnpm.util.DatabaseConnection;
import com.ptit.doancnpm.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentClassDAO {

    public List<OptionItem> findCourseSectionOptions() {
        String sql = """
                SELECT
                    lhp.ma_lop_hoc_phan,
                    lhp.ma_lop,
                    CONCAT(lhp.ten_lop_hoc_phan, N' — ', hk.ten_hoc_ky, N' (', hk.nam_hoc, N')')
                FROM dbo.lop_hoc_phan lhp
                JOIN dbo.hoc_ky hk ON hk.ma_hoc_ky = lhp.ma_hoc_ky
                WHERE lhp.trang_thai <> N'LUU_TRU'
                ORDER BY hk.ngay_bat_dau DESC, lhp.ten_lop_hoc_phan
                """;
        return findOptions(sql);
    }

    public List<OptionItem> findAvailableStudentOptions(int courseSectionId) {
        String sql = """
                SELECT sv.ma_sinh_vien, sv.ma_so_sinh_vien, sv.ho_ten
                FROM dbo.sinh_vien sv
                JOIN dbo.tai_khoan tk ON tk.ma_tai_khoan = sv.ma_tai_khoan
                WHERE sv.trang_thai = N'DANG_HOC'
                  AND tk.trang_thai = N'HOAT_DONG'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM dbo.sinh_vien_lop svl
                      WHERE svl.ma_lop_hoc_phan = ?
                        AND svl.ma_sinh_vien = sv.ma_sinh_vien
                        AND svl.trang_thai = N'DANG_HOC'
                  )
                ORDER BY sv.ma_so_sinh_vien
                """;

        List<OptionItem> options = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    options.add(new OptionItem(
                            resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3)));
                }
            }
            return options;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách sinh viên có thể thêm: " + e.getMessage(), e);
        }
    }

    public List<StudentClassMemberSummary> findStudentsByCourseSection(int courseSectionId) {
        String sql = """
                SELECT
                    svl.ma_lop_hoc_phan,
                    sv.ma_sinh_vien,
                    sv.ma_so_sinh_vien,
                    sv.ho_ten,
                    sv.email,
                    sv.lop_sinh_hoat,
                    svl.trang_thai,
                    svl.ghi_chu
                FROM dbo.sinh_vien_lop svl
                JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
                WHERE svl.ma_lop_hoc_phan = ?
                ORDER BY
                    CASE WHEN svl.trang_thai = N'DANG_HOC' THEN 0 ELSE 1 END,
                    sv.ma_so_sinh_vien
                """;

        List<StudentClassMemberSummary> students = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    students.add(mapStudent(resultSet));
                }
            }
            return students;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải danh sách sinh viên lớp: " + e.getMessage(), e);
        }
    }

    public void addStudentsToCourseSection(int courseSectionId, List<Integer> studentIds, String note) {
        String updateSql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DANG_HOC',
                    ngay_tham_gia = CASE WHEN trang_thai = N'DA_RUT'
                                         THEN CONVERT(DATE, SYSDATETIME()) ELSE ngay_tham_gia END,
                    thoi_diem_tham_gia = CASE WHEN trang_thai = N'DA_RUT'
                                              THEN SYSDATETIME() ELSE thoi_diem_tham_gia END,
                    ghi_chu = ?
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;
        String insertSql = """
                INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                    PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                for (int studentId : studentIds) {
                    updateStatement.setString(1, note);
                    updateStatement.setInt(2, courseSectionId);
                    updateStatement.setInt(3, studentId);
                    int affectedRows = updateStatement.executeUpdate();

                    if (affectedRows == 0) {
                        insertStatement.setInt(1, courseSectionId);
                        insertStatement.setInt(2, studentId);
                        insertStatement.setString(3, note);
                        insertStatement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm danh sách sinh viên vào lớp: " + e.getMessage(), e);
        }
    }

    public StudentExcelImportResult importStudents(int courseSectionId, List<StudentExcelRow> rows) {
        int createdStudents = 0;
        int updatedStudents = 0;
        int addedToClass = 0;
        int skippedRows = 0;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (StudentExcelRow row : rows) {
                    StudentRef student = findStudentByCode(connection, row.studentCode());
                    if (student != null) {
                        if (!"SINH_VIEN".equalsIgnoreCase(student.accountRole())) {
                            throw new IllegalArgumentException(
                                    "Dòng " + row.excelRowNumber() + ": tài khoản không có vai trò Sinh viên.");
                        }
                        if ("BI_KHOA".equalsIgnoreCase(student.accountStatus())) {
                            skippedRows++;
                            continue;
                        }
                        updateAccountEmail(connection, student.accountId(), row.email());
                        updateStudent(connection, student.studentId(), row);
                        updatedStudents++;
                    } else {
                        AccountRef account = findAccountByUsername(connection, row.studentCode());
                        if (account != null && !"SINH_VIEN".equalsIgnoreCase(account.role())) {
                            throw new IllegalArgumentException(
                                    "Dòng " + row.excelRowNumber() + ": tên đăng nhập đã thuộc vai trò khác.");
                        }
                        if (account != null && "BI_KHOA".equalsIgnoreCase(account.status())) {
                            skippedRows++;
                            continue;
                        }

                        int accountId;
                        if (account == null) {
                            accountId = createStudentAccount(connection, row);
                        } else {
                            accountId = account.accountId();
                            if (findStudentIdByAccount(connection, accountId) != null) {
                                throw new IllegalArgumentException(
                                        "Dòng " + row.excelRowNumber() + ": tài khoản đã gắn với mã sinh viên khác.");
                            }
                            updateAccountEmail(connection, accountId, row.email());
                        }
                        int studentId = createStudent(connection, accountId, row);
                        student = new StudentRef(studentId, accountId, "HOAT_DONG", "SINH_VIEN");
                        createdStudents++;
                    }

                    String enrollmentStatus = findEnrollmentStatus(connection, courseSectionId, student.studentId());
                    if ("DANG_HOC".equalsIgnoreCase(enrollmentStatus)) {
                        skippedRows++;
                    } else if (enrollmentStatus == null) {
                        insertEnrollment(connection, courseSectionId, student.studentId());
                        addedToClass++;
                    } else {
                        reactivateEnrollment(connection, courseSectionId, student.studentId());
                        addedToClass++;
                    }
                }
                connection.commit();
                return new StudentExcelImportResult(
                        rows.size(), createdStudents, updatedStudents, addedToClass, skippedRows);
            } catch (SQLException | RuntimeException exception) {
                DatabaseConnection.rollbackQuietly(connection);
                throw exception;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Lỗi import sinh viên: " + exception.getMessage(), exception);
        }
    }

    public void withdrawStudentFromCourseSection(int courseSectionId, int studentId) {
        String sql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DA_RUT'
                WHERE ma_lop_hoc_phan = ?
                  AND ma_sinh_vien = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Không tìm thấy sinh viên trong lớp để rút.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi rút sinh viên khỏi lớp: " + e.getMessage(), e);
        }
    }

    private List<OptionItem> findOptions(String sql) {
        List<OptionItem> options = new ArrayList<>();
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                options.add(new OptionItem(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)));
            }
            return options;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải dữ liệu chọn: " + e.getMessage(), e);
        }
    }

    private StudentRef findStudentByCode(Connection connection, String studentCode) throws SQLException {
        String sql = """
                SELECT sv.ma_sinh_vien, sv.ma_tai_khoan, tk.trang_thai, tk.vai_tro
                FROM dbo.sinh_vien sv
                JOIN dbo.tai_khoan tk ON tk.ma_tai_khoan = sv.ma_tai_khoan
                WHERE sv.ma_so_sinh_vien = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? new StudentRef(
                                resultSet.getInt("ma_sinh_vien"),
                                resultSet.getInt("ma_tai_khoan"),
                                resultSet.getString("trang_thai"),
                                resultSet.getString("vai_tro"))
                        : null;
            }
        }
    }

    private AccountRef findAccountByUsername(Connection connection, String username) throws SQLException {
        String sql = """
                SELECT ma_tai_khoan, trang_thai, vai_tro
                FROM dbo.tai_khoan
                WHERE ten_dang_nhap = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        ? new AccountRef(
                                resultSet.getInt("ma_tai_khoan"),
                                resultSet.getString("trang_thai"),
                                resultSet.getString("vai_tro"))
                        : null;
            }
        }
    }

    private Integer findStudentIdByAccount(Connection connection, int accountId) throws SQLException {
        String sql = "SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_tai_khoan = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : null;
            }
        }
    }

    private int createStudentAccount(Connection connection, StudentExcelRow row) throws SQLException {
        String sql = """
                INSERT INTO dbo.tai_khoan (
                    ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email
                ) VALUES (?, ?, N'SINH_VIEN', N'HOAT_DONG', ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, row.studentCode());
            statement.setString(2, PasswordUtil.hash("123456"));
            statement.setString(3, row.email());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Không lấy được mã tài khoản vừa tạo.");
                }
                return keys.getInt(1);
            }
        }
    }

    private int createStudent(Connection connection, int accountId, StudentExcelRow row) throws SQLException {
        String sql = """
                INSERT INTO dbo.sinh_vien (
                    ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, lop_sinh_hoat, trang_thai
                ) VALUES (?, ?, ?, ?, ?, N'DANG_HOC')
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, accountId);
            statement.setString(2, row.studentCode());
            statement.setString(3, row.fullName());
            statement.setString(4, row.email());
            statement.setString(5, row.classCode());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Không lấy được mã sinh viên vừa tạo.");
                }
                return keys.getInt(1);
            }
        }
    }

    private void updateAccountEmail(Connection connection, int accountId, String email) throws SQLException {
        String sql = """
                UPDATE dbo.tai_khoan
                SET email = ?, thoi_diem_cap_nhat = SYSDATETIME()
                WHERE ma_tai_khoan = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setInt(2, accountId);
            statement.executeUpdate();
        }
    }

    private void updateStudent(Connection connection, int studentId, StudentExcelRow row) throws SQLException {
        String sql = """
                UPDATE dbo.sinh_vien
                SET ho_ten = ?, email = ?, lop_sinh_hoat = ?, trang_thai = N'DANG_HOC'
                WHERE ma_sinh_vien = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, row.fullName());
            statement.setString(2, row.email());
            statement.setString(3, row.classCode());
            statement.setInt(4, studentId);
            statement.executeUpdate();
        }
    }

    private String findEnrollmentStatus(Connection connection, int courseSectionId, int studentId)
            throws SQLException {
        String sql = """
                SELECT trang_thai
                FROM dbo.sinh_vien_lop
                WHERE ma_lop_hoc_phan = ? AND ma_sinh_vien = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString(1) : null;
            }
        }
    }

    private void insertEnrollment(Connection connection, int courseSectionId, int studentId) throws SQLException {
        String sql = """
                INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
                VALUES (?, ?, N'Import Excel')
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            statement.executeUpdate();
        }
    }

    private void reactivateEnrollment(Connection connection, int courseSectionId, int studentId)
            throws SQLException {
        String sql = """
                UPDATE dbo.sinh_vien_lop
                SET trang_thai = N'DANG_HOC',
                    ngay_tham_gia = CONVERT(DATE, SYSDATETIME()),
                    thoi_diem_tham_gia = SYSDATETIME(),
                    ghi_chu = N'Import Excel'
                WHERE ma_lop_hoc_phan = ? AND ma_sinh_vien = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseSectionId);
            statement.setInt(2, studentId);
            statement.executeUpdate();
        }
    }


    private StudentClassMemberSummary mapStudent(ResultSet resultSet) throws SQLException {
        return new StudentClassMemberSummary(
                resultSet.getInt("ma_lop_hoc_phan"),
                resultSet.getInt("ma_sinh_vien"),
                resultSet.getString("ma_so_sinh_vien"),
                resultSet.getString("ho_ten"),
                resultSet.getString("email"),
                resultSet.getString("lop_sinh_hoat"),
                resultSet.getString("trang_thai"),
                resultSet.getString("ghi_chu"));
    }

    private record StudentRef(int studentId, int accountId, String accountStatus, String accountRole) {
    }

    private record AccountRef(int accountId, String status, String role) {
    }
}
