package com.ptit.doancnpm.service;

import com.ptit.doancnpm.model.dao.RegistrationPeriodDAO;
import com.ptit.doancnpm.model.dto.RegistrationPeriodInfo;

import java.time.LocalDateTime;
import java.util.Optional;

public class RegistrationPeriodService {

    private final RegistrationPeriodDAO registrationPeriodDAO = new RegistrationPeriodDAO();

    public Optional<RegistrationPeriodInfo> findCurrentByLop(int maLopHocPhan) {
        return registrationPeriodDAO.findCurrentByLop(maLopHocPhan);
    }

    public void openPeriod(int maLopHocPhan, int maGiangVien,
                           LocalDateTime start, LocalDateTime end, String note) {
        registrationPeriodDAO.openPeriod(maLopHocPhan, maGiangVien, start, end, note);
    }

    public void closePeriod(int maLopHocPhan, int maGiangVien) {
        registrationPeriodDAO.closePeriod(maLopHocPhan, maGiangVien);
    }
}
