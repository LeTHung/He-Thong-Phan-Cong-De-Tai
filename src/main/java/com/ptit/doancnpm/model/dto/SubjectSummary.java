package com.ptit.doancnpm.model.dto;

public class SubjectSummary {
    private final int maMonHoc;
    private final String maMonHocHeThong;
    private final String tenMonHoc;
    private final int soTinChi;
    private final String moTa;
    private final String trangThai;

    public SubjectSummary(int maMonHoc, String maMonHocHeThong, String tenMonHoc, int soTinChi, String moTa, String trangThai) {
        this.maMonHoc = maMonHoc;
        this.maMonHocHeThong = maMonHocHeThong;
        this.tenMonHoc = tenMonHoc;
        this.soTinChi = soTinChi;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public int getMaMonHoc() {
        return maMonHoc;
    }

    public String getMaMonHocHeThong() {
        return maMonHocHeThong;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public String getTrangThaiText() {
        return "DANG_SU_DUNG".equalsIgnoreCase(trangThai) ? "Đang sử dụng" : "Ngừng sử dụng";
    }
}
