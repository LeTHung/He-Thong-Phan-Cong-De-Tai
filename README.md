# Hệ Thống Phân Công Đề Tài (PTIT Topic)

Ứng dụng desktop JavaFX quản lý việc đăng ký và phân công đề tài tốt nghiệp/đồ
án giữa giảng viên và sinh viên, phục vụ đồ án môn Công nghệ phần mềm. Project
tổ chức theo hướng MVC, kết nối Microsoft SQL Server bằng JDBC thuần (không
ORM) và quản lý build bằng Maven Wrapper.

## Tính Năng Theo Vai Trò

Ứng dụng có 3 vai trò đăng nhập (`tai_khoan.vai_tro`), mỗi vai trò có dashboard
và bộ màn hình riêng:

**Quản trị viên**
- Quản lý tài khoản (thêm/sửa/khóa/mở khóa/reset mật khẩu)
- Quản lý môn học, học kỳ, lớp học phần
- Import sinh viên vào lớp học phần (thủ công hoặc từ file Excel)
- Báo cáo tổng hợp tình trạng đăng ký đề tài theo lớp học phần

**Giảng viên**
- Xem các lớp học phần được phân công, xem sinh viên trong từng lớp
- Quản lý ngân hàng đề tài của riêng mình
- Gán đề tài vào lớp học phần và chọn chế độ phân công (sinh viên tự đăng ký /
  giảng viên phân công / hệ thống tự động)
- Mở/đóng cổng đăng ký theo từng lớp
- Theo dõi kết quả đăng ký, phân công thủ công hoặc tự động cho sinh viên
  chưa có đề tài
- Chốt danh sách cuối kỳ, xuất báo cáo CSV

**Sinh viên**
- Xem danh sách đề tài của lớp học phần đang học, tìm kiếm/lọc/sắp xếp
- Tự đăng ký đề tài (khi đề tài ở chế độ tự đăng ký và cổng đang mở), đổi/hủy
  đăng ký
- Xem thông báo (đề tài mới, được thêm vào lớp, sắp hết hạn đăng ký...)
- Xem lịch sử đăng ký/hủy/phân công và thông tin cá nhân

Tài khoản dùng chung cho mọi vai trò: đổi mật khẩu (mật khẩu được hash bằng
PBKDF2-HMAC-SHA256 với salt riêng; tài khoản cũ dùng SHA-256/rõ sẽ tự nâng cấp
sau lần đăng nhập thành công đầu tiên).

## Công Nghệ

- Java 25
- JavaFX 25.0.3 (`javafx-controls`, `javafx-fxml`)
- Maven 3.9.16 qua `mvnw.cmd` (không cần cài Maven global)
- Microsoft SQL Server + Microsoft JDBC Driver for SQL Server
- Apache POI (đọc file Excel khi import sinh viên)
- Log4j 2
- JUnit 5

## Cấu Trúc Project

```text
src/main/java/com/ptit/doancnpm
  app/                      Launcher + MainApp (điều hướng FXML, hằng số *_VIEW)
  controller/
    admin/                  Controller các màn quản trị viên
    auth/                   Đăng nhập
    lecturer/               Controller các màn giảng viên
    student/                Controller các màn sinh viên
  model/
    dao/                    Lớp truy xuất dữ liệu (JDBC thuần)
    dto/                    Dữ liệu đọc/ghi giữa DAO - service - controller
    entity/                 Entity/enum nghiệp vụ
  service/                  Xử lý nghiệp vụ, validate, gọi DAO
  util/                     Kết nối DB, hash mật khẩu, đọc Excel, helper FXML chung

src/main/resources
  config/                   File cấu hình ứng dụng (db.properties)
  views/
    admin/ auth/ lecturer/ student/   File giao diện FXML theo vai trò
  styles/
    globals/                CSS dùng chung (màu, layout, typography, topbar...)
    pages/                  CSS riêng từng màn hình

sql/
  schema.sql                Tạo database, bảng, trigger, view và stored procedure
  data.sql                  Nạp dữ liệu mẫu để chạy và kiểm thử hệ thống
```

## Cấu Hình Database

Sao chép file mẫu:

```text
src/main/resources/config/database.example.properties
```

thành file cấu hình cục bộ:

```text
src/main/resources/config/db.properties
```

Sau đó cập nhật thông tin SQL Server của máy đang chạy. Ví dụ:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=PhanCongDeTai;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=YOUR_SQL_SERVER_PASSWORD
```

Có thể để trống username/password trong file và truyền bằng biến môi trường
(ưu tiên cao hơn giá trị trong file):

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

`db.properties` không được commit (đã có trong `.gitignore`); mỗi máy tự tạo
file riêng từ file mẫu trên.

## Tạo Database

Chạy lần lượt hai script:

```text
sql/schema.sql
sql/data.sql
```

`schema.sql` tạo mới database `PhanCongDeTai` cùng toàn bộ cấu trúc nghiệp vụ;
`data.sql` nạp các tài khoản và dữ liệu demo.

Nếu dùng `sqlcmd`:

```powershell
sqlcmd -S localhost -E -i sql\schema.sql
sqlcmd -S localhost -E -i sql\data.sql
```

## Chạy Ứng Dụng

Dùng Maven Wrapper có sẵn:

```powershell
.\mvnw.cmd javafx:run
```

Nếu chạy bằng nút Run của IDE, chọn main class:

```text
com.ptit.doancnpm.app.Launcher
```

Không chạy trực tiếp `MainApp` bằng lệnh `java` thủ công, vì JavaFX cần được
Maven hoặc IDE thêm đúng runtime/module path.

## Build Và Kiểm Tra

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd clean test
```

## Lưu Ý JDK

Project dùng Java 25. Trên máy hiện tại, Maven Wrapper đã được cấu hình để ưu
tiên JDK 25 tại:

```text
C:\Program Files\Java\jdk-25.0.3
```

Nếu IDE vẫn nhận JDK cũ, hãy tắt terminal trong IDE hoặc restart IDE.

## Quy Ước Phát Triển

- Màn hình mới: thêm FXML trong `src/main/resources/views/<vai-trò>`, controller
  tương ứng trong `controller/<vai-trò>`. Mỗi FXML khai báo `fx:controller` và
  liên kết `fx:id`/`onAction` phải khớp field/method thật trong controller đó.
- Điều hướng giữa màn hình: thêm hằng số `*_VIEW` trong `MainApp`, gọi
  `MainApp.setRoot(...)`. Khi thêm/xóa màn hình, rà lại sidebar/topbar của các
  màn cùng vai trò để giữ menu điều hướng đồng bộ.
- CSS dùng chung: đặt trong `src/main/resources/styles/globals`.
- CSS riêng màn hình: đặt trong `src/main/resources/styles/pages`.
- Entity mới: đặt trong `model/entity`. DTO mới (dữ liệu tổng hợp cho
  service/controller, không map 1-1 với bảng): đặt trong `model/dto`.
- DAO mới: đặt trong `model/dao`, mở `Connection` bằng
  `DatabaseConnection.getConnection()`, dùng try-with-resources, rollback lỗi
  bằng `DatabaseConnection.rollbackQuietly(connection)`.
- Logic nghiệp vụ/validate: đặt trong `service`, không viết SQL hoặc cập nhật
  UI trực tiếp trong controller.
- Cột số thứ tự (STT) trên bảng: dùng `TableCells.indexColumn()`. Định dạng
  ngày/giờ chung: dùng `DateTimeFormatters.DATE_TIME` /
  `DateTimeFormatters.DATE_ONLY`. Chuỗi có thể null: dùng `TextFormat.orDash()`
  / `TextFormat.emptyIfNull()` — tránh viết lại các hàm helper này trong từng
  controller.
