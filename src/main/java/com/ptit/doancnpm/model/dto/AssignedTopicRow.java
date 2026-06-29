package com.ptit.doancnpm.model.dto;

/**
 * Một dòng đề tài đã được gán vào lớp học phần.
 */
public class AssignedTopicRow {

    private final int maDeTaiLop;
    private final String maDeTaiHeThong;
    private final String tenDeTai;
    private final int soLuongToiDa;
    private final int soLuongHienTai;
    private final String cheDoPhancong;
    private final String trangThai;

    public AssignedTopicRow(int maDeTaiLop, String maDeTaiHeThong, String tenDeTai,
                            int soLuongToiDa, int soLuongHienTai,
                            String cheDoPhancong, String trangThai) {
        this.maDeTaiLop = maDeTaiLop;
        this.maDeTaiHeThong = maDeTaiHeThong;
        this.tenDeTai = tenDeTai;
        this.soLuongToiDa = soLuongToiDa;
        this.soLuongHienTai = soLuongHienTai;
        this.cheDoPhancong = cheDoPhancong;
        this.trangThai = trangThai;
    }

    public int getMaDeTaiLop() { return maDeTaiLop; }
    public String getMaDeTaiHeThong() { return maDeTaiHeThong; }
    public String getTenDeTai() { return tenDeTai; }
    public int getSoLuongToiDa() { return soLuongToiDa; }
    public int getSoLuongHienTai() { return soLuongHienTai; }
    public String getCheDoPhancong() { return cheDoPhancong; }
    public String getTrangThai() { return trangThai; }
    public int getSoChoConLai() { return soLuongToiDa - soLuongHienTai; }

    public String getCheDoPhanCongText() {
        if (cheDoPhancong == null) return "";
        return switch (cheDoPhancong) {
            case "SINH_VIEN_TU_DANG_KY" -> "Sinh viên tự đăng ký";
            case "GIANG_VIEN_PHAN_CONG" -> "Giảng viên phân công";
            default -> cheDoPhancong;
        };
    }

    @Override
    public String toString() {
        return maDeTaiHeThong + " - " + tenDeTai + " (còn " + getSoChoConLai() + " chỗ)";
    }
}
