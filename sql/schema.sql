/*
    Database: phan_cong_de_tai_db
    He quan tri: Microsoft SQL Server
    De tai: Xay dung ung dung phan cong de tai cho sinh vien
*/

IF DB_ID(N'phan_cong_de_tai_db') IS NULL
BEGIN
    CREATE DATABASE phan_cong_de_tai_db;
END
GO

USE phan_cong_de_tai_db;
GO

/* =========================
   XOA DOI TUONG CU NEU CO
   ========================= */
IF OBJECT_ID(N'vw_bao_cao_nhom_de_tai', N'V') IS NOT NULL
    DROP VIEW vw_bao_cao_nhom_de_tai;
GO

IF OBJECT_ID(N'trg_dang_ky_de_tai_insert', N'TR') IS NOT NULL
    DROP TRIGGER trg_dang_ky_de_tai_insert;
GO

IF OBJECT_ID(N'trg_dang_ky_de_tai_delete', N'TR') IS NOT NULL
    DROP TRIGGER trg_dang_ky_de_tai_delete;
GO

IF OBJECT_ID(N'dang_ky_de_tai', N'U') IS NOT NULL DROP TABLE dang_ky_de_tai;
IF OBJECT_ID(N'lich_su_dang_ky', N'U') IS NOT NULL DROP TABLE lich_su_dang_ky;
IF OBJECT_ID(N'dot_dang_ky', N'U') IS NOT NULL DROP TABLE dot_dang_ky;
IF OBJECT_ID(N'de_tai_lop', N'U') IS NOT NULL DROP TABLE de_tai_lop;
IF OBJECT_ID(N'ngan_hang_de_tai', N'U') IS NOT NULL DROP TABLE ngan_hang_de_tai;
IF OBJECT_ID(N'sinh_vien_lop', N'U') IS NOT NULL DROP TABLE sinh_vien_lop;
IF OBJECT_ID(N'lop_hoc_phan', N'U') IS NOT NULL DROP TABLE lop_hoc_phan;
IF OBJECT_ID(N'hoc_ky', N'U') IS NOT NULL DROP TABLE hoc_ky;
IF OBJECT_ID(N'mon_hoc', N'U') IS NOT NULL DROP TABLE mon_hoc;
IF OBJECT_ID(N'sinh_vien', N'U') IS NOT NULL DROP TABLE sinh_vien;
IF OBJECT_ID(N'giang_vien', N'U') IS NOT NULL DROP TABLE giang_vien;
IF OBJECT_ID(N'nhat_ky_he_thong', N'U') IS NOT NULL DROP TABLE nhat_ky_he_thong;
IF OBJECT_ID(N'tai_khoan', N'U') IS NOT NULL DROP TABLE tai_khoan;
GO

/* =========================
   BANG TAI KHOAN VA NGUOI DUNG
   ========================= */
CREATE TABLE tai_khoan (
    ma_tai_khoan INT IDENTITY(1,1) PRIMARY KEY,
    ten_dang_nhap NVARCHAR(50) NOT NULL UNIQUE,
    mat_khau_ma_hoa NVARCHAR(255) NOT NULL,
    vai_tro NVARCHAR(30) NOT NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    ngay_tao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    ngay_cap_nhat DATETIME2 NULL,
    CONSTRAINT ck_tai_khoan_vai_tro CHECK (vai_tro IN (N'Quản trị viên', N'Giảng viên', N'Sinh viên'))
);
GO

CREATE TABLE giang_vien (
    ma_giang_vien INT IDENTITY(1,1) PRIMARY KEY,
    ma_tai_khoan INT NOT NULL UNIQUE,
    ma_can_bo NVARCHAR(30) NOT NULL UNIQUE,
    ho_ten NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NULL,
    so_dien_thoai NVARCHAR(20) NULL,
    bo_mon NVARCHAR(100) NULL,
    CONSTRAINT fk_giang_vien_tai_khoan
        FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(ma_tai_khoan)
);
GO

CREATE TABLE sinh_vien (
    ma_sinh_vien INT IDENTITY(1,1) PRIMARY KEY,
    ma_tai_khoan INT NOT NULL UNIQUE,
    ma_so_sinh_vien NVARCHAR(30) NOT NULL UNIQUE,
    ho_ten NVARCHAR(100) NOT NULL,
    lop_hanh_chinh NVARCHAR(50) NULL,
    email NVARCHAR(100) NULL,
    so_dien_thoai NVARCHAR(20) NULL,
    CONSTRAINT fk_sinh_vien_tai_khoan
        FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(ma_tai_khoan)
);
GO

/* =========================
   DANH MUC MON HOC, HOC KY, LOP HOC PHAN
   ========================= */
CREATE TABLE mon_hoc (
    ma_mon_hoc INT IDENTITY(1,1) PRIMARY KEY,
    ma_mon NVARCHAR(30) NOT NULL UNIQUE,
    ten_mon NVARCHAR(150) NOT NULL,
    so_tin_chi INT NOT NULL DEFAULT 3,
    mo_ta NVARCHAR(500) NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    CONSTRAINT ck_mon_hoc_so_tin_chi CHECK (so_tin_chi > 0)
);
GO

CREATE TABLE hoc_ky (
    ma_hoc_ky INT IDENTITY(1,1) PRIMARY KEY,
    ma_ky NVARCHAR(30) NOT NULL UNIQUE,
    ten_hoc_ky NVARCHAR(100) NOT NULL,
    nam_hoc NVARCHAR(20) NOT NULL,
    trang_thai BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE lop_hoc_phan (
    ma_lop_hoc_phan INT IDENTITY(1,1) PRIMARY KEY,
    ma_lop NVARCHAR(50) NOT NULL UNIQUE,
    ten_lop NVARCHAR(150) NOT NULL,
    ma_mon_hoc INT NOT NULL,
    ma_hoc_ky INT NOT NULL,
    ma_giang_vien INT NOT NULL,
    trang_thai NVARCHAR(30) NOT NULL DEFAULT N'Đang học',
    ngay_tao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_lop_mon_hoc
        FOREIGN KEY (ma_mon_hoc) REFERENCES mon_hoc(ma_mon_hoc),
    CONSTRAINT fk_lop_hoc_ky
        FOREIGN KEY (ma_hoc_ky) REFERENCES hoc_ky(ma_hoc_ky),
    CONSTRAINT fk_lop_giang_vien
        FOREIGN KEY (ma_giang_vien) REFERENCES giang_vien(ma_giang_vien),
    CONSTRAINT ck_lop_trang_thai CHECK (trang_thai IN (N'Đang học', N'Đã kết thúc', N'Tạm khóa'))
);
GO

CREATE TABLE sinh_vien_lop (
    ma_lop_hoc_phan INT NOT NULL,
    ma_sinh_vien INT NOT NULL,
    ngay_tham_gia DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    trang_thai BIT NOT NULL DEFAULT 1,
    CONSTRAINT pk_sinh_vien_lop PRIMARY KEY (ma_lop_hoc_phan, ma_sinh_vien),
    CONSTRAINT fk_svl_lop
        FOREIGN KEY (ma_lop_hoc_phan) REFERENCES lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT fk_svl_sinh_vien
        FOREIGN KEY (ma_sinh_vien) REFERENCES sinh_vien(ma_sinh_vien)
);
GO

/* =========================
   DE TAI VA DOT DANG KY
   ========================= */
CREATE TABLE ngan_hang_de_tai (
    ma_de_tai INT IDENTITY(1,1) PRIMARY KEY,
    ma_giang_vien INT NOT NULL,
    ma_de_tai_hien_thi NVARCHAR(30) NOT NULL,
    ten_de_tai NVARCHAR(255) NOT NULL,
    mo_ta_yeu_cau NVARCHAR(MAX) NULL,
    so_luong_mac_dinh INT NOT NULL DEFAULT 3,
    ghi_chu NVARCHAR(500) NULL,
    trang_thai BIT NOT NULL DEFAULT 1,
    ngay_tao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_de_tai_giang_vien
        FOREIGN KEY (ma_giang_vien) REFERENCES giang_vien(ma_giang_vien),
    CONSTRAINT uq_de_tai_theo_giang_vien UNIQUE (ma_giang_vien, ma_de_tai_hien_thi),
    CONSTRAINT ck_de_tai_so_luong_mac_dinh CHECK (so_luong_mac_dinh > 0)
);
GO

CREATE TABLE de_tai_lop (
    ma_de_tai_lop INT IDENTITY(1,1) PRIMARY KEY,
    ma_lop_hoc_phan INT NOT NULL,
    ma_de_tai INT NOT NULL,
    so_luong_toi_da INT NOT NULL,
    so_luong_hien_tai INT NOT NULL DEFAULT 0,
    trang_thai NVARCHAR(30) NOT NULL DEFAULT N'Mở',
    hinh_thuc_phan_cong NVARCHAR(30) NOT NULL DEFAULT N'Sinh viên đăng ký',
    ngay_gan DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_de_tai_lop_lop
        FOREIGN KEY (ma_lop_hoc_phan) REFERENCES lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT fk_de_tai_lop_de_tai
        FOREIGN KEY (ma_de_tai) REFERENCES ngan_hang_de_tai(ma_de_tai),
    CONSTRAINT uq_de_tai_lop UNIQUE (ma_lop_hoc_phan, ma_de_tai),
    CONSTRAINT ck_de_tai_lop_so_luong_toi_da CHECK (so_luong_toi_da > 0),
    CONSTRAINT ck_de_tai_lop_so_luong_hien_tai CHECK (so_luong_hien_tai >= 0 AND so_luong_hien_tai <= so_luong_toi_da),
    CONSTRAINT ck_de_tai_lop_trang_thai CHECK (trang_thai IN (N'Mở', N'Đã đủ', N'Đã đóng')),
    CONSTRAINT ck_de_tai_lop_hinh_thuc CHECK (hinh_thuc_phan_cong IN (N'Giảng viên phân công', N'Sinh viên đăng ký'))
);
GO

CREATE TABLE dot_dang_ky (
    ma_dot_dang_ky INT IDENTITY(1,1) PRIMARY KEY,
    ma_lop_hoc_phan INT NOT NULL UNIQUE,
    thoi_gian_bat_dau DATETIME2 NOT NULL,
    thoi_gian_ket_thuc DATETIME2 NOT NULL,
    trang_thai NVARCHAR(30) NOT NULL DEFAULT N'Nháp',
    ma_giang_vien_tao INT NOT NULL,
    ngay_tao DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_dot_lop
        FOREIGN KEY (ma_lop_hoc_phan) REFERENCES lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT fk_dot_giang_vien
        FOREIGN KEY (ma_giang_vien_tao) REFERENCES giang_vien(ma_giang_vien),
    CONSTRAINT ck_dot_thoi_gian CHECK (thoi_gian_bat_dau < thoi_gian_ket_thuc),
    CONSTRAINT ck_dot_trang_thai CHECK (trang_thai IN (N'Nháp', N'Mở', N'Đã đóng'))
);
GO

/* =========================
   DANG KY / PHAN CONG DE TAI
   ========================= */
CREATE TABLE dang_ky_de_tai (
    ma_dang_ky INT IDENTITY(1,1) PRIMARY KEY,
    ma_lop_hoc_phan INT NOT NULL,
    ma_sinh_vien INT NOT NULL,
    ma_de_tai_lop INT NOT NULL,
    thoi_gian_dang_ky DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    hinh_thuc NVARCHAR(30) NOT NULL,
    ghi_chu NVARCHAR(500) NULL,
    CONSTRAINT fk_dk_lop
        FOREIGN KEY (ma_lop_hoc_phan) REFERENCES lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT fk_dk_sinh_vien
        FOREIGN KEY (ma_sinh_vien) REFERENCES sinh_vien(ma_sinh_vien),
    CONSTRAINT fk_dk_de_tai_lop
        FOREIGN KEY (ma_de_tai_lop) REFERENCES de_tai_lop(ma_de_tai_lop),
    CONSTRAINT uq_mot_sinh_vien_mot_de_tai_trong_lop UNIQUE (ma_lop_hoc_phan, ma_sinh_vien),
    CONSTRAINT ck_dk_hinh_thuc CHECK (hinh_thuc IN (N'Tự đăng ký', N'Giảng viên gán', N'Tự động'))
);
GO

CREATE TABLE lich_su_dang_ky (
    ma_lich_su INT IDENTITY(1,1) PRIMARY KEY,
    ma_lop_hoc_phan INT NOT NULL,
    ma_sinh_vien INT NOT NULL,
    ma_de_tai_lop INT NULL,
    hanh_dong NVARCHAR(30) NOT NULL,
    thoi_gian_thuc_hien DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    ly_do NVARCHAR(500) NULL,
    CONSTRAINT ck_ls_hanh_dong CHECK (hanh_dong IN (N'Đăng ký', N'Hủy đăng ký', N'Phân công thủ công', N'Phân công tự động')),
    CONSTRAINT fk_ls_lop
        FOREIGN KEY (ma_lop_hoc_phan) REFERENCES lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT fk_ls_sinh_vien
        FOREIGN KEY (ma_sinh_vien) REFERENCES sinh_vien(ma_sinh_vien),
    CONSTRAINT fk_ls_de_tai_lop
        FOREIGN KEY (ma_de_tai_lop) REFERENCES de_tai_lop(ma_de_tai_lop)
);
GO

CREATE TABLE nhat_ky_he_thong (
    ma_nhat_ky INT IDENTITY(1,1) PRIMARY KEY,
    ma_tai_khoan INT NULL,
    hanh_dong NVARCHAR(100) NOT NULL,
    ten_bang NVARCHAR(100) NULL,
    khoa_ban_ghi NVARCHAR(100) NULL,
    noi_dung NVARCHAR(MAX) NULL,
    thoi_gian_thuc_hien DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT fk_nhat_ky_tai_khoan
        FOREIGN KEY (ma_tai_khoan) REFERENCES tai_khoan(ma_tai_khoan)
);
GO

/* =========================
   TRIGGER NGHIEP VU
   ========================= */
CREATE TRIGGER trg_dang_ky_de_tai_insert
ON dang_ky_de_tai
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Kiem tra de tai phai thuoc dung lop hoc phan
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN de_tai_lop dtl ON i.ma_de_tai_lop = dtl.ma_de_tai_lop
        WHERE i.ma_lop_hoc_phan <> dtl.ma_lop_hoc_phan
    )
    BEGIN
        RAISERROR(N'Đề tài không thuộc lớp học phần đã chọn.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Kiem tra sinh vien co nam trong lop hoc phan khong
    IF EXISTS (
        SELECT 1
        FROM inserted i
        LEFT JOIN sinh_vien_lop svl
            ON i.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
           AND i.ma_sinh_vien = svl.ma_sinh_vien
           AND svl.trang_thai = 1
        WHERE svl.ma_sinh_vien IS NULL
    )
    BEGIN
        RAISERROR(N'Sinh viên không thuộc lớp học phần này.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Neu sinh vien tu dang ky thi phai trong thoi gian mo cong
    IF EXISTS (
        SELECT 1
        FROM inserted i
        LEFT JOIN dot_dang_ky ddk
            ON i.ma_lop_hoc_phan = ddk.ma_lop_hoc_phan
           AND ddk.trang_thai = N'Mở'
        WHERE i.hinh_thuc = N'Tự đăng ký'
          AND (
              ddk.ma_dot_dang_ky IS NULL
              OR SYSDATETIME() < ddk.thoi_gian_bat_dau
              OR SYSDATETIME() > ddk.thoi_gian_ket_thuc
          )
    )
    BEGIN
        RAISERROR(N'Cổng đăng ký chưa mở hoặc đã hết hạn.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Kiem tra de tai con cho va chua dong
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN de_tai_lop dtl ON i.ma_de_tai_lop = dtl.ma_de_tai_lop
        GROUP BY i.ma_de_tai_lop, dtl.so_luong_hien_tai, dtl.so_luong_toi_da, dtl.trang_thai
        HAVING dtl.trang_thai = N'Đã đóng'
            OR dtl.so_luong_hien_tai + COUNT(*) > dtl.so_luong_toi_da
    )
    BEGIN
        RAISERROR(N'Đề tài đã đủ số lượng hoặc đã đóng.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    INSERT INTO dang_ky_de_tai (
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        thoi_gian_dang_ky,
        hinh_thuc,
        ghi_chu
    )
    SELECT
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        ISNULL(thoi_gian_dang_ky, SYSDATETIME()),
        hinh_thuc,
        ghi_chu
    FROM inserted;

    UPDATE dtl
    SET
        so_luong_hien_tai = dtl.so_luong_hien_tai + x.so_luong_them,
        trang_thai = CASE
            WHEN dtl.so_luong_hien_tai + x.so_luong_them >= dtl.so_luong_toi_da THEN N'Đã đủ'
            ELSE N'Mở'
        END
    FROM de_tai_lop dtl
    JOIN (
        SELECT ma_de_tai_lop, COUNT(*) AS so_luong_them
        FROM inserted
        GROUP BY ma_de_tai_lop
    ) x ON dtl.ma_de_tai_lop = x.ma_de_tai_lop;

    INSERT INTO lich_su_dang_ky (
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        hanh_dong,
        ly_do
    )
    SELECT
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        CASE
            WHEN hinh_thuc = N'Tự đăng ký' THEN N'Đăng ký'
            WHEN hinh_thuc = N'Giảng viên gán' THEN N'Phân công thủ công'
            ELSE N'Phân công tự động'
        END,
        ghi_chu
    FROM inserted;
END;
GO

CREATE TRIGGER trg_dang_ky_de_tai_delete
ON dang_ky_de_tai
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE dtl
    SET
        so_luong_hien_tai = CASE
            WHEN dtl.so_luong_hien_tai - x.so_luong_xoa < 0 THEN 0
            ELSE dtl.so_luong_hien_tai - x.so_luong_xoa
        END,
        trang_thai = CASE
            WHEN dtl.trang_thai = N'Đã đóng' THEN N'Đã đóng'
            ELSE N'Mở'
        END
    FROM de_tai_lop dtl
    JOIN (
        SELECT ma_de_tai_lop, COUNT(*) AS so_luong_xoa
        FROM deleted
        GROUP BY ma_de_tai_lop
    ) x ON dtl.ma_de_tai_lop = x.ma_de_tai_lop;

    INSERT INTO lich_su_dang_ky (
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        hanh_dong,
        ly_do
    )
    SELECT
        ma_lop_hoc_phan,
        ma_sinh_vien,
        ma_de_tai_lop,
        N'Hủy đăng ký',
        N'Sinh viên hoặc giảng viên hủy đăng ký'
    FROM deleted;
END;
GO

/* =========================
   VIEW BAO CAO
   ========================= */
CREATE VIEW vw_bao_cao_nhom_de_tai AS
SELECT
    lhp.ma_lop,
    lhp.ten_lop,
    mh.ma_mon,
    mh.ten_mon,
    hk.ten_hoc_ky,
    hk.nam_hoc,
    ndt.ma_de_tai_hien_thi,
    ndt.ten_de_tai,
    sv.ma_so_sinh_vien,
    sv.ho_ten AS ho_ten_sinh_vien,
    dk.hinh_thuc,
    dk.thoi_gian_dang_ky
FROM dang_ky_de_tai dk
JOIN lop_hoc_phan lhp ON dk.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
JOIN mon_hoc mh ON lhp.ma_mon_hoc = mh.ma_mon_hoc
JOIN hoc_ky hk ON lhp.ma_hoc_ky = hk.ma_hoc_ky
JOIN sinh_vien sv ON dk.ma_sinh_vien = sv.ma_sinh_vien
JOIN de_tai_lop dtl ON dk.ma_de_tai_lop = dtl.ma_de_tai_lop
JOIN ngan_hang_de_tai ndt ON dtl.ma_de_tai = ndt.ma_de_tai;
GO

/* =========================
   DU LIEU MAU
   Mat khau demo dang de plain text '123456' de tien test.
   Khi trien khai thuc te phai ma hoa mat khau.
   ========================= */
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_ma_hoa, vai_tro)
VALUES
    (N'admin', N'123456', N'Quản trị viên'),
    (N'gv01', N'123456', N'Giảng viên'),
    (N'N23DCCN023', N'123456', N'Sinh viên'),
    (N'N23DCCN030', N'123456', N'Sinh viên'),
    (N'N23DCCN031', N'123456', N'Sinh viên');
GO

INSERT INTO giang_vien (ma_tai_khoan, ma_can_bo, ho_ten, email, bo_mon)
VALUES
    (2, N'GV001', N'Nguyễn Hoàng Thành', N'gv01@ptit.edu.vn', N'Công nghệ phần mềm');
GO

INSERT INTO sinh_vien (ma_tai_khoan, ma_so_sinh_vien, ho_ten, lop_hanh_chinh, email)
VALUES
    (3, N'N23DCCN023', N'Lê Tiến Hưng', N'D23CQCN01-N', N'hung.n23dccn023@ptit.edu.vn'),
    (4, N'N23DCCN030', N'Nguyễn Đăng Khoa', N'D23CQCN01-N', N'khoa.n23dccn030@ptit.edu.vn'),
    (5, N'N23DCCN031', N'Nguyễn Quang Huy', N'D23CQCN01-N', N'huy.n23dccn031@ptit.edu.vn');
GO

INSERT INTO mon_hoc (ma_mon, ten_mon, so_tin_chi, mo_ta)
VALUES
    (N'CNPM', N'Công nghệ phần mềm', 3, N'Môn học về quy trình, phân tích, thiết kế và phát triển phần mềm');
GO

INSERT INTO hoc_ky (ma_ky, ten_hoc_ky, nam_hoc)
VALUES
    (N'HK2_2025_2026', N'Học kỳ 2', N'2025-2026');
GO

INSERT INTO lop_hoc_phan (ma_lop, ten_lop, ma_mon_hoc, ma_hoc_ky, ma_giang_vien)
VALUES
    (N'CNPM_N01', N'Công nghệ phần mềm - Nhóm 01', 1, 1, 1);
GO

INSERT INTO sinh_vien_lop (ma_lop_hoc_phan, ma_sinh_vien)
VALUES
    (1, 1),
    (1, 2),
    (1, 3);
GO

INSERT INTO ngan_hang_de_tai (ma_giang_vien, ma_de_tai_hien_thi, ten_de_tai, mo_ta_yeu_cau, so_luong_mac_dinh)
VALUES
    (1, N'DT001', N'Xây dựng ứng dụng phân công đề tài cho sinh viên', N'Quản lý đề tài, lớp học phần, đăng ký và chốt danh sách nhóm.', 3),
    (1, N'DT002', N'Xây dựng ứng dụng quản lý thư viện', N'Quản lý sách, độc giả, mượn trả và thống kê.', 3),
    (1, N'DT003', N'Xây dựng ứng dụng quản lý phòng máy', N'Quản lý máy tính, lịch sử dụng phòng và sự cố thiết bị.', 3);
GO

INSERT INTO de_tai_lop (ma_lop_hoc_phan, ma_de_tai, so_luong_toi_da, hinh_thuc_phan_cong)
VALUES
    (1, 1, 3, N'Sinh viên đăng ký'),
    (1, 2, 3, N'Sinh viên đăng ký'),
    (1, 3, 3, N'Sinh viên đăng ký');
GO

INSERT INTO dot_dang_ky (ma_lop_hoc_phan, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai, ma_giang_vien_tao)
VALUES
    (1, DATEADD(DAY, -1, SYSDATETIME()), DATEADD(DAY, 7, SYSDATETIME()), N'Mở', 1);
GO

INSERT INTO dang_ky_de_tai (ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop, hinh_thuc, ghi_chu)
VALUES
    (1, 1, 1, N'Tự đăng ký', N'Dữ liệu mẫu'),
    (1, 2, 1, N'Tự đăng ký', N'Dữ liệu mẫu');
GO

SELECT * FROM vw_bao_cao_nhom_de_tai;
GO
