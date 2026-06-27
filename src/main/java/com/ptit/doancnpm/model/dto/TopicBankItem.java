package com.ptit.doancnpm.model.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Một đề tài trong ngân hàng đề tài của giảng viên (bảng ngan_hang_de_tai).
 */
public class TopicBankItem {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int maDeTai;
    private final String maDeTaiHeThong;
    private final String tenDeTai;
    private final String moTa;
    private final String yeuCau;
    private final int soLuongMacDinh;
    private final String trangThai;
    private final LocalDateTime thoiDiemTao;

    public TopicBankItem(int maDeTai, String maDeTaiHeThong, String tenDeTai,
                         String moTa, String yeuCau, int soLuongMacDinh,
                         String trangThai, LocalDateTime thoiDiemTao) {
        this.maDeTai = maDeTai;
        this.maDeTaiHeThong = maDeTaiHeThong;
        this.tenDeTai = tenDeTai;
        this.moTa = moTa;
        this.yeuCau = yeuCau;
        this.soLuongMacDinh = soLuongMacDinh;
        this.trangThai = trangThai;
        this.thoiDiemTao = thoiDiemTao;
    }

    public int getMaDeTai() { return maDeTai; }
    public String getMaDeTaiHeThong() { return maDeTaiHeThong; }
    public String getTenDeTai() { return tenDeTai; }
    public String getMoTa() { return moTa; }
    public String getYeuCau() { return yeuCau; }
    public int getSoLuongMacDinh() { return soLuongMacDinh; }
    public String getTrangThai() { return trangThai; }
    public LocalDateTime getThoiDiemTao() { return thoiDiemTao; }

    public String getThoiDiemTaoText() {
        return thoiDiemTao == null ? "" : FMT.format(thoiDiemTao);
    }

    @Override
    public String toString() {
        return maDeTaiHeThong + " - " + tenDeTai;
    }
}
