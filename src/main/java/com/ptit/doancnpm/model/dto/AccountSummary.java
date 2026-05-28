package com.ptit.doancnpm.model.dto;

import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.model.entity.UserStatus;

import java.time.LocalDateTime;

public class AccountSummary {
    private int maTaiKhoan;
    private String tenDangNhap;
    private UserRole vaiTro;
    private UserStatus trangThai;
    private String email;
    private String soDienThoai;
    private LocalDateTime lanDangNhapCuoi;

    public AccountSummary(
            int maTaiKhoan,
            String tenDangNhap,
            UserRole vaiTro,
            UserStatus trangThai,
            String email,
            String soDienThoai,
            LocalDateTime lanDangNhapCuoi) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.email = email;
        this.soDienThoai = soDienThoai;
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public UserRole getVaiTro() {
        return vaiTro;
    }

    public String getVaiTroText() {
        return vaiTro == null ? "" : vaiTro.getDisplayName();
    }

    public UserStatus getTrangThai() {
        return trangThai;
    }

    public String getTrangThaiText() {
        return trangThai == UserStatus.HOAT_DONG ? "Hoạt động" : "Bị khóa";
    }

    public String getEmail() {
        return email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public LocalDateTime getLanDangNhapCuoi() {
        return lanDangNhapCuoi;
    }
}
