package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.entity.Topic;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicBankService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;

public class TopicBankController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Topic> tableView;
    @FXML private TableColumn<Topic, Integer> colStt;
    @FXML private TableColumn<Topic, String> colMaDeTai;
    @FXML private TableColumn<Topic, String> colTenDeTai;
    @FXML private TableColumn<Topic, String> colMoTa;
    @FXML private TableColumn<Topic, String> colNgayTao;
    @FXML private Label lblTotal;

    private final TopicBankService topicBankService = new TopicBankService();
    private int maGiangVien;
    private int maTaiKhoan;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }
        maTaiKhoan = user.getMaTaiKhoan();

        try {
            maGiangVien = topicBankService.findMaGiangVienByTaiKhoan(maTaiKhoan);
        } catch (Exception e) {
            MainApp.showError("Lỗi xác định giảng viên: " + e.getMessage());
            return;
        }

        setupColumns();
        loadData();
        tableView.setPlaceholder(new Label("Bạn chưa có đề tài nào. Nhấn \"+ Thêm mới\" để tạo đề tài."));
    }

    private void setupColumns() {
        colStt.setCellValueFactory(cd ->
                new SimpleIntegerProperty(tableView.getItems().indexOf(cd.getValue()) + 1).asObject());
        colMaDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaDeTaiHeThong()));
        colTenDeTai.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenDeTai()));
        colMoTa.setCellValueFactory(cd -> {
            String moTa = cd.getValue().getMoTa();
            return new SimpleStringProperty(moTa == null ? "" : (moTa.length() > 60 ? moTa.substring(0, 60) + "…" : moTa));
        });
        colNgayTao.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getThoiDiemTao() == null
                        ? ""
                        : java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                .format(cd.getValue().getThoiDiemTao())));
    }

    private void loadData() {
        try {
            List<Topic> items = topicBankService.findByGiangVien(maGiangVien);
            tableView.setItems(FXCollections.observableArrayList(items));
            lblTotal.setText("Tổng: " + items.size() + " đề tài");
        } catch (Exception e) {
            MainApp.showError("Lỗi tải ngân hàng đề tài: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            loadData();
            return;
        }
        try {
            List<Topic> items = topicBankService.searchByName(maGiangVien, kw);
            tableView.setItems(FXCollections.observableArrayList(items));
            lblTotal.setText("Kết quả: " + items.size() + " đề tài");
        } catch (Exception e) {
            MainApp.showError("Lỗi tìm kiếm: " + e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        showTopicDialog(null);
    }

    @FXML
    private void handleEdit() {
        Topic selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MainApp.showInfo("Vui lòng chọn một đề tài để sửa.");
            return;
        }
        showTopicDialog(selected);
    }

    @FXML
    private void handleDelete() {
        Topic selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MainApp.showInfo("Vui lòng chọn một đề tài để xóa.");
            return;
        }

        try {
            if (topicBankService.isAssignedToClass(selected.getMaDeTai())) {
                MainApp.showError("Không thể xóa đề tài \"" + selected.getTenDeTai()
                        + "\" vì đang được gán vào một hoặc nhiều lớp học phần.");
                return;
            }
        } catch (Exception e) {
            MainApp.showError("Lỗi kiểm tra đề tài: " + e.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa đề tài \"" + selected.getTenDeTai() + "\" không?\n(Dữ liệu sẽ bị ẩn, không bị xóa vĩnh viễn)");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                topicBankService.softDelete(selected.getMaDeTai(), maGiangVien);
                loadData();
                MainApp.showInfo("Đã xóa đề tài \"" + selected.getTenDeTai() + "\".");
            } catch (Exception e) {
                MainApp.showError("Lỗi xóa đề tài: " + e.getMessage());
            }
        }
    }

    private void showTopicDialog(Topic existing) {
        boolean isEdit = existing != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Sửa đề tài" : "Thêm đề tài mới");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField txtMa = new TextField(isEdit ? existing.getMaDeTaiHeThong() : "");
        txtMa.setPromptText("VD: DT001");
        TextField txtTen = new TextField(isEdit ? existing.getTenDeTai() : "");
        txtTen.setPromptText("Tên đề tài");
        TextArea txtMoTa = new TextArea(isEdit ? (existing.getMoTa() == null ? "" : existing.getMoTa()) : "");
        txtMoTa.setPromptText("Mô tả");
        txtMoTa.setPrefRowCount(3);
        TextArea txtYeuCau = new TextArea(isEdit ? (existing.getYeuCau() == null ? "" : existing.getYeuCau()) : "");
        txtYeuCau.setPromptText("Yêu cầu");
        txtYeuCau.setPrefRowCount(3);
        TextField txtSoLuong = new TextField(isEdit ? String.valueOf(existing.getSoLuongMacDinh()) : "3");
        txtSoLuong.setPromptText("Số sinh viên tối đa");

        grid.add(new Label("Mã đề tài:"), 0, 0);
        grid.add(txtMa, 1, 0);
        grid.add(new Label("Tên đề tài:"), 0, 1);
        grid.add(txtTen, 1, 1);
        grid.add(new Label("Mô tả:"), 0, 2);
        grid.add(txtMoTa, 1, 2);
        grid.add(new Label("Yêu cầu:"), 0, 3);
        grid.add(txtYeuCau, 1, 3);
        grid.add(new Label("Số SV tối đa:"), 0, 4);
        grid.add(txtSoLuong, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String ma = txtMa.getText().trim().toUpperCase();
            String ten = txtTen.getText().trim();
            String moTa = txtMoTa.getText().trim();
            String yeuCau = txtYeuCau.getText().trim();
            String soLuongStr = txtSoLuong.getText().trim();

            if (ma.isEmpty() || ten.isEmpty()) {
                MainApp.showError("Mã đề tài và tên đề tài không được để trống.");
                return;
            }
            if (ma.length() > 50) {
                MainApp.showError("Mã đề tài không được vượt quá 50 ký tự.");
                return;
            }
            if (ten.length() > 255) {
                MainApp.showError("Tên đề tài không được vượt quá 255 ký tự.");
                return;
            }
            int soLuong;
            try {
                soLuong = Integer.parseInt(soLuongStr);
                if (soLuong <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                MainApp.showError("Số sinh viên tối đa phải là số nguyên dương.");
                return;
            }

            try {
                Integer excludeId = isEdit ? existing.getMaDeTai() : null;
                if (topicBankService.existsByCode(ma, excludeId)) {
                    MainApp.showError("Mã đề tài \"" + ma + "\" đã tồn tại. Vui lòng dùng mã khác.");
                    return;
                }
                if (isEdit) {
                    topicBankService.update(existing.getMaDeTai(), maGiangVien, ma, ten, moTa.isEmpty() ? null : moTa,
                            yeuCau.isEmpty() ? null : yeuCau, soLuong);
                    MainApp.showInfo("Đã cập nhật đề tài thành công.");
                } else {
                    topicBankService.insert(maGiangVien, ma, ten, moTa.isEmpty() ? null : moTa,
                            yeuCau.isEmpty() ? null : yeuCau, soLuong);
                    MainApp.showInfo("Đã thêm đề tài mới thành công.");
                }
                loadData();
            } catch (Exception e) {
                MainApp.showError("Lỗi lưu đề tài: " + e.getMessage());
            }
        }
    }

    @FXML private void handleBack() { MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW); }
    @FXML private void handleNavCourseSections() { MainApp.setRoot(MainApp.LECTURER_COURSE_SECTIONS_VIEW); }
    @FXML private void handleNavAssignTopic() { MainApp.setRoot(MainApp.LECTURER_ASSIGN_TOPIC_TO_CLASS_VIEW); }
    @FXML private void handleNavRegistrationPeriod() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_PERIOD_VIEW); }
    @FXML private void handleNavRegistrationResult() { MainApp.setRoot(MainApp.LECTURER_REGISTRATION_RESULT_VIEW); }
    @FXML private void handleNavFinalReport() { MainApp.setRoot(MainApp.LECTURER_FINAL_REPORT_VIEW); }
}
