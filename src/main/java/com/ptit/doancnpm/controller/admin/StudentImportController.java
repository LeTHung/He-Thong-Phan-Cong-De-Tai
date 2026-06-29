package com.ptit.doancnpm.controller.admin;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.OptionItem;
import com.ptit.doancnpm.model.dto.StudentClassMemberSummary;
import com.ptit.doancnpm.model.dto.StudentExcelImportResult;
import com.ptit.doancnpm.model.dto.StudentExcelRow;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.StudentClassService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import com.ptit.doancnpm.util.TextFormat;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class StudentImportController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private ComboBox<OptionItem> cboCourseSection;

    @FXML
    private TableView<StudentClassMemberSummary> tblStudents;

    @FXML
    private TableColumn<StudentClassMemberSummary, Void> colStt;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colStudentCode;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colFullName;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colEmail;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colClassName;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colStatus;

    @FXML
    private TableColumn<StudentClassMemberSummary, String> colNote;

    @FXML
    private Button btnWithdraw;

    @FXML
    private TextField txtSearch;

    private final StudentClassService studentClassService = new StudentClassService();
    private final ObservableList<StudentClassMemberSummary> allStudents = FXCollections.observableArrayList();
    private final FilteredList<StudentClassMemberSummary> filteredStudents =
            new FilteredList<>(allStudents, student -> true);

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.QUAN_TRI_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập quản lý sinh viên lớp.");
            MainApp.showLogin();
            return;
        }

        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());
        setupTable();
        setupFilters();
        setupForm();
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.ADMIN_DASHBOARD_VIEW);
    }

    @FXML
    private void handleShowAccounts() {
        MainApp.setRoot(MainApp.ACCOUNT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowSubjects() {
        MainApp.setRoot(MainApp.SUBJECT_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowSemesters() {
        MainApp.setRoot(MainApp.SEMESTER_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowCourseSections() {
        MainApp.setRoot(MainApp.COURSE_SECTION_MANAGEMENT_VIEW);
    }

    @FXML
    private void handleShowReports() {
        MainApp.setRoot(MainApp.ADMIN_REPORT_VIEW);
    }

    @FXML
    private void handleImportExcel() {
        OptionItem courseSection = cboCourseSection.getValue();
        if (courseSection == null) {
            showMessage("Vui lòng chọn lớp học phần trước khi import.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn file danh sách sinh viên");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel (*.xlsx, *.xls)", "*.xlsx", "*.xls"));
        File file = chooser.showOpenDialog(tblStudents.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            List<StudentExcelRow> rows = studentClassService.readExcel(file);
            showExcelPreview(file, courseSection, rows);
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
            MainApp.showError(exception.getMessage());
        }
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    @FXML
    private void handleAddStudent() {
        try {
            OptionItem courseSection = cboCourseSection.getValue();
            if (courseSection == null) {
                showMessage("Vui lòng chọn lớp học phần.");
                return;
            }

            List<OptionItem> studentOptions = studentClassService.getStudentOptions(courseSection);
            if (studentOptions.isEmpty()) {
                showMessage("Không còn sinh viên nào có thể thêm vào lớp này.");
                return;
            }

            Map<Integer, BooleanProperty> selectionStates = new LinkedHashMap<>();
            studentOptions.forEach(student ->
                    selectionStates.put(student.getId(), new SimpleBooleanProperty(false)));

            ListView<OptionItem> studentList = new ListView<>();
            studentList.getItems().setAll(studentOptions);
            studentList.setCellFactory(CheckBoxListCell.forListView(
                    student -> selectionStates.get(student.getId())));
            studentList.setPrefHeight(260);
            studentList.setPrefWidth(380);

            CheckBox selectAll = new CheckBox("Chọn tất cả");
            Label selectedCount = new Label();
            selectedCount.getStyleClass().add("body-muted");
            Runnable updateSelectionSummary = () -> {
                long count = selectionStates.values().stream().filter(BooleanProperty::get).count();
                selectedCount.setText("Đã chọn " + count + "/" + studentOptions.size() + " sinh viên");
                selectAll.setIndeterminate(count > 0 && count < studentOptions.size());
                if (count == 0) {
                    selectAll.setSelected(false);
                } else if (count == studentOptions.size()) {
                    selectAll.setSelected(true);
                }
            };
            selectionStates.values().forEach(state ->
                    state.addListener((observable, oldValue, newValue) -> updateSelectionSummary.run()));
            selectAll.setOnAction(event -> {
                boolean selected = selectAll.isSelected();
                selectionStates.values().forEach(state -> state.set(selected));
            });
            updateSelectionSummary.run();

            VBox studentSelector = new VBox(8, selectAll, studentList, selectedCount);
            TextArea noteArea = new TextArea();
            noteArea.setPrefRowCount(3);
            noteArea.setWrapText(true);

            GridPane form = AdminFormDialog.createForm();
            AdminFormDialog.addRow(form, 0, "Sinh viên", studentSelector);
            AdminFormDialog.addRow(form, 1, "Ghi chú", noteArea);

            int[] addedCount = {0};
            boolean saved = AdminFormDialog.show(
                    tblStudents.getScene().getWindow(),
                    "Thêm sinh viên vào lớp",
                    "Lớp học phần: " + courseSection.getCode(),
                    "Thêm các sinh viên",
                    form,
                    () -> {
                        List<OptionItem> selectedStudents = studentOptions.stream()
                                .filter(student -> selectionStates.get(student.getId()).get())
                                .toList();
                        addedCount[0] = studentClassService.addStudentsToCourseSection(
                                courseSection, selectedStudents, noteArea.getText());
                    });
            if (saved) {
                loadStudents();
                tblStudents.getSelectionModel().clearSelection();
                showMessage("Đã thêm " + addedCount[0] + " sinh viên vào lớp.");
            }
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleWithdrawStudent() {
        StudentClassMemberSummary student = tblStudents.getSelectionModel().getSelectedItem();
        if (student == null) {
            showMessage("Vui lòng chọn sinh viên cần rút khỏi lớp.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận rút sinh viên");
        confirm.setHeaderText("Rút " + student.getHoTen() + " khỏi lớp?");
        confirm.setContentText("Sinh viên sẽ không còn thuộc lớp học phần đang chọn.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            studentClassService.withdrawStudentFromCourseSection(
                    cboCourseSection.getValue(),
                    student);
            showMessage("Đã rút sinh viên khỏi lớp.");
            tblStudents.getSelectionModel().clearSelection();
            loadStudents();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleRefreshStudents() {
        setupForm();
        showMessage("Đã làm mới dữ liệu sinh viên lớp.");
    }

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
    }

    private void setupTable() {
        tblStudents.setItems(filteredStudents);
        tblStudents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colStt.setCellFactory(TableCells.indexColumn());
        colStudentCode.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getMaSoSinhVien()));
        colFullName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHoTen()));
        colEmail.setCellValueFactory(data -> new ReadOnlyStringWrapper(TextFormat.emptyIfNull(data.getValue().getEmail())));
        colClassName.setCellValueFactory(data -> new ReadOnlyStringWrapper(TextFormat.emptyIfNull(data.getValue().getLopSinhHoat())));
        colStatus.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTrangThaiText()));
        colNote.setCellValueFactory(data -> new ReadOnlyStringWrapper(TextFormat.emptyIfNull(data.getValue().getGhiChu())));
        btnWithdraw.disableProperty().bind(tblStudents.getSelectionModel().selectedItemProperty().isNull());
    }

    private void showExcelPreview(File file, OptionItem courseSection, List<StudentExcelRow> rows) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Xem trước import sinh viên");
        dialog.setHeaderText("Lớp học phần: " + courseSection.getCode());
        dialog.initOwner(tblStudents.getScene().getWindow());

        TableView<StudentExcelRow> previewTable = new TableView<>();
        List<TableColumn<StudentExcelRow, ?>> previewColumns = List.of(
                createPreviewColumn("STT", 65, row -> String.valueOf(row.sequenceNumber())),
                createPreviewColumn("Mã SV", 145, StudentExcelRow::studentCode),
                createPreviewColumn("Họ lót", 190, StudentExcelRow::lastName),
                createPreviewColumn("Tên", 120, StudentExcelRow::firstName),
                createPreviewColumn("Mã lớp", 155, StudentExcelRow::classCode),
                createPreviewColumn("Email", 250, StudentExcelRow::email));
        previewTable.getColumns().addAll(previewColumns);

        TableColumn<StudentExcelRow, String> statusColumn =
                createPreviewColumn("Kiểm tra", 230, StudentExcelRow::statusText);
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(!empty && !"Hợp lệ".equals(item)
                        ? "-fx-text-fill: #dc2626; -fx-font-weight: 700;"
                        : "-fx-text-fill: #15803d; -fx-font-weight: 700;");
            }
        });
        previewTable.getColumns().add(statusColumn);
        previewTable.getItems().setAll(rows);
        previewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        previewTable.setPrefHeight(480);

        long invalidCount = rows.stream().filter(row -> !row.isValid()).count();
        Label fileLabel = new Label("File: " + file.getName());
        fileLabel.getStyleClass().add("caption-strong");
        Label summaryLabel = new Label(invalidCount == 0
                ? rows.size() + " sinh viên hợp lệ · Tài khoản mới dùng mật khẩu mặc định 123456"
                : "Có " + invalidCount + " dòng lỗi. Hãy sửa file Excel rồi chọn lại.");
        summaryLabel.setWrapText(true);
        summaryLabel.setStyle(invalidCount == 0
                ? "-fx-text-fill: #15803d;"
                : "-fx-text-fill: #dc2626; -fx-font-weight: 700;");
        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: 700;");

        VBox content = new VBox(10, fileLabel, summaryLabel, previewTable, errorLabel);
        ButtonType importButtonType = new ButtonType(
                "Import " + rows.size() + " sinh viên",
                javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(
                "Hủy",
                javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, cancelButtonType);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(1120, 700);
        dialog.getDialogPane().getStyleClass().add("admin-form-dialog");
        dialog.getDialogPane().getStylesheets().setAll(tblStudents.getScene().getStylesheets());

        Button importButton = (Button) dialog.getDialogPane().lookupButton(importButtonType);
        importButton.getStyleClass().add("btn-primary");
        importButton.setDisable(invalidCount > 0);
        dialog.getDialogPane().lookupButton(cancelButtonType).getStyleClass().add("btn-outline");

        StudentExcelImportResult[] importResult = {null};
        importButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                importResult[0] = studentClassService.importExcel(courseSection, rows);
            } catch (RuntimeException exception) {
                errorLabel.setText(exception.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
        if (importResult[0] != null) {
            StudentExcelImportResult result = importResult[0];
            loadStudents();
            showMessage("Đã import " + result.totalRows() + " dòng: tạo mới "
                    + result.createdStudents() + ", cập nhật " + result.updatedStudents()
                    + ", thêm vào lớp " + result.addedToClass() + ", bỏ qua " + result.skippedRows() + ".");
        }
    }

    private TableColumn<StudentExcelRow, String> createPreviewColumn(
            String title,
            double width,
            Function<StudentExcelRow, String> valueExtractor) {
        TableColumn<StudentExcelRow, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(valueExtractor.apply(data.getValue())));
        return column;
    }

    private void setupFilters() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void applyFilters() {
        filteredStudents.setPredicate(student ->
                AdminFilterSupport.contains(
                        txtSearch.getText(),
                        student.getMaSoSinhVien(),
                        student.getHoTen(),
                        student.getEmail(),
                        student.getLopSinhHoat(),
                        student.getGhiChu()));
    }

    private void setupForm() {
        try {
            cboCourseSection.setConverter(new StringConverter<>() {
                @Override
                public String toString(OptionItem courseSection) {
                    return courseSection == null ? "" : courseSection.getName();
                }

                @Override
                public OptionItem fromString(String text) {
                    return null;
                }
            });
            List<OptionItem> courseSections = studentClassService.getCourseSectionOptions();
            OptionItem selectedCourseSection = cboCourseSection.getValue();
            cboCourseSection.getItems().setAll(courseSections);
            selectOrFirst(cboCourseSection, selectedCourseSection);

            cboCourseSection.setOnAction(event -> loadStudents());
            loadStudents();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    private void loadStudents() {
        OptionItem courseSection = cboCourseSection.getValue();
        if (courseSection == null) {
            allStudents.clear();
            return;
        }

        try {
            allStudents.setAll(studentClassService.getStudentsByCourseSection(courseSection));
        } catch (RuntimeException exception) {
            allStudents.clear();
            showMessage(exception.getMessage());
        }
    }

    private void selectOrFirst(ComboBox<OptionItem> comboBox, OptionItem previousValue) {
        if (previousValue != null) {
            comboBox.getItems().stream()
                    .filter(option -> option.getId() == previousValue.getId())
                    .findFirst()
                    .ifPresent(comboBox::setValue);
        }

        if (comboBox.getValue() == null && !comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(0));
        }
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
