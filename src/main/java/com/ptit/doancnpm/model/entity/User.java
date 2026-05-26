package com.ptit.doancnpm.model.entity;

public class User {
    private int maTaiKhoan;
    private String tenDangNhap;
    private String matKhauMaHoa;
    private UserRole vaiTro;
    private UserStatus trangThai;
    private String email;
    private String soDienThoai;

    public User() {
    }

    public User(
            int maTaiKhoan,
            String tenDangNhap,
            String matKhauMaHoa,
            UserRole vaiTro,
            UserStatus trangThai,
            String email,
            String soDienThoai) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhauMaHoa = matKhauMaHoa;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.email = email;
        this.soDienThoai = soDienThoai;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhauMaHoa() {
        return matKhauMaHoa;
    }

    public void setMatKhauMaHoa(String matKhauMaHoa) {
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public UserRole getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(UserRole vaiTro) {
        this.vaiTro = vaiTro;
    }

    public UserStatus getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(UserStatus trangThai) {
        this.trangThai = trangThai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}
