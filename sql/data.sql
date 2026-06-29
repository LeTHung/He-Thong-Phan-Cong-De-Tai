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
   - Tat ca tai khoan con lai cung dung mat khau: 123456
   ============================================================ */

/* 1. Tai khoan */
DECLARE @H_123456 NVARCHAR(255) = N'pbkdf2$210000$W/w84chRx538ZB6wnz+2lQ==$kgYcgOcf3Lt8ww9tqNAzE8SAk0sXw2akCDImFo8Y/KI=';

INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email, so_dien_thoai)
VALUES
(N'admin', @H_123456, N'QUAN_TRI_VIEN', N'HOAT_DONG', N'admin@ptit.edu.vn', N'0900000001'),
(N'admin02', @H_123456, N'QUAN_TRI_VIEN', N'HOAT_DONG', N'admin02@ptit.edu.vn', N'0900000010'),
(N'gv01', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'ntbnguyen@ptit.edu.vn', N'0900000002'),
(N'gv02', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'nvmanh@ptit.edu.vn', N'0900000003'),
(N'gv03', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'tthlan@ptit.edu.vn', N'0900000004'),
(N'N23DCCN001', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn001@student.ptit.edu.vn', N'0910000001'),
(N'N23DCCN002', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn002@student.ptit.edu.vn', N'0910000002'),
(N'N23DCCN007', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn007@student.ptit.edu.vn', N'0910000007'),
(N'N23DCCN015', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn015@student.ptit.edu.vn', N'0910000015'),
(N'N23DCCN016', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn016@student.ptit.edu.vn', N'0910000016'),
(N'N23DCCN023', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn023@student.ptit.edu.vn', N'0910000023'),
(N'N23DCAT056', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dcat056@student.ptit.edu.vn', N'0910000056'),
(N'N23DCCN070', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn070@student.ptit.edu.vn', N'0910000070'),
(N'N23DCCN080', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn080@student.ptit.edu.vn', N'0910000080'),
(N'N23DCCN086', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn086@student.ptit.edu.vn', N'0910000086'),
(N'N23DCCN087', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn087@student.ptit.edu.vn', N'0910000087'),
(N'N23DCCN099', @H_123456, N'SINH_VIEN', N'HOAT_DONG', N'n23dccn099@student.ptit.edu.vn', N'0910000099'),
(N'sv_khoa', @H_123456, N'SINH_VIEN', N'BI_KHOA', N'sv.khoa@student.ptit.edu.vn', N'0910000100');
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

INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, so_dien_thoai, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV003', N'Trần Thị Hương Lan', N'tthlan@ptit.edu.vn', N'0900000004',
       N'Mạng máy tính và an toàn thông tin', N'Thạc sĩ'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv03';
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

INSERT INTO dbo.sinh_vien (
    ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, so_dien_thoai,
    lop_sinh_hoat, khoa_hoc, nganh
)
SELECT
    tk.ma_tai_khoan, mau.ma_sinh_vien, mau.ho_ten, tk.email, tk.so_dien_thoai,
    mau.lop_sinh_hoat, N'2023', mau.nganh
FROM (VALUES
    (N'N23DCCN001', N'Đặng Kim An', N'D23CQCN01-N', N'Công nghệ thông tin'),
    (N'N23DCCN002', N'Trần Nguyên An', N'D23CQCN01-N', N'Công nghệ thông tin'),
    (N'N23DCCN015', N'Nguyễn Ngọc Duy', N'D23CQCN01-N', N'Công nghệ thông tin'),
    (N'N23DCCN016', N'Nguyễn Trần Ngọc Duyên', N'D23CQCN01-N', N'Công nghệ thông tin'),
    (N'N23DCCN070', N'Nguyễn Kỳ Đức An', N'D23CQCN02-N', N'Công nghệ thông tin'),
    (N'N23DCCN080', N'Trần Minh Đức', N'D23CQCN02-N', N'Công nghệ thông tin'),
    (N'N23DCCN086', N'Nguyễn Lê Nhựt Hào', N'D23CQCN02-N', N'Công nghệ thông tin'),
    (N'N23DCCN087', N'Dương Văn Hay', N'D23CQCN02-N', N'Công nghệ thông tin')
) AS mau(ma_sinh_vien, ho_ten, lop_sinh_hoat, nganh)
JOIN dbo.tai_khoan tk ON tk.ten_dang_nhap = mau.ma_sinh_vien;
GO

/* 3. Mon hoc, hoc ky, lop hoc phan */
INSERT INTO dbo.mon_hoc (ma_mon_hoc_he_thong, ten_mon_hoc, so_tin_chi, mo_ta)
VALUES
(N'CNPM', N'Công nghệ phần mềm', 3, N'Quy trình phân tích, thiết kế và phát triển phần mềm.'),
(N'CSDL', N'Cơ sở dữ liệu', 3, N'Thiết kế cơ sở dữ liệu quan hệ và truy vấn SQL.'),
(N'LTJAVA', N'Lập trình Java', 3, N'Lập trình Java và xây dựng ứng dụng desktop.'),
(N'MMT', N'Mạng máy tính', 3, N'Mô hình TCP/IP, thiết kế và vận hành hệ thống mạng.'),
(N'ATTT', N'An toàn thông tin', 3, N'Bảo mật ứng dụng, mã hóa và quản trị rủi ro.'),
(N'PTUD', N'Phát triển ứng dụng Web', 3, N'Xây dựng ứng dụng web theo kiến trúc nhiều lớp.');
GO

INSERT INTO dbo.hoc_ky (ma_hoc_ky_he_thong, ten_hoc_ky, nam_hoc, ngay_bat_dau, ngay_ket_thuc, trang_thai)
VALUES
(N'HK1_2025_2026', N'Học kỳ 1', N'2025-2026', '2025-09-01', '2026-01-10', N'DA_DONG'),
(N'HK2_2025_2026', N'Học kỳ 2', N'2025-2026', '2026-01-15', '2026-06-30', N'DANG_MO'),
(N'HK1_2026_2027', N'Học kỳ 1', N'2026-2027', '2026-09-01', '2027-01-15', N'NHAP');
GO

DECLARE @ma_mon_cnpm INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'CNPM');
DECLARE @ma_mon_csdl INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'CSDL');
DECLARE @ma_hk2 INT = (SELECT ma_hoc_ky FROM dbo.hoc_ky WHERE ma_hoc_ky_he_thong = N'HK2_2025_2026');
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @ma_gv02 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');
DECLARE @ma_gv03 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');

INSERT INTO dbo.lop_hoc_phan (
    ma_lop, ten_lop_hoc_phan, ma_mon_hoc, ma_hoc_ky,
    ma_giang_vien, si_so_toi_da, che_do_phan_cong, ghi_chu
)
VALUES
(N'CNPM_D23CQCN01_N', N'Công nghệ phần mềm - D23CQCN01-N', @ma_mon_cnpm, @ma_hk2, @ma_gv01, 80,
 N'SINH_VIEN_TU_DANG_KY',
 N'Lớp học phần chính dùng để demo đăng ký đề tài.'),
(N'CSDL_D23CQCN01_N', N'Cơ sở dữ liệu - D23CQCN01-N', @ma_mon_csdl, @ma_hk2, @ma_gv02, 80,
 N'GIANG_VIEN_PHAN_CONG',
 N'Lớp học phần phụ dùng để demo dữ liệu quản trị.');
GO

DECLARE @ma_mon_java INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'LTJAVA');
DECLARE @ma_mon_mmt INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'MMT');
DECLARE @ma_mon_attt INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'ATTT');
DECLARE @ma_hk2 INT = (SELECT ma_hoc_ky FROM dbo.hoc_ky WHERE ma_hoc_ky_he_thong = N'HK2_2025_2026');
DECLARE @ma_hk_tuong_lai INT = (SELECT ma_hoc_ky FROM dbo.hoc_ky WHERE ma_hoc_ky_he_thong = N'HK1_2026_2027');
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @ma_gv03 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');

INSERT INTO dbo.lop_hoc_phan (
    ma_lop, ten_lop_hoc_phan, ma_mon_hoc, ma_hoc_ky,
    ma_giang_vien, si_so_toi_da, che_do_phan_cong, ghi_chu, trang_thai
)
VALUES
(N'JAVA_D23CQCN01_N', N'Lập trình Java - D23CQCN01-N', @ma_mon_java, @ma_hk2, @ma_gv01, 60,
 N'SINH_VIEN_TU_DANG_KY', N'Cổng đăng ký được hẹn mở trong tương lai.', N'DANG_MO'),
(N'MMT_D23CQCN02_N', N'Mạng máy tính - D23CQCN02-N', @ma_mon_mmt, @ma_hk2, @ma_gv03, 50,
 N'GIANG_VIEN_PHAN_CONG', N'Lớp đã chốt để demo báo cáo cuối kỳ.', N'DANG_MO'),
(N'ATTT_D23CQAT01_N', N'An toàn thông tin - D23CQAT01-N', @ma_mon_attt, @ma_hk_tuong_lai, @ma_gv03, 40,
 N'SINH_VIEN_TU_DANG_KY', N'Lớp lưu trữ dùng để demo lọc trạng thái.', N'LUU_TRU');
GO

DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');
DECLARE @ma_lop_csdl INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CSDL_D23CQCN01_N');

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_cnpm, ma_sinh_vien, N'Dữ liệu mẫu lớp CNPM'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (
    N'N23DCCN001', N'N23DCCN002', N'N23DCCN007', N'N23DCCN015',
    N'N23DCCN016', N'N23DCCN023', N'N23DCAT056', N'N23DCCN070',
    N'N23DCCN080', N'N23DCCN086', N'N23DCCN087', N'N23DCCN099'
);

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_csdl, ma_sinh_vien, N'Dữ liệu mẫu lớp CSDL'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (
    N'N23DCCN070', N'N23DCCN080', N'N23DCCN086', N'N23DCCN087', N'N23DCCN099'
);

DECLARE @ma_lop_java INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'JAVA_D23CQCN01_N');
DECLARE @ma_lop_mmt INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'MMT_D23CQCN02_N');
DECLARE @ma_lop_attt INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'ATTT_D23CQAT01_N');

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_java, ma_sinh_vien, N'Dữ liệu mẫu lớp Java'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (
    N'N23DCCN001', N'N23DCCN002', N'N23DCCN015',
    N'N23DCCN016', N'N23DCCN070', N'N23DCCN080'
);

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_mmt, ma_sinh_vien, N'Dữ liệu mẫu lớp Mạng máy tính'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (
    N'N23DCCN001', N'N23DCCN002', N'N23DCCN015', N'N23DCCN016'
);

INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien, ghi_chu)
SELECT @ma_lop_attt, ma_sinh_vien, N'Dữ liệu mẫu lớp An toàn thông tin'
FROM dbo.sinh_vien
WHERE ma_so_sinh_vien IN (N'N23DCAT056', N'N23DCCN086');
GO

/* 4. Ngan hang de tai va de tai lop */
DECLARE @ma_gv01 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @ma_gv02 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');
DECLARE @ma_gv03 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');

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
(N'JAVA01', N'Ứng dụng quản lý cửa hàng bằng JavaFX',
 N'Quản lý sản phẩm, hóa đơn, khách hàng và doanh thu.',
 N'JavaFX, JDBC, SQL Server và mô hình MVC.', 3, @ma_gv01),
(N'JAVA02', N'Ứng dụng quản lý lịch học cá nhân',
 N'Quản lý thời khóa biểu, công việc và nhắc lịch.',
 N'JavaFX, lưu trữ dữ liệu và tìm kiếm.', 3, @ma_gv01),
(N'CSDL01', N'Thiết kế cơ sở dữ liệu quản lý bán hàng',
 N'Mô hình hóa dữ liệu bán hàng, hóa đơn, tồn kho và khách hàng.',
 N'Có mô hình ERD, chuẩn hóa và truy vấn báo cáo.', 3, @ma_gv02),
(N'CSDL02', N'Cơ sở dữ liệu quản lý bệnh viện',
 N'Quản lý bệnh nhân, lịch khám, hồ sơ bệnh án và viện phí.',
 N'Chuẩn hóa dữ liệu, transaction và báo cáo.', 3, @ma_gv02),
(N'CSDL03', N'Kho dữ liệu phân tích kết quả học tập',
 N'Tổng hợp điểm, lớp học và kết quả học tập theo học kỳ.',
 N'Thiết kế star schema và truy vấn tổng hợp.', 2, @ma_gv02),
(N'MMT01', N'Mô phỏng hệ thống mạng doanh nghiệp',
 N'Thiết kế VLAN, định tuyến và phân hoạch địa chỉ IP.',
 N'Có sơ đồ mạng, cấu hình và kiểm thử kết nối.', 2, @ma_gv03),
(N'MMT02', N'Giám sát thiết bị mạng trong phòng máy',
 N'Theo dõi trạng thái thiết bị, cảnh báo sự cố và lịch sử hoạt động.',
 N'Có dashboard và báo cáo trạng thái.', 2, @ma_gv03),
(N'MMT03', N'Xây dựng công cụ kiểm tra chất lượng mạng',
 N'Đo độ trễ, mất gói và tốc độ kết nối.',
 N'Có biểu đồ và lưu lịch sử đo.', 2, @ma_gv03),
(N'ATTT01', N'Hệ thống phát hiện đăng nhập bất thường',
 N'Phân tích lịch sử đăng nhập và cảnh báo hành vi đáng ngờ.',
 N'Có nhật ký, luật phát hiện và báo cáo.', 2, @ma_gv03);
GO

DECLARE @ma_lop_cnpm INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CNPM_D23CQCN01_N');
DECLARE @ma_lop_csdl INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CSDL_D23CQCN01_N');
DECLARE @ma_lop_java INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'JAVA_D23CQCN01_N');
DECLARE @ma_lop_mmt INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'MMT_D23CQCN02_N');
DECLARE @ma_lop_attt INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'ATTT_D23CQAT01_N');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_cnpm, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_cnpm
WHERE ma_de_tai_he_thong IN (N'DT001', N'DT002', N'DT003', N'DT004', N'DT005', N'DT006');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_csdl, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_csdl
WHERE ma_de_tai_he_thong = N'CSDL01';

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_csdl, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_csdl
WHERE ma_de_tai_he_thong IN (N'CSDL02', N'CSDL03');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_java, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_java
WHERE ma_de_tai_he_thong IN (N'JAVA01', N'JAVA02');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_mmt, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_mmt
WHERE ma_de_tai_he_thong IN (N'MMT01', N'MMT02', N'MMT03');

INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @ma_lop_attt, ndt.ma_de_tai, ndt.so_luong_mac_dinh, lhp.che_do_phan_cong
FROM dbo.ngan_hang_de_tai ndt
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = @ma_lop_attt
WHERE ma_de_tai_he_thong = N'ATTT01';

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
-- Còn dưới 2 ngày để tài khoản chưa đăng ký nhận cảnh báo hạn chót khi demo.
DECLARE @ket_thuc DATETIME2(0) = DATEADD(HOUR, 47, SYSDATETIME());

EXEC dbo.sp_mo_cong_dang_ky
    @ma_lop_hoc_phan = @ma_lop_cnpm,
    @ma_giang_vien = @ma_gv01,
    @thoi_gian_bat_dau = @bat_dau,
    @thoi_gian_ket_thuc = @ket_thuc,
    @ghi_chu = N'Cổng đăng ký mẫu phục vụ demo';

DECLARE @ma_lop_java INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'JAVA_D23CQCN01_N');
DECLARE @java_bat_dau DATETIME2(0) = DATEADD(DAY, 2, SYSDATETIME());
DECLARE @java_ket_thuc DATETIME2(0) = DATEADD(DAY, 12, SYSDATETIME());

EXEC dbo.sp_mo_cong_dang_ky
    @ma_lop_hoc_phan = @ma_lop_java,
    @ma_giang_vien = @ma_gv01,
    @thoi_gian_bat_dau = @java_bat_dau,
    @thoi_gian_ket_thuc = @java_ket_thuc,
    @ghi_chu = N'Cổng đăng ký đã lên lịch, chưa đến giờ mở';
GO

/* 6. Dang ky mau va lich su mau */
DECLARE @ma_sv_cuong INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN007');
DECLARE @ma_sv_hung INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN023');
DECLARE @ma_sv_quoc INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCAT056');
DECLARE @ma_sv_001 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN001');
DECLARE @ma_sv_002 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN002');
DECLARE @ma_sv_015 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN015');
DECLARE @ma_sv_016 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN016');
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
DECLARE @dt005 INT = (
    SELECT dtl.ma_de_tai_lop
    FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_cnpm
      AND ndt.ma_de_tai_he_thong = N'DT005'
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

-- Một số nhóm đang hoạt động để bảng kết quả và báo cáo có dữ liệu dễ nhìn.
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_001, @ma_de_tai_lop = @dt002;
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_002, @ma_de_tai_lop = @dt002;
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_015, @ma_de_tai_lop = @dt003;
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien = @ma_sv_016, @ma_de_tai_lop = @dt005;
GO

/* 7. Phan cong thu cong va lop da chot */
DECLARE @ma_gv02 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');
DECLARE @ma_lop_csdl INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'CSDL_D23CQCN01_N');
DECLARE @csdl_sv099 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN099');
DECLARE @csdl_sv070 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN070');
DECLARE @csdl_sv080 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN080');
DECLARE @csdl01 INT = (
    SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_csdl AND ndt.ma_de_tai_he_thong = N'CSDL01'
);
DECLARE @csdl02 INT = (
    SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_csdl AND ndt.ma_de_tai_he_thong = N'CSDL02'
);

EXEC dbo.sp_phan_cong_thu_cong
    @ma_giang_vien = @ma_gv02,
    @ma_sinh_vien = @csdl_sv099,
    @ma_de_tai_lop = @csdl01,
    @ghi_chu = N'Phân công mẫu nhóm cơ sở dữ liệu';
EXEC dbo.sp_phan_cong_thu_cong
    @ma_giang_vien = @ma_gv02,
    @ma_sinh_vien = @csdl_sv070,
    @ma_de_tai_lop = @csdl01,
    @ghi_chu = N'Phân công mẫu nhóm cơ sở dữ liệu';
EXEC dbo.sp_phan_cong_thu_cong
    @ma_giang_vien = @ma_gv02,
    @ma_sinh_vien = @csdl_sv080,
    @ma_de_tai_lop = @csdl02,
    @ghi_chu = N'Phân công mẫu nhóm cơ sở dữ liệu';
GO

DECLARE @ma_gv03 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');
DECLARE @ma_lop_mmt INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'MMT_D23CQCN02_N');
DECLARE @mmt_sv001 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN001');
DECLARE @mmt_sv002 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN002');
DECLARE @mmt_sv015 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN015');
DECLARE @mmt_sv016 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'N23DCCN016');
DECLARE @mmt01 INT = (
    SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_mmt AND ndt.ma_de_tai_he_thong = N'MMT01'
);
DECLARE @mmt02 INT = (
    SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_mmt AND ndt.ma_de_tai_he_thong = N'MMT02'
);
DECLARE @mmt03 INT = (
    SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl
    JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai
    WHERE dtl.ma_lop_hoc_phan = @ma_lop_mmt AND ndt.ma_de_tai_he_thong = N'MMT03'
);

EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien = @ma_gv03,
    @ma_sinh_vien = @mmt_sv001,
    @ma_de_tai_lop = @mmt01, @ghi_chu = N'Nhóm mạng số 1';
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien = @ma_gv03,
    @ma_sinh_vien = @mmt_sv002,
    @ma_de_tai_lop = @mmt01, @ghi_chu = N'Nhóm mạng số 1';
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien = @ma_gv03,
    @ma_sinh_vien = @mmt_sv015,
    @ma_de_tai_lop = @mmt02, @ghi_chu = N'Nhóm mạng số 2';
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien = @ma_gv03,
    @ma_sinh_vien = @mmt_sv016,
    @ma_de_tai_lop = @mmt03, @ghi_chu = N'Nhóm mạng số 3';

EXEC dbo.sp_chot_danh_sach
    @ma_giang_vien = @ma_gv03,
    @ma_lop_hoc_phan = @ma_lop_mmt;
GO

/* 8. Nhat ky he thong */
INSERT INTO dbo.nhat_ky_he_thong (ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung)
SELECT ma_tai_khoan, N'Khởi tạo dữ liệu', N'TAO_DU_LIEU_MAU', N'ALL',
       N'Tạo dữ liệu mẫu phong phú cho demo Admin, Giảng viên và Sinh viên'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'admin';

INSERT INTO dbo.nhat_ky_he_thong (ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung)
SELECT ma_tai_khoan, N'Quản lý lớp học phần', N'CHOT_DANH_SACH', N'lop_hoc_phan',
       N'Đã chốt lớp Mạng máy tính mẫu'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv03';
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
