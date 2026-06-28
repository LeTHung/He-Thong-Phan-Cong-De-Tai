package com.ptit.doancnpm.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Cell factory dùng chung cho các cột TableView chứa văn bản dài: hiển thị đầy đủ
 * nội dung bằng cách xuống dòng (wrap) thay vì cắt bớt và thêm dấu "...".
 *
 * <p>Dùng {@link Text} với {@code wrappingWidth} (chứ không phải Label có wrapText)
 * để chiều cao dòng tự giãn theo nội dung; Label bị giới hạn chiều cao một dòng sẽ
 * vẫn hiện dấu "..." dù đã bật wrap.
 */
public final class TableCells {

    private TableCells() {
    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> wrapping() {
        return column -> new TableCell<>() {
            private final Text text = new Text();

            {
                text.getStyleClass().add("table-cell-wrap");
                // Bề rộng wrap bám theo bề rộng cột, chừa chút padding hai bên.
                text.wrappingWidthProperty().bind(column.widthProperty().subtract(14));
                setPrefHeight(Region.USE_COMPUTED_SIZE);
            }

            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    text.setText("");
                    setGraphic(null);
                } else {
                    text.setText(value);
                    setGraphic(text);
                }
            }
        };
    }

    /**
     * Cell factory cho cột số thứ tự (STT): hiển thị vị trí dòng hiện tại trong
     * TableView (theo đúng thứ tự hiển thị, kể cả khi đã sắp xếp/lọc), bắt đầu từ 1.
     */
    public static <S> Callback<TableColumn<S, Void>, TableCell<S, Void>> indexColumn() {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(Void value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        };
    }
}
