# DoAnCNPM

Project desktop app JavaFX được dựng theo cấu trúc MVC, có sẵn cấu hình kết nối Microsoft SQL Server và Maven Wrapper để chạy project mà không cần cài Maven global.

## Công nghệ sử dụng

- Java 25
- JavaFX 25.0.3
- Maven 3.9.16 thông qua `mvnw.cmd`
- Microsoft SQL Server
- JDBC Driver: `mssql-jdbc`
- JUnit 5 cho kiểm thử

## Cấu trúc thư mục

```text
src/main/java/com/ptit/doancnpm
  app/            Class khoi dong ung dung JavaFX
  controller/     Controller xử lý giao diện JavaFX
  dao/            Lớp truy xuất dữ liệu từ database
  model/          Các class model/entity
  service/        Xử lý nghiệp vụ
  util/           Tiện ích dùng chung, cấu hình DB

src/main/resources
  config/         Thông tin cấu hình ứng dụng, database
  views/          File giao diện FXML
  styles/         File style JavaFX

sql/
  schema.sql      Script tạo database và bảng mẫu
```

## Cấu hình database

Mở file `src/main/resources/config/db.properties` và sửa lại thông tin SQL Server trên máy:

```properties
db.url=jdbc:sqlserver://localhost:1433;databaseName=DoAnCNPM;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=your_password
```

Nếu muốn dùng biến môi trường thay cho file cấu hình, có thể đặt:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

## Tạo database

Chạy script trong file:

```text
sql/schema.sql
```

Script này sẽ tạo database `phan_cong_de_tai_db`, các bảng nghiệp vụ của đề tài, và thêm dữ liệu mẫu.

## Chạy project

Máy không cần cài Maven riêng. Dùng Maven Wrapper có sẵn:

```powershell
.\mvnw.cmd javafx:run
```

Lần chạy đầu tiên Maven sẽ tự tải các thư viện cần thiết như JavaFX, JDBC Driver SQL Server và JUnit.

Nếu chạy bằng nút Run của IDE, hãy chọn main class:

```text
com.ptit.doancnpm.app.Launcher
```

Không nên chạy trực tiếp `com.ptit.doancnpm.MainApp` bằng lệnh `java` thủ công vì JavaFX cần được IDE/Maven thêm đúng runtime/module path.

## Kiểm tra compile/test

```powershell
.\mvnw.cmd clean test
```

## Lưu ý về JDK

Project đang dùng Java 25. Trên máy này Maven Wrapper đã được cấu hình để ưu tiên JDK 25 tại:

```text
C:\Program Files\Java\jdk-25.0.3
```

Nếu mở terminal từ IDE cũ mà vẫn thấy lỗi Java version, hãy tắt terminal hoặc restart IDE để nhận lại biến môi trường mới.

## Điểm bắt đầu phát triển

- Thêm màn hình mới: tạo file FXML trong `src/main/resources/views`, tạo controller trong package `controller`.
- Thêm bảng/model mới: tạo class trong `model`, DAO trong `dao`, service trong `service`.
- Kết nối DB: dùng `DatabaseConnection.getConnection()` trong các class DAO.
