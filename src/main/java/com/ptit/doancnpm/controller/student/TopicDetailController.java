package com.ptit.doancnpm.controller.student;

import com.ptit.doancnpm.app.MainApp;
import com.ptit.doancnpm.model.dto.RegistrationPeriod;
import com.ptit.doancnpm.model.dto.StudentInfo;
import com.ptit.doancnpm.model.dto.TopicDetail;
import com.ptit.doancnpm.model.dto.TopicMember;
import com.ptit.doancnpm.model.entity.User;
import com.ptit.doancnpm.model.entity.UserRole;
import com.ptit.doancnpm.service.TopicRegistrationService;
import com.ptit.doancnpm.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.Optional;

/**
 * Màn hình chi tiết một đề tài: hiển thị mô tả, yêu cầu, số chỗ và cho phép
 * sinh viên đăng ký ngay trên màn hình này.
 */
public class TopicDetailController {

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblTopicCode;

    @FXML
    private Label lblTopicTitle;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblClass;

    @FXML
    private Label lblSlots;

    @FXML
    private Label lblRemaining;

    @FXML
    private Label lblMode;

    @FXML
    private Label lblLecturer;

    @FXML
    private ListView<String> lstMembers;

    @FXML
    private Label lblPeriod;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblRequirement;

    @FXML
    private Button btnRegister;

    private final TopicRegistrationService topicRegistrationService = new TopicRegistrationService();

    private int maTaiKhoan;
    private int maSinhVien;
    private Integer maDeTaiLop;
    private boolean dangMoDangKy;

    @FXML
    private void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            MainApp.showLogin();
            return;
        }

        if (user.getVaiTro() != UserRole.SINH_VIEN) {
            MainApp.showError("Bạn không có quyền truy cập màn hình đăng ký đề tài.");
            MainApp.showLogin();
            return;
        }

        maTaiKhoan = user.getMaTaiKhoan();
        lblUserInfo.setText(user.getTenDangNhap() + " • " + user.getVaiTro().getDisplayName());

        try {
            StudentInfo info = topicRegistrationService.getStudentInfo(maTaiKhoan);
            maSinhVien = info.maSinhVien();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }

        maDeTaiLop = StudentTopicContext.getSelectedTopic();
        loadDetail();
    }

    private void loadDetail() {
        if (maDeTaiLop == null) {
            lblTopicTitle.setText("Chưa chọn đề tài");
            lblMessage.setText("Hãy quay lại danh sách đề tài và chọn một đề tài để xem chi tiết.");
            btnRegister.setDisable(true);
            return;
        }

        try {
            Optional<TopicDetail> result = topicRegistrationService.getTopicDetail(maDeTaiLop, maTaiKhoan);
            if (result.isEmpty()) {
                lblTopicTitle.setText("Không có quyền xem đề tài này");
                lblMessage.setText("Không tìm thấy đề tài trong lớp học phần của bạn.");
                btnRegister.setDisable(true);
                return;
            }

            TopicDetail detail = result.get();
            lblTopicCode.setText(detail.maDeTaiHeThong());
            lblTopicTitle.setText(detail.tenDeTai());
            lblClass.setText("Lớp học phần: " + nullToDash(detail.maLop()));
            lblSlots.setText(detail.soLuongHienTai() + "/" + detail.soLuongToiDa());
            lblRemaining.setText(String.valueOf(detail.soChoConLai()));
            lblMode.setText(cheDoText(detail.cheDoPhanCong()));
            lblLecturer.setText(nullToDash(detail.tenGiangVien()));
            lblDescription.setText(nullToDash(detail.moTa()));
            lblRequirement.setText(nullToDash(detail.yeuCau()));
            setStatus(detail.trangThai());
            loadPeriod(detail.maLopHocPhan());
            loadMembers(detail.maDeTaiLop());

            boolean canRegister = detail.conCho() && dangMoDangKy;
            btnRegister.setDisable(!canRegister);
            if (!dangMoDangKy) {
                showMessage("Cổng đăng ký hiện không mở nên không thể đăng ký.");
            } else if (!detail.conCho()) {
                showMessage("Đề tài này hiện không nhận đăng ký (đã đủ hoặc đã đóng).");
            } else {
                showMessage("Đề tài còn " + detail.soChoConLai() + " chỗ. Bạn có thể đăng ký.");
            }
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
            btnRegister.setDisable(true);
        }
    }

    private void loadMembers(int maDeTaiLop) {
        try {
            List<TopicMember> members = topicRegistrationService.getTopicMembers(maDeTaiLop);
            if (members.isEmpty()) {
                lstMembers.getItems().setAll("Chưa có sinh viên đăng ký đề tài này.");
                return;
            }
            lstMembers.getItems().setAll(members.stream()
                    .map(member -> member.maSoSinhVien() + " — " + member.hoTen()
                            + " (" + member.hinhThucText() + ")")
                    .toList());
        } catch (RuntimeException exception) {
            lstMembers.getItems().setAll(exception.getMessage());
        }
    }

    private void loadPeriod(int maLopHocPhan) {
        dangMoDangKy = false;
        try {
            Optional<RegistrationPeriod> period = topicRegistrationService.getRegistrationPeriod(maLopHocPhan);
            if (period.isEmpty()) {
                lblPeriod.setText("Chưa mở đợt đăng ký");
                lblPeriod.getStyleClass().setAll("badge", "badge-info");
                return;
            }

            RegistrationPeriod current = period.get();
            dangMoDangKy = current.dangMo();
            lblPeriod.setText(current.moTaTrangThai());
            lblPeriod.getStyleClass().setAll("badge",
                    dangMoDangKy ? (current.sapHetHan() ? "badge-warning" : "badge-success") : "badge-warning");
        } catch (RuntimeException exception) {
            lblPeriod.setText("");
        }
    }

    @FXML
    private void handleRegister() {
        if (maDeTaiLop == null) {
            showMessage("Chưa chọn đề tài để đăng ký.");
            return;
        }
        if (!dangMoDangKy) {
            showMessage("Cổng đăng ký hiện không mở nên không thể đăng ký.");
            return;
        }

        boolean confirmed = confirm("Xác nhận đăng ký đề tài \"" + lblTopicTitle.getText() + "\"?");
        if (!confirmed) {
            return;
        }

        try {
            topicRegistrationService.register(maSinhVien, maDeTaiLop);
            MainApp.showInfo("Đăng ký đề tài thành công.");
            loadDetail();
        } catch (RuntimeException exception) {
            showMessage(exception.getMessage());
        }
    }

    @FXML
    private void handleBackToList() {
        MainApp.setRoot(MainApp.STUDENT_TOPIC_LIST_VIEW);
    }

    @FXML
    private void handleBackDashboard() {
        MainApp.setRoot(MainApp.STUDENT_DASHBOARD_VIEW);
    }

    @FXML
    private void handleShowMyRegistration() {
        MainApp.setRoot(MainApp.STUDENT_MY_REGISTRATION_VIEW);
    }

    @FXML
    private void handleShowChangePassword() {
        MainApp.setRoot("/views/student/change-password.fxml");
    }

    @FXML
    private void handleShowProfile() {
        MainApp.setRoot(MainApp.STUDENT_PROFILE_VIEW);
    }

    @FXML
    private void handleLogout() {
        MainApp.showLogin();
    }

    private void setStatus(String trangThai) {
        String text;
        String style;
        switch (trangThai == null ? "" : trangThai) {
            case "DANG_MO" -> {
                text = "Đang mở";
                style = "badge badge-success";
            }
            case "DA_DU" -> {
                text = "Đã đủ";
                style = "badge badge-warning";
            }
            case "DA_DONG" -> {
                text = "Đã đóng";
                style = "badge badge-info";
            }
            default -> {
                text = nullToDash(trangThai);
                style = "badge badge-info";
            }
        }
        lblStatus.setText(text);
        lblStatus.getStyleClass().setAll(style.split(" "));
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String cheDoText(String cheDo) {
        if (cheDo == null) {
            return "—";
        }
        return switch (cheDo) {
            case "SINH_VIEN_TU_DANG_KY" -> "Sinh viên tự đăng ký";
            case "GIANG_VIEN_PHAN_CONG" -> "Giảng viên phân công";
            default -> cheDo;
        };
    }

    private String nullToDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private void showMessage(String message) {
        lblMessage.setText(message == null ? "" : message);
    }
}
