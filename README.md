# Hệ Thống Phân Công Đề Tài

Ứng dụng desktop JavaFX phục vụ đồ án Công nghệ phần mềm. Project được tổ chức theo hướng MVC, kết nối Microsoft SQL Server bằng JDBC và quản lý thư viện bằng Maven Wrapper.

## Công Nghệ

- Java 25
- JavaFX 25.0.3
- Maven 3.9.16 qua `mvnw.cmd`
- Microsoft SQL Server
- Microsoft JDBC Driver for SQL Server
- JUnit 5

## Cấu Trúc Project

```text
src/main/java/com/ptit/doancnpm
  app/                 Class khởi động ứng dụng
  controller/          Controller cho màn hình JavaFX
  model/
    dao/               Lớp truy xuất dữ liệu
    entity/            Entity/model nghiệp vụ
  service/             Xử lý nghiệp vụ
  util/                Cấu hình và kết nối database

src/main/resources
  config/              File cấu hình ứng dụng
  views/               File giao diện FXML
  styles/
    globals/           CSS dùng chung
    pages/             CSS riêng từng màn hình

sql/
  schema.sql           Tạo database, bảng, trigger, view và stored procedure
  data.sql             Nạp dữ liệu mẫu để chạy và kiểm thử hệ thống
```

## Cấu Hình Database

Sao chép file mẫu:

```text
src/main/resources/config/db.example.properties
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

Có thể để trống username/password trong file và truyền bằng biến môi trường:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

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

Dùng Maven Wrapper có sẵn, không cần cài Maven global:

```powershell
.\mvnw.cmd javafx:run
```

Nếu chạy bằng nút Run của IDE, chọn main class:

```text
com.ptit.doancnpm.app.Launcher
```

Không chạy trực tiếp `MainApp` bằng lệnh `java` thủ công, vì JavaFX cần được Maven hoặc IDE thêm đúng runtime/module path.

## Kiểm Tra Build

```powershell
.\mvnw.cmd clean test
```

Mật khẩu mới được lưu bằng PBKDF2-HMAC-SHA256 với salt riêng. Tài khoản dùng
mật khẩu rõ/SHA-256 từ dữ liệu cũ sẽ được nâng cấp tự động sau lần đăng nhập
thành công đầu tiên.

## Lưu Ý JDK

Project dùng Java 25. Trên máy hiện tại, Maven Wrapper đã được cấu hình để ưu tiên JDK 25 tại:

```text
C:\Program Files\Java\jdk-25.0.3
```

Nếu IDE vẫn nhận JDK cũ, hãy tắt terminal trong IDE hoặc restart IDE.

## Quy Ước Phát Triển

- Màn hình mới: thêm FXML trong `src/main/resources/views`, controller trong `controller`.
- CSS dùng chung: đặt trong `src/main/resources/styles/globals`.
- CSS riêng màn hình: đặt trong `src/main/resources/styles/pages`.
- Entity mới: đặt trong `model/entity`.
- DAO mới: đặt trong `model/dao`.
- Logic nghiệp vụ: đặt trong `service`.
- Kết nối database: dùng `DatabaseConnection.getConnection()` trong DAO.
