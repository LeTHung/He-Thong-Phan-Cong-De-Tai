USE [master];
GO

IF DB_ID(N'PhanCongDeTai') IS NOT NULL
BEGIN
    ALTER DATABASE [PhanCongDeTai] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [PhanCongDeTai];
END
GO

CREATE DATABASE [PhanCongDeTai];
GO

USE [PhanCongDeTai];
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* ============================================================
   1. CAC BANG DANH MUC VA NGUOI DUNG
   ============================================================ */

CREATE TABLE dbo.tai_khoan (
    ma_tai_khoan       INT IDENTITY(1,1) NOT NULL,
    ten_dang_nhap     NVARCHAR(50) NOT NULL,
    mat_khau_ma_hoa   NVARCHAR(255) NOT NULL,
    vai_tro           NVARCHAR(30) NOT NULL,
    trang_thai        NVARCHAR(20) NOT NULL CONSTRAINT DF_tai_khoan_trang_thai DEFAULT N'HOAT_DONG',
    email             NVARCHAR(100) NULL,
    so_dien_thoai     NVARCHAR(20) NULL,
    anh_dai_dien      NVARCHAR(255) NULL,
    lan_dang_nhap_cuoi DATETIME2(0) NULL,
    thoi_diem_tao     DATETIME2(0) NOT NULL CONSTRAINT DF_tai_khoan_thoi_diem_tao DEFAULT SYSDATETIME(),
    thoi_diem_cap_nhat DATETIME2(0) NULL,

    CONSTRAINT PK_tai_khoan PRIMARY KEY (ma_tai_khoan),
    CONSTRAINT UQ_tai_khoan_ten_dang_nhap UNIQUE (ten_dang_nhap),
    CONSTRAINT CK_tai_khoan_vai_tro CHECK (vai_tro IN (N'QUAN_TRI_VIEN', N'GIANG_VIEN', N'SINH_VIEN')),
    CONSTRAINT CK_tai_khoan_trang_thai CHECK (trang_thai IN (N'HOAT_DONG', N'BI_KHOA'))
);
GO

CREATE TABLE dbo.giang_vien (
    ma_giang_vien        INT IDENTITY(1,1) NOT NULL,
    ma_tai_khoan         INT NOT NULL,
    ma_so_giang_vien     NVARCHAR(30) NOT NULL,
    ho_ten               NVARCHAR(100) NOT NULL,
    email                NVARCHAR(100) NULL,
    so_dien_thoai        NVARCHAR(20) NULL,
    khoa_bo_mon          NVARCHAR(100) NULL,
    hoc_vi               NVARCHAR(50) NULL,
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_giang_vien_trang_thai DEFAULT N'DANG_CONG_TAC',
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_giang_vien_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_giang_vien PRIMARY KEY (ma_giang_vien),
    CONSTRAINT UQ_giang_vien_ma_so UNIQUE (ma_so_giang_vien),
    CONSTRAINT UQ_giang_vien_tai_khoan UNIQUE (ma_tai_khoan),
    CONSTRAINT CK_giang_vien_trang_thai CHECK (trang_thai IN (N'DANG_CONG_TAC', N'TAM_NGHI', N'NGHI_CONG_TAC')),
    CONSTRAINT FK_giang_vien_tai_khoan FOREIGN KEY (ma_tai_khoan)
        REFERENCES dbo.tai_khoan(ma_tai_khoan)
);
GO

CREATE TABLE dbo.sinh_vien (
    ma_sinh_vien        INT IDENTITY(1,1) NOT NULL,
    ma_tai_khoan        INT NOT NULL,
    ma_so_sinh_vien     NVARCHAR(30) NOT NULL,
    ho_ten              NVARCHAR(100) NOT NULL,
    email               NVARCHAR(100) NULL,
    so_dien_thoai       NVARCHAR(20) NULL,
    lop_sinh_hoat       NVARCHAR(50) NULL,
    khoa_hoc            NVARCHAR(20) NULL,
    nganh               NVARCHAR(100) NULL,
    trang_thai          NVARCHAR(20) NOT NULL CONSTRAINT DF_sinh_vien_trang_thai DEFAULT N'DANG_HOC',
    thoi_diem_tao       DATETIME2(0) NOT NULL CONSTRAINT DF_sinh_vien_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_sinh_vien PRIMARY KEY (ma_sinh_vien),
    CONSTRAINT UQ_sinh_vien_ma_so UNIQUE (ma_so_sinh_vien),
    CONSTRAINT UQ_sinh_vien_tai_khoan UNIQUE (ma_tai_khoan),
    CONSTRAINT CK_sinh_vien_trang_thai CHECK (trang_thai IN (N'DANG_HOC', N'TAM_NGHI', N'DA_RA_TRUONG')),
    CONSTRAINT FK_sinh_vien_tai_khoan FOREIGN KEY (ma_tai_khoan)
        REFERENCES dbo.tai_khoan(ma_tai_khoan)
);
GO

CREATE TABLE dbo.mon_hoc (
    ma_mon_hoc            INT IDENTITY(1,1) NOT NULL,
    ma_mon_hoc_he_thong   NVARCHAR(30) NOT NULL,
    ten_mon_hoc           NVARCHAR(150) NOT NULL,
    so_tin_chi            INT NOT NULL,
    mo_ta                 NVARCHAR(500) NULL,
    trang_thai            NVARCHAR(20) NOT NULL CONSTRAINT DF_mon_hoc_trang_thai DEFAULT N'DANG_SU_DUNG',
    thoi_diem_tao         DATETIME2(0) NOT NULL CONSTRAINT DF_mon_hoc_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_mon_hoc PRIMARY KEY (ma_mon_hoc),
    CONSTRAINT UQ_mon_hoc_ma_he_thong UNIQUE (ma_mon_hoc_he_thong),
    CONSTRAINT CK_mon_hoc_so_tin_chi CHECK (so_tin_chi > 0),
    CONSTRAINT CK_mon_hoc_trang_thai CHECK (trang_thai IN (N'DANG_SU_DUNG', N'NGUNG_SU_DUNG'))
);
GO

CREATE TABLE dbo.hoc_ky (
    ma_hoc_ky            INT IDENTITY(1,1) NOT NULL,
    ma_hoc_ky_he_thong   NVARCHAR(30) NOT NULL,
    ten_hoc_ky           NVARCHAR(100) NOT NULL,
    nam_hoc              NVARCHAR(20) NOT NULL,
    ngay_bat_dau         DATE NULL,
    ngay_ket_thuc        DATE NULL,
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_hoc_ky_trang_thai DEFAULT N'DANG_MO',
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_hoc_ky_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_hoc_ky PRIMARY KEY (ma_hoc_ky),
    CONSTRAINT UQ_hoc_ky_ma_he_thong UNIQUE (ma_hoc_ky_he_thong),
    CONSTRAINT CK_hoc_ky_trang_thai CHECK (trang_thai IN (N'NHAP', N'DANG_MO', N'DA_DONG')),
    CONSTRAINT CK_hoc_ky_thoi_gian CHECK (ngay_bat_dau IS NULL OR ngay_ket_thuc IS NULL OR ngay_bat_dau <= ngay_ket_thuc)
);
GO

CREATE TABLE dbo.lop_hoc_phan (
    ma_lop_hoc_phan      INT IDENTITY(1,1) NOT NULL,
    ma_lop               NVARCHAR(50) NOT NULL,
    ten_lop_hoc_phan     NVARCHAR(150) NOT NULL,
    ma_mon_hoc           INT NOT NULL,
    ma_hoc_ky            INT NOT NULL,
    ma_giang_vien        INT NOT NULL,
    si_so_toi_da         INT NULL,
    ghi_chu              NVARCHAR(500) NULL,
    che_do_phan_cong     NVARCHAR(30) NOT NULL CONSTRAINT DF_lop_hoc_phan_che_do DEFAULT N'SINH_VIEN_TU_DANG_KY',
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_lop_hoc_phan_trang_thai DEFAULT N'DANG_MO',
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_lop_hoc_phan_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_lop_hoc_phan PRIMARY KEY (ma_lop_hoc_phan),
    CONSTRAINT UQ_lop_hoc_phan_ma_lop UNIQUE (ma_lop),
    CONSTRAINT CK_lop_hoc_phan_si_so CHECK (si_so_toi_da IS NULL OR si_so_toi_da > 0),
    CONSTRAINT CK_lop_hoc_phan_che_do CHECK (che_do_phan_cong IN (N'GIANG_VIEN_PHAN_CONG', N'SINH_VIEN_TU_DANG_KY')),
    CONSTRAINT CK_lop_hoc_phan_trang_thai CHECK (trang_thai IN (N'DANG_MO', N'DA_DONG', N'LUU_TRU')),
    CONSTRAINT FK_lop_hoc_phan_mon_hoc FOREIGN KEY (ma_mon_hoc)
        REFERENCES dbo.mon_hoc(ma_mon_hoc),
    CONSTRAINT FK_lop_hoc_phan_hoc_ky FOREIGN KEY (ma_hoc_ky)
        REFERENCES dbo.hoc_ky(ma_hoc_ky),
    CONSTRAINT FK_lop_hoc_phan_giang_vien FOREIGN KEY (ma_giang_vien)
        REFERENCES dbo.giang_vien(ma_giang_vien)
);
GO

CREATE TABLE dbo.sinh_vien_lop (
    ma_lop_hoc_phan      INT NOT NULL,
    ma_sinh_vien         INT NOT NULL,
    ngay_tham_gia        DATE NOT NULL CONSTRAINT DF_sinh_vien_lop_ngay_tham_gia DEFAULT CONVERT(DATE, SYSDATETIME()),
    thoi_diem_tham_gia   DATETIME2(0) NOT NULL CONSTRAINT DF_sinh_vien_lop_thoi_diem_tham_gia DEFAULT SYSDATETIME(),
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_sinh_vien_lop_trang_thai DEFAULT N'DANG_HOC',
    ghi_chu              NVARCHAR(500) NULL,

    CONSTRAINT PK_sinh_vien_lop PRIMARY KEY (ma_lop_hoc_phan, ma_sinh_vien),
    CONSTRAINT CK_sinh_vien_lop_trang_thai CHECK (trang_thai IN (N'DANG_HOC', N'DA_RUT')),
    CONSTRAINT FK_sinh_vien_lop_lop FOREIGN KEY (ma_lop_hoc_phan)
        REFERENCES dbo.lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT FK_sinh_vien_lop_sinh_vien FOREIGN KEY (ma_sinh_vien)
        REFERENCES dbo.sinh_vien(ma_sinh_vien)
);
GO

/* ============================================================
   2. CAC BANG DE TAI, DOT DANG KY, KET QUA DANG KY
   ============================================================ */

CREATE TABLE dbo.ngan_hang_de_tai (
    ma_de_tai            INT IDENTITY(1,1) NOT NULL,
    ma_de_tai_he_thong   NVARCHAR(50) NOT NULL,
    ten_de_tai           NVARCHAR(255) NOT NULL,
    mo_ta                NVARCHAR(MAX) NULL,
    yeu_cau              NVARCHAR(MAX) NULL,
    ghi_chu              NVARCHAR(500) NULL,
    so_luong_mac_dinh    INT NOT NULL CONSTRAINT DF_ngan_hang_de_tai_so_luong_mac_dinh DEFAULT 3,
    ma_giang_vien_tao    INT NOT NULL,
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_ngan_hang_de_tai_trang_thai DEFAULT N'DANG_SU_DUNG',
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_ngan_hang_de_tai_thoi_diem_tao DEFAULT SYSDATETIME(),
    thoi_diem_cap_nhat   DATETIME2(0) NULL,

    CONSTRAINT PK_ngan_hang_de_tai PRIMARY KEY (ma_de_tai),
    CONSTRAINT UQ_ngan_hang_de_tai_ma_he_thong UNIQUE (ma_de_tai_he_thong),
    CONSTRAINT CK_ngan_hang_de_tai_so_luong CHECK (so_luong_mac_dinh > 0),
    CONSTRAINT CK_ngan_hang_de_tai_trang_thai CHECK (trang_thai IN (N'DANG_SU_DUNG', N'NGUNG_SU_DUNG')),
    CONSTRAINT FK_ngan_hang_de_tai_giang_vien FOREIGN KEY (ma_giang_vien_tao)
        REFERENCES dbo.giang_vien(ma_giang_vien)
);
GO

CREATE TABLE dbo.de_tai_lop (
    ma_de_tai_lop        INT IDENTITY(1,1) NOT NULL,
    ma_lop_hoc_phan      INT NOT NULL,
    ma_de_tai            INT NOT NULL,
    so_luong_toi_da      INT NOT NULL,
    so_luong_hien_tai    INT NOT NULL CONSTRAINT DF_de_tai_lop_so_luong_hien_tai DEFAULT 0,
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_de_tai_lop_trang_thai DEFAULT N'DANG_MO',
    che_do_phan_cong     NVARCHAR(30) NOT NULL,
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_de_tai_lop_thoi_diem_tao DEFAULT SYSDATETIME(),

    CONSTRAINT PK_de_tai_lop PRIMARY KEY (ma_de_tai_lop),
    CONSTRAINT UQ_de_tai_lop_lop_de_tai UNIQUE (ma_lop_hoc_phan, ma_de_tai),
    CONSTRAINT CK_de_tai_lop_so_luong_toi_da CHECK (so_luong_toi_da > 0),
    CONSTRAINT CK_de_tai_lop_so_luong_hien_tai CHECK (so_luong_hien_tai >= 0 AND so_luong_hien_tai <= so_luong_toi_da),
    CONSTRAINT CK_de_tai_lop_trang_thai CHECK (trang_thai IN (N'DANG_MO', N'DA_DU', N'DA_DONG')),
    CONSTRAINT CK_de_tai_lop_che_do CHECK (che_do_phan_cong IN (N'GIANG_VIEN_PHAN_CONG', N'SINH_VIEN_TU_DANG_KY')),
    CONSTRAINT FK_de_tai_lop_lop FOREIGN KEY (ma_lop_hoc_phan)
        REFERENCES dbo.lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT FK_de_tai_lop_de_tai FOREIGN KEY (ma_de_tai)
        REFERENCES dbo.ngan_hang_de_tai(ma_de_tai)
);
GO

CREATE TRIGGER dbo.trg_de_tai_lop_dong_bo_che_do
ON dbo.de_tai_lop
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    IF TRIGGER_NESTLEVEL() > 1 RETURN;

    UPDATE dtl
    SET che_do_phan_cong = lhp.che_do_phan_cong
    FROM dbo.de_tai_lop dtl
    JOIN inserted i ON i.ma_de_tai_lop = dtl.ma_de_tai_lop
    JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
    WHERE dtl.che_do_phan_cong <> lhp.che_do_phan_cong;
END;
GO

CREATE TABLE dbo.dot_dang_ky (
    ma_dot_dang_ky       INT IDENTITY(1,1) NOT NULL,
    ma_lop_hoc_phan      INT NOT NULL,
    thoi_gian_bat_dau    DATETIME2(0) NOT NULL,
    thoi_gian_ket_thuc   DATETIME2(0) NOT NULL,
    trang_thai           NVARCHAR(20) NOT NULL CONSTRAINT DF_dot_dang_ky_trang_thai DEFAULT N'NHAP',
    ma_giang_vien_tao    INT NOT NULL,
    ghi_chu              NVARCHAR(500) NULL,
    thoi_diem_tao        DATETIME2(0) NOT NULL CONSTRAINT DF_dot_dang_ky_thoi_diem_tao DEFAULT SYSDATETIME(),
    thoi_diem_cap_nhat   DATETIME2(0) NULL,

    CONSTRAINT PK_dot_dang_ky PRIMARY KEY (ma_dot_dang_ky),
    CONSTRAINT UQ_dot_dang_ky_lop UNIQUE (ma_lop_hoc_phan),
    CONSTRAINT CK_dot_dang_ky_trang_thai CHECK (trang_thai IN (N'NHAP', N'DANG_MO', N'DA_DONG')),
    CONSTRAINT CK_dot_dang_ky_thoi_gian CHECK (thoi_gian_bat_dau < thoi_gian_ket_thuc),
    CONSTRAINT FK_dot_dang_ky_lop FOREIGN KEY (ma_lop_hoc_phan)
        REFERENCES dbo.lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT FK_dot_dang_ky_giang_vien FOREIGN KEY (ma_giang_vien_tao)
        REFERENCES dbo.giang_vien(ma_giang_vien)
);
GO

CREATE TABLE dbo.dang_ky_de_tai (
    ma_dang_ky           INT IDENTITY(1,1) NOT NULL,
    ma_lop_hoc_phan      INT NOT NULL,
    ma_sinh_vien         INT NOT NULL,
    ma_de_tai_lop        INT NOT NULL,
    thoi_diem_dang_ky    DATETIME2(0) NOT NULL CONSTRAINT DF_dang_ky_de_tai_thoi_diem DEFAULT SYSDATETIME(),
    hinh_thuc_phan_cong  NVARCHAR(20) NOT NULL,
    ghi_chu              NVARCHAR(500) NULL,

    CONSTRAINT PK_dang_ky_de_tai PRIMARY KEY (ma_dang_ky),
    CONSTRAINT UQ_dang_ky_de_tai_lop_sinh_vien UNIQUE (ma_lop_hoc_phan, ma_sinh_vien),
    CONSTRAINT CK_dang_ky_de_tai_hinh_thuc CHECK (hinh_thuc_phan_cong IN (N'TU_DANG_KY', N'THU_CONG', N'TU_DONG')),
    CONSTRAINT FK_dang_ky_de_tai_lop FOREIGN KEY (ma_lop_hoc_phan)
        REFERENCES dbo.lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT FK_dang_ky_de_tai_sinh_vien FOREIGN KEY (ma_sinh_vien)
        REFERENCES dbo.sinh_vien(ma_sinh_vien),
    CONSTRAINT FK_dang_ky_de_tai_de_tai_lop FOREIGN KEY (ma_de_tai_lop)
        REFERENCES dbo.de_tai_lop(ma_de_tai_lop)
);
GO

CREATE TABLE dbo.lich_su_dang_ky (
    ma_nhat_ky           INT IDENTITY(1,1) NOT NULL,
    ma_dang_ky           INT NULL,
    ma_lop_hoc_phan      INT NOT NULL,
    ma_sinh_vien         INT NOT NULL,
    ma_de_tai_lop        INT NOT NULL,
    hanh_dong            NVARCHAR(30) NOT NULL,
    hinh_thuc_phan_cong  NVARCHAR(20) NULL,
    ly_do                NVARCHAR(500) NULL,
    nguoi_thuc_hien      INT NULL,
    thoi_diem_thuc_hien  DATETIME2(0) NOT NULL CONSTRAINT DF_lich_su_dang_ky_thoi_diem DEFAULT SYSDATETIME(),

    CONSTRAINT PK_lich_su_dang_ky PRIMARY KEY (ma_nhat_ky),
    CONSTRAINT CK_lich_su_dang_ky_hanh_dong CHECK (hanh_dong IN (N'DANG_KY', N'HUY_DANG_KY', N'PHAN_CONG_THU_CONG', N'PHAN_CONG_TU_DONG')),
    CONSTRAINT CK_lich_su_dang_ky_hinh_thuc CHECK (hinh_thuc_phan_cong IS NULL OR hinh_thuc_phan_cong IN (N'TU_DANG_KY', N'THU_CONG', N'TU_DONG')),
    CONSTRAINT FK_lich_su_dang_ky_lop FOREIGN KEY (ma_lop_hoc_phan)
        REFERENCES dbo.lop_hoc_phan(ma_lop_hoc_phan),
    CONSTRAINT FK_lich_su_dang_ky_sinh_vien FOREIGN KEY (ma_sinh_vien)
        REFERENCES dbo.sinh_vien(ma_sinh_vien),
    CONSTRAINT FK_lich_su_dang_ky_de_tai_lop FOREIGN KEY (ma_de_tai_lop)
        REFERENCES dbo.de_tai_lop(ma_de_tai_lop),
    CONSTRAINT FK_lich_su_dang_ky_nguoi_thuc_hien FOREIGN KEY (nguoi_thuc_hien)
        REFERENCES dbo.tai_khoan(ma_tai_khoan)
);
GO

CREATE TABLE dbo.nhat_ky_he_thong (
    ma_nhat_ky           INT IDENTITY(1,1) NOT NULL,
    ma_tai_khoan         INT NULL,
    chuc_nang            NVARCHAR(100) NOT NULL,
    hanh_dong            NVARCHAR(100) NOT NULL,
    ten_bang_lien_quan   NVARCHAR(100) NULL,
    khoa_chinh_lien_quan NVARCHAR(100) NULL,
    noi_dung             NVARCHAR(MAX) NULL,
    dia_chi_ip           NVARCHAR(50) NULL,
    thoi_diem_thuc_hien  DATETIME2(0) NOT NULL CONSTRAINT DF_nhat_ky_he_thong_thoi_diem DEFAULT SYSDATETIME(),

    CONSTRAINT PK_nhat_ky_he_thong PRIMARY KEY (ma_nhat_ky),
    CONSTRAINT FK_nhat_ky_he_thong_tai_khoan FOREIGN KEY (ma_tai_khoan)
        REFERENCES dbo.tai_khoan(ma_tai_khoan)
);
GO

/* ============================================================
   3. INDEX DE TRA CUU NHANH
   ============================================================ */

CREATE INDEX IX_sinh_vien_lop_lop ON dbo.sinh_vien_lop(ma_lop_hoc_phan);
CREATE INDEX IX_sinh_vien_lop_sinh_vien ON dbo.sinh_vien_lop(ma_sinh_vien);
CREATE INDEX IX_lop_hoc_phan_giang_vien ON dbo.lop_hoc_phan(ma_giang_vien);
CREATE INDEX IX_de_tai_lop_lop ON dbo.de_tai_lop(ma_lop_hoc_phan);
CREATE INDEX IX_dang_ky_de_tai_de_tai_lop ON dbo.dang_ky_de_tai(ma_de_tai_lop);
CREATE INDEX IX_dang_ky_de_tai_sinh_vien ON dbo.dang_ky_de_tai(ma_sinh_vien);
CREATE INDEX IX_lich_su_dang_ky_lop ON dbo.lich_su_dang_ky(ma_lop_hoc_phan);
GO

/* ============================================================
   4. TRIGGER NGHIEP VU
   ============================================================ */

CREATE TRIGGER dbo.trg_kiem_tra_va_cap_nhat_sau_khi_them_dang_ky
ON dbo.dang_ky_de_tai
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- De tai lop phai thuoc dung lop hoc phan da dang ky
    IF EXISTS (
        SELECT 1
        FROM inserted i
        LEFT JOIN dbo.de_tai_lop dtl ON i.ma_de_tai_lop = dtl.ma_de_tai_lop
        WHERE dtl.ma_de_tai_lop IS NULL
           OR i.ma_lop_hoc_phan <> dtl.ma_lop_hoc_phan
    )
    BEGIN
        RAISERROR (N'De tai lop khong thuoc lop hoc phan da chon.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Sinh vien phai thuoc lop hoc phan
    IF EXISTS (
        SELECT 1
        FROM inserted i
        WHERE NOT EXISTS (
            SELECT 1
            FROM dbo.sinh_vien_lop svl
            WHERE svl.ma_lop_hoc_phan = i.ma_lop_hoc_phan
              AND svl.ma_sinh_vien = i.ma_sinh_vien
              AND svl.trang_thai = N'DANG_HOC'
        )
    )
    BEGIN
        RAISERROR (N'Sinh vien khong thuoc lop hoc phan nay.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Dang ky tu do cua sinh vien chi hop le khi cong dang ky dang mo va con han
    IF EXISTS (
        SELECT 1
        FROM inserted i
        WHERE i.hinh_thuc_phan_cong = N'TU_DANG_KY'
          AND NOT EXISTS (
              SELECT 1
              FROM dbo.dot_dang_ky ddk
              WHERE ddk.ma_lop_hoc_phan = i.ma_lop_hoc_phan
                AND ddk.trang_thai = N'DANG_MO'
                AND SYSDATETIME() >= ddk.thoi_gian_bat_dau
                AND SYSDATETIME() <= ddk.thoi_gian_ket_thuc
          )
    )
    BEGIN
        RAISERROR (N'Khong the dang ky vi cong dang ky chua mo hoac da het han.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Khong duoc dang ky vao de tai da dong
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN dbo.de_tai_lop dtl ON i.ma_de_tai_lop = dtl.ma_de_tai_lop
        WHERE dtl.trang_thai = N'DA_DONG'
    )
    BEGIN
        RAISERROR (N'Khong the dang ky vao de tai da dong.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- Kiem tra so luong khong vuot qua toi da
    IF EXISTS (
        SELECT 1
        FROM dbo.de_tai_lop dtl
        JOIN (
            SELECT ma_de_tai_lop, COUNT(*) AS so_luong_dang_ky
            FROM dbo.dang_ky_de_tai
            GROUP BY ma_de_tai_lop
        ) dem ON dem.ma_de_tai_lop = dtl.ma_de_tai_lop
        WHERE dem.so_luong_dang_ky > dtl.so_luong_toi_da
    )
    BEGIN
        RAISERROR (N'De tai da du so luong sinh vien toi da.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    ;WITH ds_anh_huong AS (
        SELECT DISTINCT ma_de_tai_lop
        FROM inserted
    ),
    dem AS (
        SELECT ds.ma_de_tai_lop, COUNT(dk.ma_dang_ky) AS so_luong
        FROM ds_anh_huong ds
        LEFT JOIN dbo.dang_ky_de_tai dk ON dk.ma_de_tai_lop = ds.ma_de_tai_lop
        GROUP BY ds.ma_de_tai_lop
    )
    UPDATE dtl
    SET so_luong_hien_tai = dem.so_luong,
        trang_thai = CASE
            WHEN dtl.trang_thai = N'DA_DONG' THEN N'DA_DONG'
            WHEN dem.so_luong >= dtl.so_luong_toi_da THEN N'DA_DU'
            ELSE N'DANG_MO'
        END
    FROM dbo.de_tai_lop dtl
    JOIN dem ON dem.ma_de_tai_lop = dtl.ma_de_tai_lop;
END;
GO

CREATE TRIGGER dbo.trg_cap_nhat_so_luong_sau_khi_xoa_dang_ky
ON dbo.dang_ky_de_tai
AFTER DELETE
AS
BEGIN
    SET NOCOUNT ON;

    ;WITH ds_anh_huong AS (
        SELECT DISTINCT ma_de_tai_lop
        FROM deleted
    ),
    dem AS (
        SELECT ds.ma_de_tai_lop, COUNT(dk.ma_dang_ky) AS so_luong
        FROM ds_anh_huong ds
        LEFT JOIN dbo.dang_ky_de_tai dk ON dk.ma_de_tai_lop = ds.ma_de_tai_lop
        GROUP BY ds.ma_de_tai_lop
    )
    UPDATE dtl
    SET so_luong_hien_tai = dem.so_luong,
        trang_thai = CASE
            WHEN dtl.trang_thai = N'DA_DONG' THEN N'DA_DONG'
            WHEN dem.so_luong >= dtl.so_luong_toi_da THEN N'DA_DU'
            ELSE N'DANG_MO'
        END
    FROM dbo.de_tai_lop dtl
    JOIN dem ON dem.ma_de_tai_lop = dtl.ma_de_tai_lop;
END;
GO

CREATE TRIGGER dbo.trg_chan_cap_nhat_khoa_dang_ky
ON dbo.dang_ky_de_tai
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF UPDATE(ma_lop_hoc_phan) OR UPDATE(ma_sinh_vien) OR UPDATE(ma_de_tai_lop)
    BEGIN
        RAISERROR (N'Khong cap nhat truc tiep lop/sinh vien/de tai trong dang_ky_de_tai. Hay huy dang ky roi tao dang ky moi.', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;
END;
GO

/* ============================================================
   5. STORED PROCEDURE NGHIEP VU
   ============================================================ */

CREATE PROCEDURE dbo.sp_cap_nhat_che_do_phan_cong_lop
    @ma_giang_vien INT,
    @ma_lop_hoc_phan INT,
    @che_do_phan_cong NVARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @che_do_phan_cong NOT IN (N'GIANG_VIEN_PHAN_CONG', N'SINH_VIEN_TU_DANG_KY')
        THROW 50601, 'Che do phan cong khong hop le.', 1;

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND ma_giang_vien = @ma_giang_vien
    )
        THROW 50602, 'Giang vien khong phu trach lop hoc phan nay.', 1;

    IF EXISTS (
        SELECT 1
        FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND trang_thai <> N'DANG_MO'
    )
        THROW 50603, 'Lop hoc phan da dong hoac luu tru.', 1;

    IF EXISTS (
        SELECT 1
        FROM dbo.dang_ky_de_tai
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
    )
        THROW 50604, 'Khong the doi che do khi lop da co sinh vien co de tai.', 1;

    BEGIN TRANSACTION;

    UPDATE dbo.lop_hoc_phan
    SET che_do_phan_cong = @che_do_phan_cong
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;

    UPDATE dbo.de_tai_lop
    SET che_do_phan_cong = @che_do_phan_cong
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;

    COMMIT TRANSACTION;
END;
GO

CREATE PROCEDURE dbo.sp_mo_cong_dang_ky
    @ma_lop_hoc_phan INT,
    @ma_giang_vien INT,
    @thoi_gian_bat_dau DATETIME2(0),
    @thoi_gian_ket_thuc DATETIME2(0),
    @ghi_chu NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF @thoi_gian_bat_dau >= @thoi_gian_ket_thuc
        THROW 50001, 'Thoi gian bat dau phai nho hon thoi gian ket thuc.', 1;

    IF NOT EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND ma_giang_vien = @ma_giang_vien
    )
        THROW 50002, 'Giang vien khong phu trach lop hoc phan nay.', 1;

    IF EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND trang_thai <> N'DANG_MO'
    )
        THROW 50004, 'Lop hoc phan da dong hoac luu tru.', 1;

    IF EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND che_do_phan_cong = N'GIANG_VIEN_PHAN_CONG'
    )
        THROW 50005, 'Lop hoc phan nay dang o che do Giang vien phan cong. Khong can mo cong dang ky.', 1;

    IF NOT EXISTS (
        SELECT 1 FROM dbo.de_tai_lop
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
    )
        THROW 50003, 'Lop hoc phan chua co de tai de mo cong dang ky.', 1;

    BEGIN TRANSACTION;

    IF EXISTS (SELECT 1 FROM dbo.dot_dang_ky WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan)
    BEGIN
        UPDATE dbo.dot_dang_ky
        SET thoi_gian_bat_dau = @thoi_gian_bat_dau,
            thoi_gian_ket_thuc = @thoi_gian_ket_thuc,
            trang_thai = N'DANG_MO',
            ma_giang_vien_tao = @ma_giang_vien,
            ghi_chu = @ghi_chu,
            thoi_diem_cap_nhat = SYSDATETIME()
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;
    END
    ELSE
    BEGIN
        INSERT INTO dbo.dot_dang_ky (
            ma_lop_hoc_phan, thoi_gian_bat_dau, thoi_gian_ket_thuc,
            trang_thai, ma_giang_vien_tao, ghi_chu
        )
        VALUES (
            @ma_lop_hoc_phan, @thoi_gian_bat_dau, @thoi_gian_ket_thuc,
            N'DANG_MO', @ma_giang_vien, @ghi_chu
        );
    END;

    UPDATE dbo.de_tai_lop
    SET trang_thai = CASE
        WHEN so_luong_hien_tai >= so_luong_toi_da THEN N'DA_DU'
        ELSE N'DANG_MO'
    END
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
      AND trang_thai <> N'DA_DONG';

    COMMIT TRANSACTION;
END;
GO

CREATE PROCEDURE dbo.sp_dang_ky_de_tai
    @ma_sinh_vien INT,
    @ma_de_tai_lop INT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @ma_lop_hoc_phan INT;
    DECLARE @so_luong_toi_da INT;
    DECLARE @so_luong_hien_tai INT;
    DECLARE @trang_thai NVARCHAR(20);
    DECLARE @che_do_phan_cong NVARCHAR(30);
    DECLARE @ma_dang_ky INT;

    BEGIN TRY
        BEGIN TRANSACTION;

        SELECT
            @ma_lop_hoc_phan = dtl.ma_lop_hoc_phan,
            @so_luong_toi_da = dtl.so_luong_toi_da,
            @so_luong_hien_tai = dtl.so_luong_hien_tai,
            @trang_thai = dtl.trang_thai,
            @che_do_phan_cong = lhp.che_do_phan_cong
        FROM dbo.de_tai_lop dtl WITH (UPDLOCK, HOLDLOCK)
        JOIN dbo.lop_hoc_phan lhp
          ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
        WHERE dtl.ma_de_tai_lop = @ma_de_tai_lop;

        IF @ma_lop_hoc_phan IS NULL
            THROW 50101, 'Khong tim thay de tai lop.', 1;

        IF @trang_thai <> N'DANG_MO'
            THROW 50102, 'De tai khong o trang thai dang mo.', 1;

        IF @che_do_phan_cong <> N'SINH_VIEN_TU_DANG_KY'
            THROW 50107, 'De tai nay do giang vien phan cong, sinh vien khong duoc tu dang ky.', 1;

        IF NOT EXISTS (
            SELECT 1
            FROM dbo.sinh_vien_lop
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND ma_sinh_vien = @ma_sinh_vien
              AND trang_thai = N'DANG_HOC'
        )
            THROW 50103, 'Sinh vien khong thuoc lop hoc phan nay.', 1;

        IF NOT EXISTS (
            SELECT 1
            FROM dbo.dot_dang_ky
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND trang_thai = N'DANG_MO'
              AND SYSDATETIME() >= thoi_gian_bat_dau
              AND SYSDATETIME() <= thoi_gian_ket_thuc
        )
            THROW 50104, 'Cong dang ky chua mo hoac da het han.', 1;

        IF EXISTS (
            SELECT 1
            FROM dbo.dang_ky_de_tai
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND ma_sinh_vien = @ma_sinh_vien
        )
            THROW 50105, 'Sinh vien da co de tai trong lop hoc phan nay.', 1;

        IF (SELECT COUNT(*) FROM dbo.dang_ky_de_tai WHERE ma_de_tai_lop = @ma_de_tai_lop) >= @so_luong_toi_da
            THROW 50106, 'De tai da du so luong.', 1;

        INSERT INTO dbo.dang_ky_de_tai (
            ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
            hinh_thuc_phan_cong, ghi_chu
        )
        VALUES (
            @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
            N'TU_DANG_KY', N'Sinh vien tu dang ky'
        );

        SET @ma_dang_ky = CONVERT(INT, SCOPE_IDENTITY());

        INSERT INTO dbo.lich_su_dang_ky (
            ma_dang_ky, ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
            hanh_dong, hinh_thuc_phan_cong, ly_do
        )
        VALUES (
            @ma_dang_ky, @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
            N'DANG_KY', N'TU_DANG_KY', N'Sinh vien tu dang ky de tai'
        );

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

CREATE PROCEDURE dbo.sp_huy_dang_ky_de_tai
    @ma_sinh_vien INT,
    @ma_lop_hoc_phan INT,
    @ly_do NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @ma_dang_ky INT;
    DECLARE @ma_de_tai_lop INT;
    DECLARE @hinh_thuc_phan_cong NVARCHAR(20);

    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (
            SELECT 1
            FROM dbo.dot_dang_ky
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND trang_thai = N'DANG_MO'
              AND SYSDATETIME() >= thoi_gian_bat_dau
              AND SYSDATETIME() <= thoi_gian_ket_thuc
        )
            THROW 50201, 'Chi duoc huy dang ky khi cong dang ky dang mo.', 1;

        SELECT
            @ma_dang_ky = ma_dang_ky,
            @ma_de_tai_lop = ma_de_tai_lop,
            @hinh_thuc_phan_cong = hinh_thuc_phan_cong
        FROM dbo.dang_ky_de_tai
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND ma_sinh_vien = @ma_sinh_vien;

        IF @ma_dang_ky IS NULL
            THROW 50202, 'Sinh vien chua co dang ky de huy.', 1;

        IF @hinh_thuc_phan_cong <> N'TU_DANG_KY'
            THROW 50203, 'Sinh vien chi duoc huy de tai do minh tu dang ky; de tai giang vien/he thong phan cong khong duoc huy.', 1;

        INSERT INTO dbo.lich_su_dang_ky (
            ma_dang_ky, ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
            hanh_dong, hinh_thuc_phan_cong, ly_do
        )
        VALUES (
            @ma_dang_ky, @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
            N'HUY_DANG_KY', N'TU_DANG_KY', ISNULL(@ly_do, N'Sinh vien huy dang ky')
        );

        DELETE FROM dbo.dang_ky_de_tai
        WHERE ma_dang_ky = @ma_dang_ky;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

CREATE PROCEDURE dbo.sp_phan_cong_thu_cong
    @ma_giang_vien INT,
    @ma_sinh_vien INT,
    @ma_de_tai_lop INT,
    @ghi_chu NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @ma_lop_hoc_phan INT;
    DECLARE @so_luong_toi_da INT;
    DECLARE @trang_thai_lop NVARCHAR(20);
    DECLARE @trang_thai_de_tai NVARCHAR(20);
    DECLARE @ma_dang_ky INT;
    DECLARE @ma_tai_khoan_giang_vien INT;

    BEGIN TRY
        BEGIN TRANSACTION;

        SELECT
            @ma_lop_hoc_phan = dtl.ma_lop_hoc_phan,
            @so_luong_toi_da = dtl.so_luong_toi_da,
            @trang_thai_lop = lhp.trang_thai,
            @trang_thai_de_tai = dtl.trang_thai
        FROM dbo.de_tai_lop dtl WITH (UPDLOCK, HOLDLOCK)
        JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
        WHERE dtl.ma_de_tai_lop = @ma_de_tai_lop
          AND lhp.ma_giang_vien = @ma_giang_vien;

        IF @ma_lop_hoc_phan IS NULL
            THROW 50301, 'Giang vien khong co quyen phan cong vao de tai nay.', 1;

        IF @trang_thai_lop <> N'DANG_MO'
            THROW 50305, 'Lop hoc phan da dong hoac luu tru, khong the phan cong them.', 1;

        IF @trang_thai_de_tai = N'DA_DONG'
            THROW 50306, 'De tai da dong, khong the phan cong them.', 1;

        IF NOT EXISTS (
            SELECT 1 FROM dbo.sinh_vien_lop
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND ma_sinh_vien = @ma_sinh_vien
              AND trang_thai = N'DANG_HOC'
        )
            THROW 50302, 'Sinh vien khong thuoc lop hoc phan nay.', 1;

        IF EXISTS (
            SELECT 1 FROM dbo.dang_ky_de_tai
            WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND ma_sinh_vien = @ma_sinh_vien
        )
            THROW 50303, 'Sinh vien da co de tai trong lop hoc phan nay.', 1;

        IF (SELECT COUNT(*) FROM dbo.dang_ky_de_tai WHERE ma_de_tai_lop = @ma_de_tai_lop) >= @so_luong_toi_da
            THROW 50304, 'De tai da du so luong.', 1;

        INSERT INTO dbo.dang_ky_de_tai (
            ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
            hinh_thuc_phan_cong, ghi_chu
        )
        VALUES (
            @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
            N'THU_CONG', ISNULL(@ghi_chu, N'Giang vien phan cong thu cong')
        );

        SET @ma_dang_ky = CONVERT(INT, SCOPE_IDENTITY());

        SELECT @ma_tai_khoan_giang_vien = ma_tai_khoan
        FROM dbo.giang_vien
        WHERE ma_giang_vien = @ma_giang_vien;

        INSERT INTO dbo.lich_su_dang_ky (
            ma_dang_ky, ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
            hanh_dong, hinh_thuc_phan_cong, ly_do, nguoi_thuc_hien
        )
        VALUES (
            @ma_dang_ky, @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
            N'PHAN_CONG_THU_CONG', N'THU_CONG',
            ISNULL(@ghi_chu, N'Giang vien phan cong thu cong'),
            @ma_tai_khoan_giang_vien
        );

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_phan_cong_tu_dong
    @ma_giang_vien INT,
    @ma_lop_hoc_phan INT,
    @so_da_phan_cong INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    DECLARE @ma_sinh_vien INT;
    DECLARE @ma_de_tai_lop INT;
    DECLARE @ma_dang_ky INT;
    DECLARE @ma_tai_khoan_giang_vien INT;

    SET @so_da_phan_cong = 0;

    IF NOT EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND ma_giang_vien = @ma_giang_vien
    )
        THROW 50501, 'Giang vien khong phu trach lop hoc phan nay.', 1;

    IF EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND trang_thai <> N'DANG_MO'
    )
        THROW 50502, 'Lop hoc phan da dong hoac luu tru, khong the phan cong tu dong.', 1;

    SELECT @ma_tai_khoan_giang_vien = ma_tai_khoan
    FROM dbo.giang_vien
    WHERE ma_giang_vien = @ma_giang_vien;

    BEGIN TRY
        BEGIN TRANSACTION;

        DECLARE cur_sv CURSOR LOCAL FAST_FORWARD FOR
            SELECT svl.ma_sinh_vien
            FROM dbo.sinh_vien_lop svl
            WHERE svl.ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND svl.trang_thai = N'DANG_HOC'
              AND NOT EXISTS (
                  SELECT 1 FROM dbo.dang_ky_de_tai dk
                  WHERE dk.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                    AND dk.ma_sinh_vien = svl.ma_sinh_vien
              )
            ORDER BY svl.ma_sinh_vien;

        OPEN cur_sv;
        FETCH NEXT FROM cur_sv INTO @ma_sinh_vien;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            SET @ma_de_tai_lop = NULL;

            SELECT TOP 1 @ma_de_tai_lop = dtl.ma_de_tai_lop
            FROM dbo.de_tai_lop dtl WITH (UPDLOCK, HOLDLOCK)
            WHERE dtl.ma_lop_hoc_phan = @ma_lop_hoc_phan
              AND dtl.trang_thai <> N'DA_DONG'
              AND dtl.so_luong_hien_tai < dtl.so_luong_toi_da
            ORDER BY
                dtl.so_luong_hien_tai,
                dtl.ma_de_tai_lop;

            IF @ma_de_tai_lop IS NULL
                BREAK;

            INSERT INTO dbo.dang_ky_de_tai (
                ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
                hinh_thuc_phan_cong, ghi_chu
            )
            VALUES (
                @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
                N'TU_DONG', N'He thong phan cong tu dong theo cho trong con lai'
            );

            SET @ma_dang_ky = CONVERT(INT, SCOPE_IDENTITY());

            INSERT INTO dbo.lich_su_dang_ky (
                ma_dang_ky, ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
                hanh_dong, hinh_thuc_phan_cong, ly_do, nguoi_thuc_hien
            )
            VALUES (
                @ma_dang_ky, @ma_lop_hoc_phan, @ma_sinh_vien, @ma_de_tai_lop,
                N'PHAN_CONG_TU_DONG', N'TU_DONG',
                N'Giang vien yeu cau he thong phan cong tu dong',
                @ma_tai_khoan_giang_vien
            );

            SET @so_da_phan_cong += 1;
            FETCH NEXT FROM cur_sv INTO @ma_sinh_vien;
        END;

        CLOSE cur_sv;
        DEALLOCATE cur_sv;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF CURSOR_STATUS('local', 'cur_sv') >= 0
            CLOSE cur_sv;
        IF CURSOR_STATUS('local', 'cur_sv') > -3
            DEALLOCATE cur_sv;
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

CREATE OR ALTER PROCEDURE dbo.sp_chot_danh_sach
    @ma_giang_vien INT,
    @ma_lop_hoc_phan INT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    IF NOT EXISTS (
        SELECT 1 FROM dbo.lop_hoc_phan
        WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND ma_giang_vien = @ma_giang_vien
    )
        THROW 50401, 'Giang vien khong phu trach lop hoc phan nay.', 1;

    IF EXISTS (
        SELECT 1
        FROM dbo.sinh_vien_lop svl
        WHERE svl.ma_lop_hoc_phan = @ma_lop_hoc_phan
          AND svl.trang_thai = N'DANG_HOC'
          AND NOT EXISTS (
              SELECT 1
              FROM dbo.dang_ky_de_tai dk
              WHERE dk.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
                AND dk.ma_sinh_vien = svl.ma_sinh_vien
          )
    )
        THROW 50402, 'Con sinh vien chua co de tai, khong the chot danh sach.', 1;

    BEGIN TRANSACTION;

    UPDATE dbo.dot_dang_ky
    SET trang_thai = N'DA_DONG',
        thoi_diem_cap_nhat = SYSDATETIME()
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;

    UPDATE dbo.de_tai_lop
    SET trang_thai = N'DA_DONG'
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;

    UPDATE dbo.lop_hoc_phan
    SET trang_thai = N'DA_DONG'
    WHERE ma_lop_hoc_phan = @ma_lop_hoc_phan;

    COMMIT TRANSACTION;
END;
GO

/* ============================================================
   6. VIEW BAO CAO VA TRA CUU
   ============================================================ */

CREATE VIEW dbo.vw_danh_sach_sinh_vien_lop
AS
SELECT
    lhp.ma_lop_hoc_phan,
    lhp.ma_lop,
    lhp.ten_lop_hoc_phan,
    sv.ma_sinh_vien,
    sv.ma_so_sinh_vien,
    sv.ho_ten,
    sv.lop_sinh_hoat,
    sv.email,
    svl.trang_thai AS trang_thai_trong_lop
FROM dbo.sinh_vien_lop svl
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien;
GO

CREATE VIEW dbo.vw_de_tai_con_cho
AS
SELECT
    dtl.ma_de_tai_lop,
    lhp.ma_lop_hoc_phan,
    lhp.ma_lop,
    ndt.ma_de_tai,
    ndt.ma_de_tai_he_thong,
    ndt.ten_de_tai,
    ndt.mo_ta,
    ndt.yeu_cau,
    dtl.so_luong_toi_da,
    dtl.so_luong_hien_tai,
    (dtl.so_luong_toi_da - dtl.so_luong_hien_tai) AS so_cho_con_lai,
    dtl.trang_thai,
    lhp.che_do_phan_cong
FROM dbo.de_tai_lop dtl
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dtl.ma_lop_hoc_phan
JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai;
GO

CREATE VIEW dbo.vw_bao_cao_nhom_de_tai
AS
SELECT
    lhp.ma_lop_hoc_phan,
    lhp.ma_lop,
    mh.ma_mon_hoc_he_thong,
    mh.ten_mon_hoc,
    hk.ten_hoc_ky,
    hk.nam_hoc,
    gv.ho_ten AS ten_giang_vien,
    dtl.ma_de_tai_lop,
    ndt.ma_de_tai_he_thong,
    ndt.ten_de_tai,
    sv.ma_so_sinh_vien,
    sv.ho_ten AS ten_sinh_vien,
    sv.lop_sinh_hoat,
    dk.hinh_thuc_phan_cong,
    dk.thoi_diem_dang_ky,
    dtl.so_luong_toi_da,
    dtl.so_luong_hien_tai
FROM dbo.dang_ky_de_tai dk
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = dk.ma_lop_hoc_phan
JOIN dbo.mon_hoc mh ON mh.ma_mon_hoc = lhp.ma_mon_hoc
JOIN dbo.hoc_ky hk ON hk.ma_hoc_ky = lhp.ma_hoc_ky
JOIN dbo.giang_vien gv ON gv.ma_giang_vien = lhp.ma_giang_vien
JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = dk.ma_sinh_vien
JOIN dbo.de_tai_lop dtl ON dtl.ma_de_tai_lop = dk.ma_de_tai_lop
JOIN dbo.ngan_hang_de_tai ndt ON ndt.ma_de_tai = dtl.ma_de_tai;
GO

CREATE VIEW dbo.vw_sinh_vien_chua_co_de_tai
AS
SELECT
    lhp.ma_lop_hoc_phan,
    lhp.ma_lop,
    lhp.ten_lop_hoc_phan,
    sv.ma_sinh_vien,
    sv.ma_so_sinh_vien,
    sv.ho_ten,
    sv.lop_sinh_hoat,
    sv.email
FROM dbo.sinh_vien_lop svl
JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
JOIN dbo.sinh_vien sv ON sv.ma_sinh_vien = svl.ma_sinh_vien
WHERE svl.trang_thai = N'DANG_HOC'
  AND NOT EXISTS (
      SELECT 1
      FROM dbo.dang_ky_de_tai dk
      WHERE dk.ma_lop_hoc_phan = svl.ma_lop_hoc_phan
        AND dk.ma_sinh_vien = svl.ma_sinh_vien
  );
GO

CREATE VIEW dbo.vw_thong_ke_lop_hoc_phan
AS
WITH ds_sinh_vien AS (
    SELECT ma_lop_hoc_phan, COUNT(*) AS tong_so_sinh_vien
    FROM dbo.sinh_vien_lop
    WHERE trang_thai = N'DANG_HOC'
    GROUP BY ma_lop_hoc_phan
),
ds_dang_ky AS (
    SELECT ma_lop_hoc_phan, COUNT(*) AS so_sinh_vien_da_co_de_tai
    FROM dbo.dang_ky_de_tai
    GROUP BY ma_lop_hoc_phan
),
ds_de_tai AS (
    SELECT ma_lop_hoc_phan, COUNT(*) AS tong_so_de_tai
    FROM dbo.de_tai_lop
    GROUP BY ma_lop_hoc_phan
)
SELECT
    lhp.ma_lop_hoc_phan,
    lhp.ma_lop,
    lhp.ten_lop_hoc_phan,
    ISNULL(sv.tong_so_sinh_vien, 0) AS tong_so_sinh_vien,
    ISNULL(dk.so_sinh_vien_da_co_de_tai, 0) AS so_sinh_vien_da_co_de_tai,
    ISNULL(sv.tong_so_sinh_vien, 0) - ISNULL(dk.so_sinh_vien_da_co_de_tai, 0) AS so_sinh_vien_chua_co_de_tai,
    ISNULL(dt.tong_so_de_tai, 0) AS tong_so_de_tai
FROM dbo.lop_hoc_phan lhp
LEFT JOIN ds_sinh_vien sv ON sv.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
LEFT JOIN ds_dang_ky dk ON dk.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan
LEFT JOIN ds_de_tai dt ON dt.ma_lop_hoc_phan = lhp.ma_lop_hoc_phan;
GO

/* ============================================================
   8. TU DONG DONG BO CAC LUONG NGHIEP VU LIEN QUAN
   ============================================================ */

/* Rút sinh viên khỏi lớp thì hủy đề tài và trả lại chỗ trống. */
CREATE TRIGGER dbo.trg_rut_sv_huy_dang_ky
ON dbo.sinh_vien_lop
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (
        SELECT 1
        FROM inserted i
        JOIN deleted d
          ON d.ma_lop_hoc_phan = i.ma_lop_hoc_phan
         AND d.ma_sinh_vien = i.ma_sinh_vien
        WHERE i.trang_thai = N'DA_RUT'
          AND d.trang_thai <> N'DA_RUT'
    )
        RETURN;

    INSERT INTO dbo.lich_su_dang_ky (
        ma_dang_ky, ma_lop_hoc_phan, ma_sinh_vien, ma_de_tai_lop,
        hanh_dong, hinh_thuc_phan_cong, ly_do, nguoi_thuc_hien
    )
    SELECT
        dk.ma_dang_ky, dk.ma_lop_hoc_phan, dk.ma_sinh_vien, dk.ma_de_tai_lop,
        N'HUY_DANG_KY', dk.hinh_thuc_phan_cong,
        N'Sinh viên bị rút khỏi lớp học phần - hủy đề tài tự động', NULL
    FROM dbo.dang_ky_de_tai dk
    JOIN inserted i
      ON i.ma_lop_hoc_phan = dk.ma_lop_hoc_phan
     AND i.ma_sinh_vien = dk.ma_sinh_vien
    JOIN deleted d
      ON d.ma_lop_hoc_phan = i.ma_lop_hoc_phan
     AND d.ma_sinh_vien = i.ma_sinh_vien
    WHERE i.trang_thai = N'DA_RUT'
      AND d.trang_thai <> N'DA_RUT';

    DELETE dk
    FROM dbo.dang_ky_de_tai dk
    JOIN inserted i
      ON i.ma_lop_hoc_phan = dk.ma_lop_hoc_phan
     AND i.ma_sinh_vien = dk.ma_sinh_vien
    JOIN deleted d
      ON d.ma_lop_hoc_phan = i.ma_lop_hoc_phan
     AND d.ma_sinh_vien = i.ma_sinh_vien
    WHERE i.trang_thai = N'DA_RUT'
      AND d.trang_thai <> N'DA_RUT';
END;
GO

/* Khóa tài khoản giảng viên thì đóng mọi cổng đăng ký đang mở do họ tạo. */
CREATE TRIGGER dbo.trg_khoa_tai_khoan_dong_cong_dk
ON dbo.tai_khoan
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (
        SELECT 1
        FROM inserted i
        JOIN deleted d ON d.ma_tai_khoan = i.ma_tai_khoan
        JOIN dbo.giang_vien gv ON gv.ma_tai_khoan = i.ma_tai_khoan
        WHERE i.trang_thai = N'BI_KHOA'
          AND d.trang_thai <> N'BI_KHOA'
    )
        RETURN;

    UPDATE ddk
    SET ddk.trang_thai = N'DA_DONG',
        ddk.thoi_diem_cap_nhat = SYSDATETIME()
    FROM dbo.dot_dang_ky ddk
    JOIN dbo.giang_vien gv ON gv.ma_giang_vien = ddk.ma_giang_vien_tao
    JOIN inserted i ON i.ma_tai_khoan = gv.ma_tai_khoan
    JOIN deleted d ON d.ma_tai_khoan = i.ma_tai_khoan
    WHERE ddk.trang_thai = N'DANG_MO'
      AND i.trang_thai = N'BI_KHOA'
      AND d.trang_thai <> N'BI_KHOA';

    INSERT INTO dbo.nhat_ky_he_thong (
        ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung
    )
    SELECT
        i.ma_tai_khoan, N'Quản lý tài khoản',
        N'KHÓA TÀI KHOẢN - ĐÓNG CỔNG ĐĂNG KÝ', N'dot_dang_ky',
        N'Tài khoản giảng viên bị khóa; các cổng đăng ký đang mở đã được đóng tự động.'
    FROM inserted i
    JOIN deleted d ON d.ma_tai_khoan = i.ma_tai_khoan
    JOIN dbo.giang_vien gv ON gv.ma_tai_khoan = i.ma_tai_khoan
    WHERE i.trang_thai = N'BI_KHOA'
      AND d.trang_thai <> N'BI_KHOA';
END;
GO

/* Đóng học kỳ thì đóng các cổng đăng ký thuộc học kỳ đó. */
CREATE TRIGGER dbo.trg_hoc_ky_dong_dong_cong_dk
ON dbo.hoc_ky
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (
        SELECT 1
        FROM inserted i
        JOIN deleted d ON d.ma_hoc_ky = i.ma_hoc_ky
        WHERE i.trang_thai = N'DA_DONG'
          AND d.trang_thai <> N'DA_DONG'
    )
        RETURN;

    UPDATE ddk
    SET ddk.trang_thai = N'DA_DONG',
        ddk.thoi_diem_cap_nhat = SYSDATETIME()
    FROM dbo.dot_dang_ky ddk
    JOIN dbo.lop_hoc_phan lhp ON lhp.ma_lop_hoc_phan = ddk.ma_lop_hoc_phan
    JOIN inserted i ON i.ma_hoc_ky = lhp.ma_hoc_ky
    JOIN deleted d ON d.ma_hoc_ky = i.ma_hoc_ky
    WHERE ddk.trang_thai = N'DANG_MO'
      AND i.trang_thai = N'DA_DONG'
      AND d.trang_thai <> N'DA_DONG';

    INSERT INTO dbo.nhat_ky_he_thong (
        ma_tai_khoan, chuc_nang, hanh_dong, ten_bang_lien_quan, noi_dung
    )
    SELECT
        NULL, N'Quản lý học kỳ', N'ĐÓNG HỌC KỲ - ĐÓNG CỔNG ĐĂNG KÝ',
        N'dot_dang_ky',
        N'Học kỳ ' + i.ten_hoc_ky + N' đã đóng; các cổng đăng ký thuộc học kỳ đã được đóng tự động.'
    FROM inserted i
    JOIN deleted d ON d.ma_hoc_ky = i.ma_hoc_ky
    WHERE i.trang_thai = N'DA_DONG'
      AND d.trang_thai <> N'DA_DONG';
END;
GO
