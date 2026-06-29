package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.AssignedTopicRow;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.entity.Topic;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.AssignTopicService;
import com.ptit.doancnpm.service.LecturerDashboardService;
import com.ptit.doancnpm.service.TopicBankService;
import com.ptit.doancnpm.util.SessionManager;
import com.ptit.doancnpm.util.TableCells;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AssignTopicToClassController {

    @FXML private ComboBox<LecturerCourseSectionSummary> cbSection;
    @FXML private TextField txtMaxStudents;
    @FXML private ComboBox<String> cbMode;

    @FXML private TableView<AssignedTopicRow> tableAssigned;
    @FXML private TableColumn<AssignedTopicRow, Void> colStt;
    @FXML private TableColumn<AssignedTopicRow, String> colMaDeTai;
    @FXML private TableColumn<AssignedTopicRow, String> colTenDeTai;
    @FXML private TableColumn<AssignedTopicRow, Integer> colToiDa;
    @FXML private TableColumn<AssignedTopicRow, Integer> colHienTai;
    @FXML private TableColumn<AssignedTopicRow, String> colCheDoText;
    @FXML private TableColumn<AssignedTopicRow, String> colTrangThai;
    @FXML private Label lblSectionInfo;
    @FXML private Label lblSavedMode;

    private final LecturerDashboardService dashboardService = new LecturerDashboardService();
    private final TopicBankService topicBankService = new TopicBankService();
    private final AssignTopicService assignTopicService = new AssignTopicService();

    private int maGiangVien;
    private List<Topic> allTopics = List.of();
    private Set<String> assignedTopicCodes = Set.of();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        try {
            maGiangVien = topicBankService.findMaGiangVienByTaiKhoan(user.getMaTaiKhoan());
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        cbMode.setItems(FXCollections.observableArrayList(
                "SINH_VIEN_TU_DANG_KY", "GIANG_VIEN_PHAN_CONG"));
        cbMode.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(String mode) { return modeLabel(mode); }
            @Override public String fromString(String text) {
                return text != null && text.contains("Giảng viên")
                        ? "GIANG_VIEN_PHAN_CONG"
                        : "SINH_VIEN_TU_DANG_KY";
            }
        });
        cbMode.setValue("SINH_VIEN_TU_DANG_KY");

        setupTable();
        loadSections(user.getMaTaiKhoan());
        loadTopics();

        cbSection.setOnAction(e -> onSectionSelected());
        tableAssigned.setPlaceholder(new Label("Chọn lớp học phần để xem danh sách đề tài đã gán."));
    }

    private void setupTable() {
        colStt.setCellFactory(TableCells.indexColumn());
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colToiDa.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getSoLuongToiDa()).asObject());
        colHienTai.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getSoLuongHienTai()).asObject());
        colCheDoText.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCheDoPhanCongText()));
        colTrangThai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai()));
    }

    private void loadSections(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = dashboardService.getCourseSections(maTaiKhoan);
            cbSection.setItems(FXCollections.observableArrayList(sections));
            cbSection.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(LecturerCourseSectionSummary s) {
                    return s == null ? "" : s.maLop() + " - " + s.tenLopHocPhan();
                }
                @Override public LecturerCourseSectionSummary fromString(String s) { return null; }
            });
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách lớp: " + e.getMessage());
        }
    }

    private void loadTopics() {
        try {
            allTopics = topicBankService.findByGiangVien(maGiangVien);
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách đề tài: " + e.getMessage());
        }
    }

    private void onSectionSelected() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) return;
        lblSectionInfo.setText(section.tenMonHoc() + " · " + section.tenHocKy() + " " + section.namHoc()
                + " · " + section.tongSoSinhVien() + " sinh viên");
        loadClassAssignmentMode(section.maLopHocPhan());
        loadAssigned(section.maLopHocPhan());
    }

    private void loadClassAssignmentMode(int maLopHocPhan) {
        try {
            String savedMode = assignTopicService.getClassAssignmentMode(maLopHocPhan);
            cbMode.setValue(savedMode);
            lblSavedMode.setText("Đang lưu: " + modeLabel(savedMode));
        } catch (Exception e) {
            MainApp.showError("Lỗi tải chế độ phân công của lớp: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveClassMode() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        String mode = cbMode.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (mode == null) { MainApp.showError("Vui lòng chọn chế độ phân công."); return; }

        try {
            assignTopicService.updateClassAssignmentMode(
                    maGiangVien, section.maLopHocPhan(), mode);
            loadClassAssignmentMode(section.maLopHocPhan());
            loadAssigned(section.maLopHocPhan());
            MainApp.showInfo("Đã lưu chế độ \"" + modeLabel(mode)
                    + "\" cho toàn bộ lớp " + section.maLop() + ".");
        } catch (Exception e) {
            MainApp.showError("Không thể lưu chế độ lớp: " + e.getMessage());
        }
    }

    private void loadAssigned(int maLopHocPhan) {
        try {
            List<AssignedTopicRow> rows = assignTopicService.findByLop(maLopHocPhan);
            tableAssigned.setItems(FXCollections.observableArrayList(rows));
            assignedTopicCodes = rows.stream()
                    .map(AssignedTopicRow::getMaDeTaiHeThong)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách đề tài đã gán: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenTopicPicker() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        String maxStr = txtMaxStudents.getText().trim();

        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Không thể gán đề tài vào lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        Integer maxStudents = null;
        if (!maxStr.isEmpty()) {
            try {
                maxStudents = Integer.parseInt(maxStr);
                if (maxStudents <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                MainApp.showError("Số sinh viên tối đa phải là số nguyên dương.");
                return;
            }
        }

        loadTopics();
        showTopicPicker(section, maxStudents);
    }

    private void showTopicPicker(LecturerCourseSectionSummary section,
                                 Integer maxStudents) {
        List<TopicChoice> choices = allTopics.stream()
                .map(topic -> new TopicChoice(
                        topic, assignedTopicCodes.contains(topic.getMaDeTaiHeThong())))
                .toList();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(MainApp.getPrimaryStage());
        dialog.setTitle("Chọn đề tài từ ngân hàng");
        dialog.setHeaderText("Thêm đề tài vào lớp " + section.maLop());

        ButtonType addButtonType = new ButtonType(
                "Thêm vào lớp", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TableView<TopicChoice> topicTable = createTopicPickerTable();
        topicTable.setItems(FXCollections.observableArrayList(choices));
        topicTable.setPlaceholder(new Label(
                "Ngân hàng chưa có đề tài. Hãy tạo đề tài tại màn hình Ngân hàng đề tài."));

        Label selectionSummary = new Label();
        selectionSummary.getStyleClass().add("body-muted");
        for (TopicChoice choice : choices) {
            choice.selectedProperty().addListener((observable, oldValue, newValue) ->
                    updatePickerSummary(selectionSummary, choices, section, maxStudents));
        }
        updatePickerSummary(selectionSummary, choices, section, maxStudents);

        Button selectAllButton = new Button("Chọn tất cả chưa gán");
        selectAllButton.getStyleClass().add("btn-secondary");
        selectAllButton.setOnAction(event -> choices.stream()
                .filter(choice -> !choice.isAssigned())
                .forEach(choice -> choice.selectedProperty().set(true)));

        Button clearButton = new Button("Bỏ chọn");
        clearButton.getStyleClass().add("btn-outline");
        clearButton.setOnAction(event -> choices.stream()
                .filter(choice -> !choice.isAssigned())
                .forEach(choice -> choice.selectedProperty().set(false)));

        HBox pickerActions = new HBox(10, selectAllButton, clearButton);
        pickerActions.setAlignment(Pos.CENTER_RIGHT);
        VBox content = new VBox(12, topicTable, pickerActions, selectionSummary);
        content.setPadding(new Insets(4));
        VBox.setVgrow(topicTable, javafx.scene.layout.Priority.ALWAYS);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(860, 540);

        Button addButton = (Button) dialog.getDialogPane().lookupButton(addButtonType);
        boolean hasAvailableTopic = choices.stream().anyMatch(choice -> !choice.isAssigned());
        addButton.setDisable(!hasAvailableTopic);
        addButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (getSelectedTopics(choices).isEmpty()) {
                MainApp.showError("Vui lòng chọn ít nhất một đề tài chưa gán.");
                event.consume();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == addButtonType) {
            assignSelectedTopics(section, getSelectedTopics(choices), maxStudents);
        }
    }

    private TableView<TopicChoice> createTopicPickerTable() {
        TableView<TopicChoice> table = new TableView<>();

        TableColumn<TopicChoice, Boolean> selectColumn = new TableColumn<>("Chọn");
        selectColumn.setPrefWidth(70);
        selectColumn.setSortable(false);
        selectColumn.setCellValueFactory(data -> data.getValue().selectedProperty());
        selectColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private BooleanProperty boundSelection;

            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                if (boundSelection != null) {
                    checkBox.selectedProperty().unbindBidirectional(boundSelection);
                    boundSelection = null;
                }
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                TopicChoice choice = getTableView().getItems().get(getIndex());
                checkBox.setDisable(choice.isAssigned());
                if (choice.isAssigned()) {
                    checkBox.setSelected(true);
                } else {
                    boundSelection = choice.selectedProperty();
                    checkBox.selectedProperty().bindBidirectional(boundSelection);
                }
                setGraphic(checkBox);
                setAlignment(Pos.CENTER);
            }
        });

        TableColumn<TopicChoice, String> codeColumn = new TableColumn<>("Mã đề tài");
        codeColumn.setPrefWidth(130);
        codeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().topic().getMaDeTaiHeThong()));

        TableColumn<TopicChoice, String> nameColumn = new TableColumn<>("Tên đề tài");
        nameColumn.setPrefWidth(390);
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().topic().getTenDeTai()));

        TableColumn<TopicChoice, Integer> capacityColumn = new TableColumn<>("SV mặc định");
        capacityColumn.setPrefWidth(110);
        capacityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().topic().getSoLuongMacDinh()).asObject());

        TableColumn<TopicChoice, String> statusColumn = new TableColumn<>("Trạng thái");
        statusColumn.setPrefWidth(120);
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isAssigned() ? "Đã gán" : "Chưa gán"));

        table.getColumns().add(selectColumn);
        table.getColumns().add(codeColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(capacityColumn);
        table.getColumns().add(statusColumn);
        return table;
    }

    private void assignSelectedTopics(LecturerCourseSectionSummary section,
                                      List<Topic> topics,
                                      Integer maxStudents) {
        try {
            int assignedCount = assignTopicService.assignTopics(
                    section.maLopHocPhan(), topics, maxStudents);
            loadAssigned(section.maLopHocPhan());
            MainApp.showInfo("Đã thêm " + assignedCount
                    + " đề tài vào lớp. Chế độ chung hiện tại: "
                    + modeLabel(assignTopicService.getClassAssignmentMode(section.maLopHocPhan())) + ".");
        } catch (Exception e) {
            MainApp.showError("Lỗi gán đề tài: " + e.getMessage());
        }
    }

    private void updatePickerSummary(Label label, List<TopicChoice> choices,
                                     LecturerCourseSectionSummary section,
                                     Integer maxStudents) {
        List<Topic> selectedTopics = getSelectedTopics(choices);
        int selectedCapacity = selectedTopics.stream()
                .mapToInt(topic -> maxStudents != null
                        ? maxStudents
                        : topic.getSoLuongMacDinh())
                .sum();
        int existingCapacity = tableAssigned.getItems().stream()
                .mapToInt(AssignedTopicRow::getSoLuongToiDa)
                .sum();
        int totalCapacity = existingCapacity + selectedCapacity;
        int missingSlots = section.tongSoSinhVien() - totalCapacity;
        String capacityStatus = missingSlots > 0
                ? " · còn thiếu " + missingSlots + " chỗ"
                : " · đủ cho " + section.tongSoSinhVien() + " sinh viên";
        label.setText(selectedTopics.size() + " đề tài được chọn · thêm "
                + selectedCapacity + " chỗ · tổng sau gán " + totalCapacity + " chỗ"
                + capacityStatus);
    }

    private List<Topic> getSelectedTopics(List<TopicChoice> choices) {
        return choices.stream()
                .filter(choice -> !choice.isAssigned() && choice.selectedProperty().get())
                .map(TopicChoice::topic)
                .toList();
    }

    @FXML
    private void handleRemove() {
        LecturerCourseSectionSummary section = cbSection.getValue();
        if (section == null) { MainApp.showError("Vui lòng chọn lớp học phần."); return; }
        if (!"DANG_MO".equals(section.trangThai())) {
            MainApp.showError("Không thể gỡ đề tài khỏi lớp học phần đã đóng hoặc lưu trữ.");
            return;
        }
        AssignedTopicRow selected = tableAssigned.getSelectionModel().getSelectedItem();
        if (selected == null) { MainApp.showInfo("Vui lòng chọn một đề tài để gỡ."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận gỡ đề tài");
        confirm.setHeaderText(null);
        confirm.setContentText("Gỡ đề tài \"" + selected.getTenDeTai() + "\" khỏi lớp?\n(Chỉ được gỡ nếu chưa có sinh viên đăng ký)");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                assignTopicService.removeAssignment(selected.getMaDeTaiLop());
                loadAssigned(section.maLopHocPhan());
            } catch (Exception e) {
                MainApp.showError("Lỗi gỡ đề tài: " + e.getMessage());
            }
        }
    }

    private static final class TopicChoice {
        private final Topic topic;
        private final boolean assigned;
        private final BooleanProperty selected;

        private TopicChoice(Topic topic, boolean assigned) {
            this.topic = topic;
            this.assigned = assigned;
            this.selected = new SimpleBooleanProperty(assigned);
        }

        private Topic topic() {
            return topic;
        }

        private boolean isAssigned() {
            return assigned;
        }

        private BooleanProperty selectedProperty() {
            return selected;
        }
    }

    private String modeLabel(String mode) {
        return switch (mode == null ? "" : mode) {
            case "GIANG_VIEN_PHAN_CONG" -> "Giảng viên phân công";
            case "SINH_VIEN_TU_DANG_KY" -> "Sinh viên tự đăng ký";
            default -> mode;
        };
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavTopicBank() { MainApp.setRoot(MainApp.LECTURER_TOPIC_BANK_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
    @FXML private void handleShowChangePassword() { MainApp.setRoot(MainApp.CHANGE_PASSWORD_VIEW); }
    @FXML private void handleLogout() { MainApp.showLogin(); }
}
