USE [PhanCongDeTai];
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* ============================================================
   DU LIEU MAU DEMO
   Chay sau sql/schema.sql.

   Tai khoan demo:
   - Admin:      admin / 123456
   - Giang vien: gv01  / 123456
   - Sinh vien da co de tai:    N23DCCN007 / 123456
   - Sinh vien chua co de tai:  N23DCCN023 / 123456
   - Sinh vien da co lich su:   N23DCAT056 / 123456
   ============================================================ */

/* 1. Tai khoan */
INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email, so_dien_thoai)
VALUES
(N'admin', N'123456', N'QUAN_TRI_VIEN', N'HOAT_DONG', N'admin@ptit.edu.vn', N'0900000001'),
(N'gv01', N'123456', N'GIANG_VIEN', N'HOAT_DONG', N'ntbnguyen@ptit.edu.vn', N'0900000002'),
(N'gv02', N'123456', N'GIANG_VIEN', N'HOAT_DONG', N'nvmanh@ptit.edu.vn', N'0900000003'),
(N'N23DCCN007', N'123456', N'SINH_VIEN', N'HOAT_DONG', N'n23dccn007@student.ptit.edu.vn', N'0910000007'),
(N'N23DCCN023', N'123456', N'SINH_VIEN', N'HOAT_DONG', N'n23dccn023@student.ptit.edu.vn', N'0910000023'),
(N'N23DCAT056', N'123456', N'SINH_VIEN', N'HOAT_DONG', N'n23dcat056@student.ptit.edu.vn', N'0910000056'),
(N'N23DCCN099', N'123456', N'SINH_VIEN', N'HOAT_DONG', N'n23dccn099@student.ptit.edu.vn', N'0910000099'),
(N'sv_khoa', N'123456', N'SINH_VIEN', N'BI_KHOA', N'sv.khoa@student.ptit.edu.vn', N'0910000100');
GO

/* 2. Ho so giang vien va sinh vien */
INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, so_dien_thoai, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV001', N'Nguyễn Thị Bích Nguyên', N'ntbnguyen@ptit.edu.vn', N'0900000002',
       N'Công nghệ phần mềm', N'Thạc sĩ'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv01';

INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, so_dien_thoai, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV002', N'Nguyễn Văn Mạnh', N'nvmanh@ptit.edu.vn', N'0900000003',
       N'Cơ sở dữ liệu', N'Tiến sĩ'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv02';
GO

INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai, lop_sinh_hoat, khoa_hoc, nganh)
SELECT ma_tai_khoan, N'N23DCCN007', N'Đỗ Cao Cường', N'n23dccn007@student.ptit.edu.vn', N'0910000007',
       N'D23CQCN01-N', N'2023', N'Công nghệ thông tin'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'N23DCCN007';

INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai, lop_sinh_hoat, khoa_hoc, nganh)
SELECT ma_tai_khoan, N'N23DCCN023', N'Lê Tiến Hưng', N'n23dccn023@student.ptit.edu.vn', N'0910000023',
       N'D23CQCN01-N', N'2023', N'Công nghệ thông tin'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'N23DCCN023';

INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai, lop_sinh_hoat, khoa_hoc, nganh)
SELECT ma_tai_khoan, N'N23DCAT056', N'Từ Minh Quốc', N'n23dcat056@student.ptit.edu.vn', N'0910000056',
       N'D23CQCN01-N', N'2023', N'An toàn thông tin'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'N23DCAT056';

INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai, lop_sinh_hoat, khoa_hoc, nganh)
SELECT ma_tai_khoan, N'N23DCCN099', N'Nguyễn Minh Anh', N'n23dccn099@student.ptit.edu.vn', N'0910000099',
       N'D23CQCN01-N', N'2023', N'Công nghệ thông tin'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'N23DCCN099';

INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai, lop_sinh_hoat, khoa_hoc, nganh)
SELECT ma_tai_khoan, N'N23DCCN100', N'Sinh Viên Bị Khóa', N'sv.khoa@student.ptit.edu.vn', N'0910000100',
       N'D23CQCN01-N', N'2023', N'Công nghệ thông tin'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'sv_khoa';
GO

/* 3. Mon hoc, hoc ky, lop hoc phan */
INSERT INTO dbo.mon_hoc (ma_mon_hoc_he_thong, ten_mon_hoc, so_tin_chi, mo_ta)
VALUES
(N'CNPM', N'Công nghệ phần mềm', 3, N'Quy trình phân tích, thiết kế và phát triển phần mềm.'),
(N'CSDL', N'Cơ sở dữ liệu', 3, N'Thiết kế cơ sở dữ liệu quan hệ và truy vấn SQL.'),
(N'LTJAVA', N'Lập trình Java', 3, N'Lập trình Java và xây dựng ứng dụng desktop.');
GO

INSERT INTO dbo.hoc_ky (ma_hoc_ky_he_thong, ten_hoc_ky, nam_hoc, ngay_bat_dau, ngay_ket_thuc, trang_thai)
VALUES
(N'HK2_2025_2026', N'Học kỳ 2', N'2025-2026', '2026-01-15', '2026-06-30', N'DANG_MO'),
(N'HK1_2026_2027', N'Học kỳ 1', N'2026-2027', '2026-09-01', '2027-01-15', N'NHAP');
GO

DECLARE @ma_mon_cnpm INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'CNPM');
DECLARE @ma_mon_csdl INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'CSDL');
DECLARE @ma_hk2 INT = (SELECT ma_hoc_ky FROM dbo.hoc_ky WHERE ma_hoc_ky_he_thong = N'HK2_2025_2026');
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @ma_gv02 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');

INSERT INTO dbo.lop_hoc_phan (ma_lop, ten_lop_hoc_phan, ma_mon_hoc, ma_hoc_ky, ma_giang_vien, si_so_toi_da, ghi_chu)
VALUES
(N'CNPM_D23CQCN01_N', N'Công nghệ phần mềm - D23CQCN01-N', @ma_mon_cnpm, @ma_hk2, @ma_gv01, 80,
 N'Lớp học phần chính dùng để demo đăng ký đề tài.'),
(N'CSDL_D23CQCN01_N', N'Cơ sở dữ liệu - D23CQCN01-N', @ma_mon_csdl, @ma_hk2, @ma_gv02, 80,
 N'Lớp học phần phụ dùng để demo dữ liệu quản trị.');
GO

DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');
DECLARE @ma_lop_csdl INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CSDL_D23CQCN01_N');

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_cnpm, ma_sinh_vien, N'Dữ liệu mẫu lớp CNPM'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (N'N23DCCN007', N'N23DCCN023', N'N23DCAT056', N'N23DCCN099');

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_csdl, ma_sinh_vien, N'Dữ liệu mẫu lớp CSDL'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (N'N23DCCN099');
GO

/* 4. Ngan hang de tai va de tai lop */
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @ma_gv02 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');

INSERT INTO dbo.ngan_hang_de_tai (
    ma_de_tai_he_thong, ten_de_tai, mo_ta, yeu_cau, so_luong_mac_dinh, ma_giang_vien_tao
)
VALUES
(N'DT001', N'Xây dựng ứng dụng phân công đề tài cho sinh viên',
 N'Quản lý lớp học phần, ngân hàng đề tài, đăng ký/hủy đăng ký và xuất danh sách nhóm.',
 N'Có đăng nhập, phân quyền, database, ràng buộc đăng ký và báo cáo.', 1, @ma_gv01),
(N'DT002', N'Xây dựng ứng dụng quản lý thư viện',
 N'Quản lý sách, độc giả, phiếu mượn trả và thống kê sách.',
 N'Có CRUD, tìm kiếm, báo cáo và phân quyền.', 3, @ma_gv01),
(N'DT003', N'Xây dựng ứng dụng quản lý bảo trì xe',
 N'Quản lý xe, lịch bảo trì, nhắc hạn và hồ sơ bảo dưỡng.',
 N'Có CRUD, tìm kiếm, lọc trạng thái và báo cáo.', 2, @ma_gv01),
(N'DT004', N'Xây dựng ứng dụng quản lý phòng máy',
 N'Quản lý phòng máy, máy tính, lịch sử dụng và bảo trì.',
 N'Có giao diện JavaFX, SQL Server và báo cáo.', 2, @ma_gv01),
(N'DT005', N'Xây dựng ứng dụng quản lý câu lạc bộ sinh viên',
 N'Quản lý thành viên, sự kiện, điểm danh và báo cáo hoạt động.',
 N'Có đăng nhập, tìm kiếm, lọc dữ liệu và xuất báo cáo.', 2, @ma_gv01),
(N'DT006', N'Xây dựng ứng dụng theo dõi tiến độ đồ án',
 N'Quản lý nhiệm vụ, deadline, tiến độ và phản hồi của giảng viên.',
 N'Có dashboard, thông báo trạng thái và báo cáo tiến độ.', 2, @ma_gv01),
(N'CSDL01', N'Thiết kế cơ sở dữ liệu quản lý bán hàng',
 N'Mô hình hóa dữ liệu bán hàng, hóa đơn, tồn kho và khách hàng.',
 N'Có mô hình ERD, chuẩn hóa và truy vấn báo cáo.', 3, @ma_gv02);
GO

DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');
DECLARE @ma_lop_csdl INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CSDL_D23CQCN01_N');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_cnpm, ma_de_tai, so_luong_mac_dinh, N'SINH_VIEN_TU_DANG_KY'
FROM dbo.ngan_hang_de_tai
WHERE ma_de_tai_he_thong IN (N'DT001', N'DT002', N'DT003', N'DT004', N'DT005', N'DT006');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_csdl, ma_de_tai, so_luong_mac_dinh, N'SINH_VIEN_TU_DANG_KY'
FROM dbo.ngan_hang_de_tai
WHERE ma_de_tai_he_thong = N'CSDL01';

UPDATE dtl
SET trang_thai = N'DA_DONG'
FROM dbo.de_tai_lop dtl
JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
WHERE ndt.ma_de_tai_he_thong = N'DT004';
GO

/* 5. Mo cong dang ky lop CNPM */
DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @bat_dau DATETIME2(0) = DATEADD(DAY, -1, SYSDATETIME());
DECLARE @ket_thuc DATETIME2(0) = DATEADD(DAY, 14, SYSDATETIME());

EXEC dbo.sp_mo_cong_dang_ky
    @ma_lop_hoc_phan = @ma_lop_cnpm,
    @ma_giang_vien = @ma_gv01,
    @thoi_gian_bat_dau = @bat_dau,
    @thoi_gian_ket_thuc = @ket_thuc,
    @ghi_chu = N'Cổng đăng ký mẫu phục vụ demo';
GO

/* 6. Dang ky mau va lich su mau */
DECLARE @ma_sv_cuong INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN007');
DECLARE @ma_sv_hung INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN023');
DECLARE @ma_sv_quoc INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCAT056');
DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');

DECLARE @dt001 INT = (
    SELECT dtl.ma_de_tai_lop
    FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE ndt.ma_de_tai_he_thong = N'DT001'
);
DECLARE @dt002 INT = (
    SELECT dtl.ma_de_tai_lop
    FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE ndt.ma_de_tai_he_thong = N'DT002'
);
DECLARE @dt003 INT = (
    SELECT dtl.ma_de_tai_lop
    FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE ndt.ma_de_tai_he_thong = N'DT003'
);

-- Cuong da co de tai; DT001 se day vi so_luong_toi_da = 1.
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_cuong, @ma_de_tai_lop = @dt001;

-- Quoc da co de tai de demo man "De tai da chon".
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_quoc, @ma_de_tai_lop = @dt003;

-- Hung co lich su huy, hien tai chua co de tai de demo dang ky moi.
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_hung, @ma_de_tai_lop = @dt002;
EXEC dbo.sp_huy_dang_ky_de_tai
    @ma_sinh_vien = @ma_sv_hung,
    @ma_lop_hoc_phan = @ma_lop_cnpm,
    @ly_do = N'Dữ liệu mẫu: sinh viên hủy để đăng ký lại trong demo';
GO

/* 7. Nhat ky he thong */
INSERT INTO dbo.nhat_ky_he_thong (ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung)
SELECT ma_tai_khoan, N'Khởi tạo dữ liệu', N'TAO_DU_LIEU_MAU', N'ALL',
       N'Tạo dữ liệu mẫu đủ cho demo Admin, Giảng viên và Sinh viên'
FROM dbo.tai_khoan
WHERE ten_dang_nhap = N'admin';
GO

/* ============================================================
   CAU LENH KIEM TRA NHANH SAU KHI CHAY DATA.SQL
   ============================================================ */
SELECT N'DA NAP DU LIEU MAU THANH CONG' AS ket_qua;
SELECT ma_tai_khoan, ten_dang_nhap, vai_tro, trang_thai FROM dbo.tai_khoan ORDER BY ma_tai_khoan;
SELECT * FROM dbo.vw_thong_ke_lop_hoc_phan;
SELECT * FROM dbo.vw_de_tai_con_cho ORDER BY ma_lop, ma_de_tai_he_thong;
SELECT * FROM dbo.vw_bao_cao_nhom_de_tai ORDER BY ma_lop, ma_de_tai_he_thong, ma_so_sinh_vien;
SELECT * FROM dbo.vw_sinh_vien_chua_co_de_tai ORDER BY ma_lop, ma_so_sinh_vien;
GO
