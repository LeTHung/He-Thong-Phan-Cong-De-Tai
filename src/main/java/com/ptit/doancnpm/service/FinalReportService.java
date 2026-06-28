package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.FinalReportDAO;
import com.ptit.doancnpm.model.dto.RegistrationResultRow;

import java.util.List;

public class FinalReportService {

    private final FinalReportDAO finalReportDAO = new FinalReportDAO();

    public int[] getStats(int maLopHocPhan) { return finalReportDAO.getStats(maLopHocPhan); }
    public boolean isFinalized(int maLopHocPhan) { return finalReportDAO.isFinalized(maLopHocPhan); }
    public boolean hasPeriodForLop(int maLopHocPhan) { return finalReportDAO.hasPeriodForLop(maLopHocPhan); }
    public boolean hasRegistrationStarted(int maLopHocPhan) { return finalReportDAO.hasRegistrationStarted(maLopHocPhan); }
    public boolean isRegistrationOpen(int maLopHocPhan) { return finalReportDAO.isRegistrationOpen(maLopHocPhan); }
    public void finalizeRegistration(int maGiangVien, int maLopHocPhan) {
        finalReportDAO.finalizeRegistration(maGiangVien, maLopHocPhan);
    }
    public List<RegistrationResultRow> getFinalReport(int maLopHocPhan) {
        return finalReportDAO.getFinalReport(maLopHocPhan);
    }
}
