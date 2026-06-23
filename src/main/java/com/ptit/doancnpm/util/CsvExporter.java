package com.ptit.doancnpm.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Tiện ích xuất dữ liệu ra file CSV (UTF-8 có BOM để Excel đọc đúng tiếng Việt).
 */
public final class CsvExporter {

    /** Mã ký tự Byte Order Mark (U+FEFF) giúp Excel nhận diện file là UTF-8. */
    private static final int UTF8_BOM = 0xFEFF;

    private CsvExporter() {
    }

    /**
     * Mở hộp thoại chọn nơi lưu file CSV. Trả về null nếu người dùng bấm hủy.
     */
    public static File chooseSaveFile(Window owner, String suggestedName) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuất CSV");
        chooser.setInitialFileName(suggestedName);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));
        return chooser.showSaveDialog(owner);
    }

    public static void write(File file, List<String> headers, List<List<String>> rows) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(UTF8_BOM);
            writeRow(writer, headers);
            for (List<String> row : rows) {
                writeRow(writer, row);
            }
        }
    }

    private static void writeRow(Writer writer, List<String> cells) throws IOException {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                line.append(',');
            }
            line.append(escape(cells.get(i)));
        }
        line.append("\r\n");
        writer.write(line.toString());
    }

    private static String escape(String value) {
        String safe = value == null ? "" : value;
        if (safe.contains("\"") || safe.contains(",") || safe.contains("\n") || safe.contains("\r")) {
            return '"' + safe.replace("\"", "\"\"") + '"';
        }
        return safe;
    }
}
