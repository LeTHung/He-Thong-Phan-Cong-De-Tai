package com.ptit.doancnpm.util;

import com.ptit.doancnpm.model.dto.RegistrationResultRow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvExporter {

    public void export(List<RegistrationResultRow> rows, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write('﻿'); // UTF-8 BOM for Excel
            writer.write("Mã SV,Họ tên,Lớp SH,Mã đề tài,Tên đề tài,Hình thức\n");
            for (RegistrationResultRow row : rows) {
                writer.write(String.join(",",
                        escape(row.getMaSoSinhVien()),
                        escape(row.getTenSinhVien()),
                        escape(row.getLopSinhHoat()),
                        escape(row.getMaDeTaiHeThong()),
                        escape(row.getTenDeTai()),
                        escape(row.getHinhThucPhanCongText())));
                writer.write('\n');
            }
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
