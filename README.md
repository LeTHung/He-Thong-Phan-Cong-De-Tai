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
  schema.sql           Script tạo database, bảng và dữ liệu mẫu
```

## Cấu Hình Database

File cấu hình nằm tại:

```text
src/main/resources/config/db.properties
```

Nội dung mẫu:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=phan_cong_de_tai_db;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=123456
```

Có thể để trống username/password trong file và truyền bằng biến môi trường:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

## Tạo Database

Chạy script:

```text
sql/schema.sql
```

Script sẽ tạo database `phan_cong_de_tai_db`, các bảng nghiệp vụ và dữ liệu mẫu cho hệ thống phân công đề tài.

Nếu dùng `sqlcmd`:

```powershell
sqlcmd -S localhost -E -i sql\schema.sql
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
