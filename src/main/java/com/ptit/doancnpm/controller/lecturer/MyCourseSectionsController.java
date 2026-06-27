package com.ptit.doancnpm.controller.lecturer;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dao.LecturerDashboardDAO;
import com.ptit.doancnpm.model.dto.LecturerCourseSectionSummary;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MyCourseSectionsController {

    @FXML private TableView<LecturerCourseSectionSummary> tableView;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMaLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colTenLop;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colMonHoc;
    @FXML private TableColumn<LecturerCourseSectionSummary, String> colHocKy;
    @FXML private TableColumn<LecturerCourseSectionSummary, Integer> colSoSV;
    @FXML private Label lblTotalSections;

    private final LecturerDashboardDAO dao = new LecturerDashboardDAO();

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null || user.getVaiTro() != UserRole.GIANG_VIEN) {
            MainApp.showLogin();
            return;
        }

        colMaLop.setCellValueFactory(new PropertyValueFactory<>("maLop"));
        colTenLop.setCellValueFactory(new PropertyValueFactory<>("tenLopHocPhan"));
        colMonHoc.setCellValueFactory(new PropertyValueFactory<>("tenMonHoc"));
        colHocKy.setCellValueFactory(cd -> {
            LecturerCourseSectionSummary s = cd.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    s.tenHocKy() + " " + s.namHoc());
        });
        colSoSV.setCellValueFactory(new PropertyValueFactory<>("tongSoSinhVien"));

        loadData(user.getMaTaiKhoan());
    }

    private void loadData(int maTaiKhoan) {
        try {
            List<LecturerCourseSectionSummary> sections = dao.findCourseSectionsByAccountId(maTaiKhoan);
            tableView.setItems(FXCollections.observableArrayList(sections));
            lblTotalSections.setText("Tổng: " + sections.size() + " lớp học phần");
        } catch (Exception e) {
            MainApp.showError("Lỗi tải danh sách lớp học phần: " + e.getMessage());
        }
    }

    @FXML private void handleBack() {
        MainApp.setRoot(MainApp.LECTURER_DASHBOARD_VIEW);
    }
}
