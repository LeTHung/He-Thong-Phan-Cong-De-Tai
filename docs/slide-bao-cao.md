---
marp: true
theme: default
paginate: true
size: 16:9
header: "Hệ Thống Phân Công Đề Tài — UniTopics"
---

<!-- _paginate: false -->
<!-- _header: "" -->

# HỆ THỐNG PHÂN CÔNG ĐỀ TÀI
## UniTopics — Ứng dụng quản lý & đăng ký đề tài

Đồ án môn **Công nghệ phần mềm**

**Nhóm thực hiện:**
- Lê T. Hưng — N23DCCN023 (Phân hệ Quản trị)
- Đỗ Cao Cường — N23DCCN007 (Phân hệ Sinh viên)
- Quốc — N23DCAT056 (Phân hệ Giảng viên)

GVHD: *(điền tên giảng viên)*

---

## Nội dung báo cáo

1. Giới thiệu đề tài
2. Mục tiêu dự án
3. Nội dung dự án
4. Giới thiệu hệ thống & người dùng
5. Các chức năng chính
6. Demo các chức năng
7. Kết luận & hướng phát triển

---

## 1. Giới thiệu đề tài

- **Bối cảnh:** việc phân công và đăng ký đề tài môn học / đồ án ở nhiều lớp vẫn làm **thủ công** (giấy, Excel, nhắn tin).
- **Hạn chế hiện tại:**
  - Dễ **trùng đề tài**, khó kiểm soát số lượng sinh viên / đề tài.
  - **Thiếu minh bạch** về số chỗ còn lại, hạn đăng ký.
  - Tốn thời gian tổng hợp, dễ sai sót.
- **Ý tưởng:** xây dựng phần mềm **quản lý tập trung** việc phân công & đăng ký đề tài giữa giảng viên và sinh viên.

---

## 2. Mục tiêu dự án

- **Số hóa** toàn bộ quy trình: tạo đề tài → mở đợt đăng ký → sinh viên đăng ký → chốt kết quả.
- Hiển thị **số chỗ còn lại theo thời gian thực**, tránh trùng và quá tải.
- **Phân quyền** rõ ràng theo 3 vai trò: Quản trị – Giảng viên – Sinh viên.
- Đảm bảo **ràng buộc nghiệp vụ ngay tại CSDL** (không cho đăng ký sai quy định).
- Có **lịch sử thao tác, thông báo và báo cáo thống kê**.

---

## 3. Nội dung dự án

Các nội dung chính đã thực hiện:

- Khảo sát & **phân tích nghiệp vụ** phân công đề tài.
- **Thiết kế CSDL** SQL Server: 13 bảng + view + stored procedure + trigger.
- Xây dựng **ứng dụng desktop JavaFX** theo mô hình **MVC**.
- Phát triển **3 phân hệ**: Quản trị, Giảng viên, Sinh viên.
- **Kiểm thử** chức năng & nghiệp vụ trên dữ liệu mẫu.

---

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Ngôn ngữ | Java 25 |
| Giao diện | JavaFX 25 (FXML + CSS) |
| Cơ sở dữ liệu | Microsoft SQL Server (JDBC) |
| Quản lý thư viện | Maven |
| Kiến trúc | MVC + phân lớp Service / DAO |

---

## Kiến trúc hệ thống (MVC phân lớp)

```text
   View (FXML + CSS)          ← giao diện người dùng
        ↓ ↑
   Controller                 ← xử lý sự kiện màn hình
        ↓ ↑
   Service                    ← xử lý nghiệp vụ
        ↓ ↑
   DAO (JDBC)                 ← truy xuất dữ liệu
        ↓ ↑
   SQL Server                 ← bảng, view, stored procedure, trigger
```

---

## 4. Giới thiệu hệ thống — Xây dựng cho ai?

- **Đối tượng áp dụng:** khoa / bộ môn cần tổ chức phân công đề tài môn học, đồ án.
- **3 nhóm người dùng (actor):**

| Vai trò | Mô tả |
|---|---|
| 👨‍💼 Quản trị viên | Quản lý danh mục & tài khoản hệ thống |
| 👨‍🏫 Giảng viên | Tạo đề tài, mở đợt đăng ký, chốt kết quả |
| 🎓 Sinh viên | Xem, đăng ký & theo dõi đề tài |

---

## 5. Các chức năng — 👨‍💼 Quản trị viên

- Đăng nhập, **phân quyền theo vai trò**
- **Dashboard** tổng quan hệ thống
- Quản lý **tài khoản** (tạo / sửa / khóa, cấp vai trò)
- Quản lý **môn học**, **học kỳ**
- Quản lý **lớp học phần** (gán môn – giảng viên – học kỳ)
- **Import sinh viên** vào lớp
- **Báo cáo / thống kê**

---

## Các chức năng — 👨‍🏫 Giảng viên

- **Ngân hàng đề tài** (tạo, chỉnh sửa đề tài)
- **Gán đề tài vào lớp học phần** (số lượng, chế độ phân công)
- Tạo & quản lý **đợt đăng ký** (thời gian mở / đóng)
- Xem **kết quả đăng ký** theo lớp
- **Phân công thủ công / chốt kết quả** cuối kỳ

---

## Các chức năng — 🎓 Sinh viên *(phân hệ trọng tâm)*

- **Trang chủ:** tổng quan theo **từng lớp học phần**, đếm ngược hạn đăng ký
- 🔔 **Thông báo** khi giảng viên thêm đề tài mới
- **Danh sách đề tài:** tìm kiếm, lọc (còn chỗ / trạng thái / lớp), sắp xếp, phân trang
- **Chi tiết đề tài:** mô tả, yêu cầu, GVHD, nhóm cùng đề tài
- **Đăng ký / đổi / hủy** đề tài (kiểm soát qua stored procedure)
- **Đề tài đã chọn**, **lịch sử** đăng ký
- Thông tin cá nhân, đổi mật khẩu

---

## Điểm nổi bật về nghiệp vụ (CSDL)

- `sp_dang_ky_de_tai` / `sp_huy_dang_ky_de_tai`: đảm bảo **còn chỗ, đúng đợt, mỗi SV 1 đề tài / lớp**.
- **Trigger** tự cập nhật số lượng & **chặn vượt quá** khi đăng ký.
- **View** `vw_de_tai_con_cho`: tính **số chỗ còn lại** realtime.
- Ghi **nhật ký**: `lich_su_dang_ky`, `nhat_ky_he_thong`.

> Logic quan trọng đặt ở CSDL → dữ liệu luôn đúng dù thao tác từ nhiều màn hình.

---

## 6. Demo — Tài khoản đăng nhập

| Vai trò | Tài khoản | Mật khẩu |
|---|---|---|
| Quản trị viên | `admin` | `123456` |
| Giảng viên | `gv01` | `123456` |
| Sinh viên | `N23DCCN007` | `123456` |

**Chạy ứng dụng:**

```powershell
.\mvnw.cmd javafx:run
```

---

## Demo — Kịch bản Quản trị viên

1. Đăng nhập `admin` → xem **Dashboard**.
2. Quản lý **môn học / học kỳ / lớp học phần**.
3. **Import sinh viên** vào lớp.
4. Xem **báo cáo / thống kê**.

---

## Demo — Kịch bản Sinh viên *(trọng tâm)*

1. Đăng nhập `N23DCCN007` → **Trang chủ** (chọn lớp, xem 🔔 thông báo).
2. **Danh sách đề tài** → tìm / lọc / sắp xếp → **xem chi tiết**.
3. **Đăng ký** đề tài → kiểm tra **"Đề tài đã chọn"**.
4. **Đổi / hủy** đề tài → xem **Lịch sử đăng ký**.
5. **Thông báo đề tài mới:** giảng viên thêm đề tài → chuông hiện số mới.

---

## Demo — Kịch bản Giảng viên

- Tạo đề tài trong **ngân hàng đề tài**.
- **Gán đề tài vào lớp**, đặt số lượng & chế độ phân công.
- **Mở đợt đăng ký** → sinh viên đăng ký → **xem kết quả**.

> *Phân hệ Giảng viên đang hoàn thiện — phần demo tập trung Quản trị + Sinh viên; chức năng Giảng viên minh họa qua thiết kế màn hình & dữ liệu mẫu.*

---

## 7. Kết luận & hướng phát triển

**Đã đạt được:**
- Hệ thống phân công đề tài **3 vai trò**, nghiệp vụ chặt chẽ tại CSDL.
- Phân hệ Sinh viên & Quản trị hoạt động đầy đủ trên dữ liệu thật.

**Hướng phát triển:**
- Hoàn thiện đầy đủ phân hệ **Giảng viên**.
- **Phân công tự động** theo nguyện vọng (thuật toán xếp).
- Thông báo **realtime / email**.
- Phát triển **phiên bản web**.

---

<!-- _paginate: false -->

# Cảm ơn thầy/cô và các bạn đã lắng nghe!

### Q & A
