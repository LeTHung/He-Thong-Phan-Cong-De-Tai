/* ============================================================
   DATA_SAMPLE_EXTENDED.SQL
   Du lieu mau mo rong cho kiem thu do an PhanCongDeTai.
   Chay SAU khi da chay sql/schema.sql goc (DB da rong).

   - Mat khau luu SHA-256 (khop PasswordUtil.sha256):
        admin   -> admin123
        con lai -> 123456
   - Tat ca FK hop le, khong co orphan record.
   - Tai khoan dung dat ten theo test case:
        Admin:      admin / admin123
        Giang vien: gv001, gv002, gv003 / 123456
        Sinh vien:  sv001 .. sv015 / 123456

   GHI CHU SCHEMA QUAN TRONG:
   - che_do_phan_cong duoc dong bo o cap LOP (trigger
     trg_de_tai_lop_dong_bo_che_do), KHONG the dat rieng tung
     de tai trong cung mot lop. Vi vay de tai che do PHAN_CONG
     (dt_lop_005) nam o lop rieng LHP005 (GIANG_VIEN_PHAN_CONG).
   ============================================================ */

USE [PhanCongDeTai];
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;
GO

DECLARE @H_ADMIN NVARCHAR(255) = N'240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9';
DECLARE @H_123456 NVARCHAR(255) = N'8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

/* ============================================================
   PHAN A: TAI KHOAN
   ============================================================ */
INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email)
VALUES (N'admin', @H_ADMIN, N'QUAN_TRI_VIEN', N'HOAT_DONG', N'admin@ptit.edu.vn');

INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email)
VALUES
(N'gv001', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'gv001@ptit.edu.vn'),
(N'gv002', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'gv002@ptit.edu.vn'),
(N'gv003', @H_123456, N'GIANG_VIEN', N'HOAT_DONG', N'gv003@ptit.edu.vn');

-- Sinh vien sv001..sv015 (sv013 BI_KHOA)
DECLARE @i INT = 1;
WHILE @i <= 15
BEGIN
    DECLARE @uname NVARCHAR(50) = N'sv' + RIGHT(N'000' + CAST(@i AS NVARCHAR(10)), 3);
    DECLARE @tt NVARCHAR(20) = CASE WHEN @i = 13 THEN N'BI_KHOA' ELSE N'HOAT_DONG' END;
    INSERT INTO dbo.tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro, trang_thai, email)
    VALUES (@uname, @H_123456, N'SINH_VIEN', @tt, @uname + N'@student.ptit.edu.vn');
    SET @i += 1;
END;
GO

/* ============================================================
   PHAN B: GIANG VIEN
   gv001 (co lop), gv002 (khong co lop), gv003 (lop dong dot dk)
   ============================================================ */
INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV001', N'Tran Van A', N'gv001@ptit.edu.vn', N'Cong nghe phan mem', N'Tien si'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv001';
INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV002', N'Le Thi B', N'gv002@ptit.edu.vn', N'He thong thong tin', N'Thac si'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv002';
INSERT INTO dbo.giang_vien (ma_tai_khoan, ma_so_giang_vien, ho_ten, email, khoa_bo_mon, hoc_vi)
SELECT ma_tai_khoan, N'GV003', N'Pham Van C', N'gv003@ptit.edu.vn', N'Mang may tinh', N'Tien si'
FROM dbo.tai_khoan WHERE ten_dang_nhap = N'gv003';
GO

/* ============================================================
   PHAN C: SINH VIEN (ho so) sv001..sv015
   ============================================================ */
INSERT INTO dbo.sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, email, lop_sinh_hoat, khoa_hoc, nganh, trang_thai)
SELECT
    tk.ma_tai_khoan,
    UPPER(tk.ten_dang_nhap),
    N'Sinh Vien ' + RIGHT(tk.ten_dang_nhap, 3),
    tk.email,
    N'D21CQCN01',
    N'2021',
    N'Cong nghe thong tin',
    CASE WHEN tk.trang_thai = N'BI_KHOA' THEN N'DANG_HOC' ELSE N'DANG_HOC' END
FROM dbo.tai_khoan tk
WHERE tk.vai_tro = N'SINH_VIEN';
GO

/* ============================================================
   PHAN: MON HOC + HOC KY
   3 trang thai hoc ky: DANG_MO, DA_DONG, NHAP
   ============================================================ */
INSERT INTO dbo.mon_hoc (ma_mon_hoc_he_thong, ten_mon_hoc, so_tin_chi, mo_ta)
VALUES
(N'INT1234', N'Phat trien ung dung', 3, N'Mon hoc do an chuyen nganh'),
(N'INT5678', N'Co so du lieu nang cao', 3, N'Mon hoc co so du lieu');

INSERT INTO dbo.hoc_ky (ma_hoc_ky_he_thong, ten_hoc_ky, nam_hoc, ngay_bat_dau, ngay_ket_thuc, trang_thai)
VALUES
(N'HK1_2025', N'Hoc ky 1', N'2025-2026', '2025-09-01', '2026-01-15', N'DANG_MO'),
(N'HK2_2024', N'Hoc ky 2', N'2024-2025', '2025-02-01', '2025-06-15', N'DA_DONG'),
(N'HK1_2026_NHAP', N'Hoc ky 1 (nhap)', N'2026-2027', NULL, NULL, N'NHAP');
GO

/* ============================================================
   PHAN D: LOP HOC PHAN
   LHP001 gv001 TU_DANG_KY  - dot dk dang mo
   LHP002 gv001 TU_DANG_KY  - dot dk da dong
   LHP003 gv001 TU_DANG_KY  - chua mo dot dk
   LHP004 gv003 TU_DANG_KY  - tat ca de tai da day, dot dk da dong
   LHP005 gv001 GIANG_VIEN_PHAN_CONG - de tai che do phan cong
   ============================================================ */
DECLARE @mh1 INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'INT1234');
DECLARE @mh2 INT = (SELECT ma_mon_hoc FROM dbo.mon_hoc WHERE ma_mon_hoc_he_thong = N'INT5678');
DECLARE @hk1 INT = (SELECT ma_hoc_ky FROM dbo.hoc_ky WHERE ma_hoc_ky_he_thong = N'HK1_2025');
DECLARE @gv001 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @gv003 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');

INSERT INTO dbo.lop_hoc_phan (ma_lop, ten_lop_hoc_phan, ma_mon_hoc, ma_hoc_ky, ma_giang_vien, si_so_toi_da, che_do_phan_cong, trang_thai)
VALUES
(N'LHP001', N'Phat trien ung dung - Nhom 01', @mh1, @hk1, @gv001, 40, N'SINH_VIEN_TU_DANG_KY', N'DANG_MO'),
(N'LHP002', N'Phat trien ung dung - Nhom 02', @mh1, @hk1, @gv001, 40, N'SINH_VIEN_TU_DANG_KY', N'DANG_MO'),
(N'LHP003', N'Co so du lieu - Nhom 01',      @mh2, @hk1, @gv001, 40, N'SINH_VIEN_TU_DANG_KY', N'DANG_MO'),
(N'LHP004', N'Co so du lieu - Nhom 02',      @mh2, @hk1, @gv003, 40, N'SINH_VIEN_TU_DANG_KY', N'DANG_MO'),
(N'LHP005', N'Phat trien ung dung - Nhom 03',@mh1, @hk1, @gv001, 40, N'GIANG_VIEN_PHAN_CONG', N'DANG_MO');
GO

/* ============================================================
   PHAN: SINH VIEN VAO LOP (sinh_vien_lop)
   sv014, sv015 KHONG thuoc lop nao.
   ============================================================ */
DECLARE @LHP001 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP001');
DECLARE @LHP002 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP002');
DECLARE @LHP003 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP003');
DECLARE @LHP004 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP004');
DECLARE @LHP005 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP005');

-- LHP001: sv001..sv010
INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
SELECT @LHP001, sv.ma_sinh_vien FROM dbo.sinh_vien sv
WHERE sv.ma_so_sinh_vien IN (N'SV001',N'SV002',N'SV003',N'SV004',N'SV005',N'SV006',N'SV007',N'SV008',N'SV009',N'SV010');

-- LHP002: sv001..sv003 (de test cong da dong)
INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
SELECT @LHP002, sv.ma_sinh_vien FROM dbo.sinh_vien sv
WHERE sv.ma_so_sinh_vien IN (N'SV001',N'SV002',N'SV003');

-- LHP003: sv006 (de test chua mo dot dk)
INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
SELECT @LHP003, sv.ma_sinh_vien FROM dbo.sinh_vien sv
WHERE sv.ma_so_sinh_vien IN (N'SV006');

-- LHP004: sv001..sv006 (de lap day cac de tai)
INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
SELECT @LHP004, sv.ma_sinh_vien FROM dbo.sinh_vien sv
WHERE sv.ma_so_sinh_vien IN (N'SV001',N'SV002',N'SV003',N'SV004',N'SV005',N'SV006');

-- LHP005: sv011, sv012 (de phan cong thu cong)
INSERT INTO dbo.sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
SELECT @LHP005, sv.ma_sinh_vien FROM dbo.sinh_vien sv
WHERE sv.ma_so_sinh_vien IN (N'SV011',N'SV012');
GO

/* ============================================================
   PHAN E: NGAN HANG DE TAI
   gv001: DT001..DT006 (DT001-004 -> LHP001, DT005 -> LHP005, DT006 chua giao)
   gv002: DT101, DT102 (chua giao lop nao)
   gv003: DT201..DT203 (giao het vao LHP004, day het)
   ============================================================ */
DECLARE @gv001b INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @gv002b INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV002');
DECLARE @gv003b INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');

INSERT INTO dbo.ngan_hang_de_tai (ma_de_tai_he_thong, ten_de_tai, mo_ta, yeu_cau, so_luong_mac_dinh, ma_giang_vien_tao)
VALUES
(N'DT001', N'Website quan ly thu vien', N'Xay dung web quan ly muon tra sach', N'Java, SQL', 3, @gv001b),
(N'DT002', N'Ung dung di dong dat do an', N'App dat mon an', N'Flutter', 3, @gv001b),
(N'DT003', N'He thong diem danh khuon mat', N'Nhan dien khuon mat', N'Python, OpenCV', 2, @gv001b),
(N'DT004', N'Chatbot tu van tuyen sinh', N'Chatbot AI', N'NLP', 2, @gv001b),
(N'DT005', N'Cong cu phan tich log', N'Phan tich log he thong', N'ELK', 3, @gv001b),
(N'DT006', N'De tai chua giao lop nao', N'Dung de test xoa de tai', N'Khong', 3, @gv001b),
(N'DT101', N'Kho du lieu ban hang', N'Data warehouse', N'SSIS', 3, @gv002b),
(N'DT102', N'Truc quan hoa du lieu', N'Power BI dashboard', N'Power BI', 3, @gv002b),
(N'DT201', N'Toi uu truy van SQL', N'Index tuning', N'SQL Server', 2, @gv003b),
(N'DT202', N'Sao luu va phuc hoi CSDL', N'Backup strategy', N'SQL Server', 2, @gv003b),
(N'DT203', N'Bao mat CSDL', N'Database security', N'SQL Server', 2, @gv003b);
GO

/* ============================================================
   PHAN F: DE TAI TRONG LOP (de_tai_lop)
   ============================================================ */
DECLARE @LHP001 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP001');
DECLARE @LHP004 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP004');
DECLARE @LHP005 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP005');

-- LHP001 (TU_DANG_KY): dt_lop_001 (3), dt_lop_002 (3), dt_lop_003 (2), dt_lop_004 (2)
INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @LHP001, ndt.ma_de_tai,
       CASE ndt.ma_de_tai_he_thong WHEN N'DT001' THEN 3 WHEN N'DT002' THEN 3 WHEN N'DT003' THEN 2 WHEN N'DT004' THEN 2 END,
       N'SINH_VIEN_TU_DANG_KY'
FROM dbo.ngan_hang_de_tai ndt
WHERE ndt.ma_de_tai_he_thong IN (N'DT001',N'DT002',N'DT003',N'DT004');

-- LHP005 (PHAN_CONG): dt_lop_005 = DT005 (3)
INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @LHP005, ndt.ma_de_tai, 3, N'GIANG_VIEN_PHAN_CONG'
FROM dbo.ngan_hang_de_tai ndt WHERE ndt.ma_de_tai_he_thong = N'DT005';

-- LHP004 (gv003): DT201, DT202, DT203 moi de tai toi da 2
INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @LHP004, ndt.ma_de_tai, 2, N'SINH_VIEN_TU_DANG_KY'
FROM dbo.ngan_hang_de_tai ndt WHERE ndt.ma_de_tai_he_thong IN (N'DT201',N'DT202',N'DT203');
GO

/* ============================================================
   MO DOT DANG KY cho LHP001 (dang mo) bang stored procedure.
   ============================================================ */
DECLARE @LHP001 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP001');
DECLARE @gv001 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
EXEC dbo.sp_mo_cong_dang_ky
     @ma_lop_hoc_phan = @LHP001,
     @ma_giang_vien   = @gv001,
     @thoi_gian_bat_dau = '2026-06-27 00:00:00',
     @thoi_gian_ket_thuc = '2026-07-31 23:59:00',
     @ghi_chu = N'Dot dang ky chinh - dang mo';
GO

/* ============================================================
   PHAN G: DANG KY + LICH SU (qua stored procedure de sinh lich su)
   LHP001 (dot dang mo):
     - sv001: dang ky DT002 -> huy -> dang ky DT001  (doi de tai 1 lan)
     - sv002: dang ky DT002 -> huy -> dang ky DT002  (huy roi dang ky lai)
     - sv003: dang ky DT002
     - sv004: dang ky DT003
     - sv005: dang ky DT003  -> dt_lop_003 day (2/2)
   sv006..sv010: chua dang ky
   ============================================================ */
DECLARE @LHP001 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP001');
DECLARE @sv001 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV001');
DECLARE @sv002 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV002');
DECLARE @sv003 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV003');
DECLARE @sv004 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV004');
DECLARE @sv005 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV005');

DECLARE @dtl001 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP001 AND n.ma_de_tai_he_thong=N'DT001');
DECLARE @dtl002 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP001 AND n.ma_de_tai_he_thong=N'DT002');
DECLARE @dtl003 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP001 AND n.ma_de_tai_he_thong=N'DT003');

-- sv001: doi de tai DT002 -> DT001
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv001, @ma_de_tai_lop=@dtl002;
EXEC dbo.sp_huy_dang_ky_de_tai @ma_sinh_vien=@sv001, @ma_lop_hoc_phan=@LHP001, @ly_do=N'Doi sang de tai khac';
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv001, @ma_de_tai_lop=@dtl001;

-- sv002: huy roi dang ky lai cung DT002
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv002, @ma_de_tai_lop=@dtl002;
EXEC dbo.sp_huy_dang_ky_de_tai @ma_sinh_vien=@sv002, @ma_lop_hoc_phan=@LHP001, @ly_do=N'Doi y, dang ky lai';
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv002, @ma_de_tai_lop=@dtl002;

-- sv003 -> DT002 ; sv004, sv005 -> DT003 (day)
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv003, @ma_de_tai_lop=@dtl002;
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv004, @ma_de_tai_lop=@dtl003;
EXEC dbo.sp_dang_ky_de_tai @ma_sinh_vien=@sv005, @ma_de_tai_lop=@dtl003;
GO

/* ============================================================
   LHP005 (PHAN_CONG): gv001 phan cong thu cong sv011, sv012 -> DT005
   ============================================================ */
DECLARE @gv001 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
DECLARE @LHP005 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP005');
DECLARE @sv011 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV011');
DECLARE @sv012 INT = (SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien = N'SV012');
DECLARE @dtl005 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP005 AND n.ma_de_tai_he_thong=N'DT005');
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv001, @ma_sinh_vien=@sv011, @ma_de_tai_lop=@dtl005, @ghi_chu=N'Phan cong mau';
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv001, @ma_sinh_vien=@sv012, @ma_de_tai_lop=@dtl005, @ghi_chu=N'Phan cong mau';
GO

/* ============================================================
   LHP004 (gv003): lap day het cac de tai bang phan cong thu cong,
   sau do tao dot dang ky DA_DONG.
   DT201: sv001, sv002 ; DT202: sv003, sv004 ; DT203: sv005, sv006
   ============================================================ */
DECLARE @gv003 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV003');
DECLARE @LHP004 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP004');
DECLARE @d201 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP004 AND n.ma_de_tai_he_thong=N'DT201');
DECLARE @d202 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP004 AND n.ma_de_tai_he_thong=N'DT202');
DECLARE @d203 INT = (SELECT dtl.ma_de_tai_lop FROM dbo.de_tai_lop dtl JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai=dtl.ma_de_tai WHERE dtl.ma_lop_hoc_phan=@LHP004 AND n.ma_de_tai_he_thong=N'DT203');
DECLARE @s1 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV001');
DECLARE @s2 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV002');
DECLARE @s3 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV003');
DECLARE @s4 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV004');
DECLARE @s5 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV005');
DECLARE @s6 INT=(SELECT ma_sinh_vien FROM dbo.sinh_vien WHERE ma_so_sinh_vien=N'SV006');
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s1, @ma_de_tai_lop=@d201;
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s2, @ma_de_tai_lop=@d201;
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s3, @ma_de_tai_lop=@d202;
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s4, @ma_de_tai_lop=@d202;
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s5, @ma_de_tai_lop=@d203;
EXEC dbo.sp_phan_cong_thu_cong @ma_giang_vien=@gv003, @ma_sinh_vien=@s6, @ma_de_tai_lop=@d203;

-- Tao dot dang ky da dong cho LHP004
INSERT INTO dbo.dot_dang_ky (ma_lop_hoc_phan, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, ma_giang_vien_tao, ghi_chu)
VALUES (@LHP004, '2025-05-01 00:00:00', '2025-05-10 23:59:00', N'DA_DONG', @gv003, N'Dot dang ky da ket thuc');
GO

/* ============================================================
   LHP002: dot dang ky DA_DONG (de test SV dang ky khi cong dong)
   ============================================================ */
DECLARE @LHP002 INT = (SELECT ma_lop_hoc_phan FROM dbo.lop_hoc_phan WHERE ma_lop = N'LHP002');
DECLARE @gv001 INT = (SELECT ma_giang_vien FROM dbo.giang_vien WHERE ma_so_giang_vien = N'GV001');
-- LHP002 can co de tai truoc khi tao dot (rang buoc nghiep vu thuc te), them 1 de tai
INSERT INTO dbo.de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, che_do_phan_cong)
SELECT @LHP002, ndt.ma_de_tai, 3, N'SINH_VIEN_TU_DANG_KY'
FROM dbo.ngan_hang_de_tai ndt WHERE ndt.ma_de_tai_he_thong = N'DT006';
INSERT INTO dbo.dot_dang_ky (ma_lop_hoc_phan, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, ma_giang_vien_tao, ghi_chu)
VALUES (@LHP002, '2025-05-01 00:00:00', '2025-05-10 23:59:00', N'DA_DONG', @gv001, N'Cong dang ky da dong');
GO

/* ============================================================
   PHAN H: NHAT KY HE THONG (10+ ban ghi)
   ============================================================ */
DECLARE @admin INT = (SELECT ma_tai_khoan FROM dbo.tai_khoan WHERE ten_dang_nhap = N'admin');
INSERT INTO dbo.nhat_ky_he_thong (ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung)
VALUES
(@admin, N'Dang nhap', N'LOGIN', N'tai_khoan', N'Admin dang nhap he thong'),
(@admin, N'Quan ly tai khoan', N'CREATE', N'tai_khoan', N'Tao tai khoan gv001'),
(@admin, N'Quan ly tai khoan', N'CREATE', N'tai_khoan', N'Tao tai khoan gv002'),
(@admin, N'Quan ly tai khoan', N'LOCK',   N'tai_khoan', N'Khoa tai khoan sv013'),
(@admin, N'Quan ly hoc ky', N'CREATE', N'hoc_ky', N'Tao hoc ky HK1_2025'),
(@admin, N'Quan ly hoc ky', N'UPDATE', N'hoc_ky', N'Mo hoc ky HK1_2025'),
(@admin, N'Quan ly mon hoc', N'CREATE', N'mon_hoc', N'Them mon INT1234'),
(@admin, N'Quan ly lop hoc phan', N'CREATE', N'lop_hoc_phan', N'Tao lop LHP001'),
(@admin, N'Quan ly lop hoc phan', N'ASSIGN', N'lop_hoc_phan', N'Gan gv001 cho LHP001'),
(@admin, N'Import sinh vien', N'IMPORT', N'sinh_vien', N'Import 15 sinh vien'),
(@admin, N'Bao cao', N'VIEW', N'vw_thong_ke_lop_hoc_phan', N'Xem bao cao tong quan');
GO

/* ============================================================
   XAC NHAN SO LUONG BAN GHI
   ============================================================ */
SELECT 'tai_khoan' AS bang, COUNT(*) AS so_ban_ghi FROM dbo.tai_khoan
UNION ALL SELECT 'giang_vien', COUNT(*) FROM dbo.giang_vien
UNION ALL SELECT 'sinh_vien', COUNT(*) FROM dbo.sinh_vien
UNION ALL SELECT 'mon_hoc', COUNT(*) FROM dbo.mon_hoc
UNION ALL SELECT 'hoc_ky', COUNT(*) FROM dbo.hoc_ky
UNION ALL SELECT 'lop_hoc_phan', COUNT(*) FROM dbo.lop_hoc_phan
UNION ALL SELECT 'sinh_vien_lop', COUNT(*) FROM dbo.sinh_vien_lop
UNION ALL SELECT 'ngan_hang_de_tai', COUNT(*) FROM dbo.ngan_hang_de_tai
UNION ALL SELECT 'de_tai_lop', COUNT(*) FROM dbo.de_tai_lop
UNION ALL SELECT 'dot_dang_ky', COUNT(*) FROM dbo.dot_dang_ky
UNION ALL SELECT 'dang_ky_de_tai', COUNT(*) FROM dbo.dang_ky_de_tai
UNION ALL SELECT 'lich_su_dang_ky', COUNT(*) FROM dbo.lich_su_dang_ky
UNION ALL SELECT 'nhat_ky_he_thong', COUNT(*) FROM dbo.nhat_ky_he_thong;
GO

-- Trang thai de tai trong LHP001 (kiem tra trigger cap nhat so_luong)
SELECT lhp.ma_lop, n.ma_de_tai_he_thong, dtl.so_luong_hien_tai, dtl.so_luong_toi_da, dtl.trang_thai
FROM dbo.de_tai_lop dtl
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
JOIN dbo.ngan_hang_de_tai n ON n.ma_de_tai = dtl.ma_de_tai
ORDER BY lhp.ma_lop, n.ma_de_tai_he_thong;
GO
