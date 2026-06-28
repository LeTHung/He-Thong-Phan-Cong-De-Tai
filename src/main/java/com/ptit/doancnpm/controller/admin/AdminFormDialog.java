package com.ptit.doancnpm.controller.admin;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

final class AdminFormDialog {

    private AdminFormDialog() {
    }

    static GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(14);
        form.setVgap(12);
        form.setPadding(new Insets(8, 4, 4, 4));

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(140);
        ColumnConstraints fieldColumn = new ColumnConstraints();
        fieldColumn.setHgrow(Priority.ALWAYS);
        fieldColumn.setFillWidth(true);
        form.getColumnConstraints().setAll(labelColumn, fieldColumn);
        return form;
    }

    static void addRow(GridPane form, int row, String labelText, Node field) {
        Label label = new Label(labelText);
        label.getStyleClass().add("caption-strong");
        form.add(label, 0, row);
        form.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    static boolean show(
            Window owner,
            String title,
            String header,
            String saveText,
            GridPane form,
            Runnable saveAction) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.initOwner(owner);

        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: 700;");

        VBox content = new VBox(10, form, errorLabel);
        ButtonType saveButtonType = new ButtonType(saveText, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(560);
        dialog.getDialogPane().getStyleClass().add("admin-form-dialog");
        if (owner != null && owner.getScene() != null) {
            dialog.getDialogPane().getStylesheets().setAll(owner.getScene().getStylesheets());
        }

        boolean[] saved = {false};
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.getStyleClass().add("btn-primary");
        dialog.getDialogPane().lookupButton(cancelButtonType).getStyleClass().add("btn-outline");
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                saveAction.run();
                saved[0] = true;
            } catch (RuntimeException exception) {
                errorLabel.setText(exception.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
        return saved[0];
    }
}
