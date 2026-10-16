# User Services — BookStore Microservices

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue)

Microservice quản lý người dùng, xác thực và phân quyền cho hệ thống **BookStore**. Cung cấp REST API xử lý đăng ký, đăng nhập (JWT), quản lý hồ sơ cá nhân và các thao tác quản trị tài khoản dành cho ADMIN.

---

## Mục lục

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Usage Examples](#usage-examples)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

---

## Features

| Tính năng | Mô tả |
|---|---|
| **Đăng ký & Đăng nhập** | Tạo tài khoản CUSTOMER, đăng nhập bằng username hoặc email, trả về cặp Access/Refresh Token (JWT) |
| **Xác thực JWT Stateless** | Access Token (24h) + Refresh Token (7 ngày), xác thực mọi request qua `OncePerRequestFilter` mà không cần session phía server |
| **Mật khẩu mạnh (Regex)** | Bắt buộc ≥ 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt, mã hóa BCrypt |
| **Rate Limiting** | Giới hạn 5 lần đăng nhập thất bại / 15 phút bằng thuật toán Token Bucket (Bucket4j), chống brute-force |
| **Quản lý hồ sơ** | Người dùng tự xem, cập nhật thông tin cá nhân và đổi mật khẩu |
| **Quản trị ADMIN** | Xem danh sách user (phân trang, tìm kiếm, lọc), khóa/mở khóa tài khoản, phân quyền vai trò |
| **Bảo vệ nghiệp vụ** | Admin không thể tự khóa hoặc tự hạ quyền chính mình, đảm bảo hệ thống luôn có ít nhất 1 admin hoạt động |
| **Swagger UI** | Tài liệu API tương tác tự động tại `/swagger-ui.html` |

---

## Tech Stack

| Công nghệ | Mục đích sử dụng |
|---|---|
| **Java 21** | Ngôn ngữ chính |
| **Spring Boot 4.1.1** | Framework backend |
| **Spring Security 6** | Xác thực, phân quyền, CORS, xử lý lỗi 401/403 |
| **Spring Data JPA** | ORM, truy vấn CSDL qua Repository pattern |
| **PostgreSQL** | Cơ sở dữ liệu quan hệ |
| **JJWT 0.12.6** | Tạo, ký và xác thực JSON Web Token (HMAC-SHA256) |
| **Bucket4j 8.10** | Rate Limiting theo thuật toán Token Bucket |
| **SpringDoc OpenAPI 2.8** | Tự động sinh Swagger UI & API docs |
| **Lombok** | Giảm boilerplate (getter, setter, builder, constructor) |
| **Maven** | Quản lý dependencies và build |

---

## Project Structure

```
src/main/java/com/bookstore/userservices/
├── config/                     # Cấu hình ứng dụng
│   ├── DataInitializer.java    #   Khởi tạo role mặc định & tài khoản admin ban đầu
│   ├── OpenApiConfig.java      #   Cấu hình Swagger/OpenAPI
│   ├── RateLimitConfig.java    #   Cấu hình Bucket4j rate limiting
│   └── SecurityConfig.java     #   Cấu hình Spring Security, JWT filter chain, CORS
├── controller/                 # Tầng Controller (REST endpoints)
│   ├── AuthController.java     #   Đăng ký, Đăng nhập, Refresh Token
│   ├── UserController.java     #   Hồ sơ cá nhân, Đổi mật khẩu
│   └── AdminUserController.java#   Quản trị user (ADMIN only)
├── dto/                        # Data Transfer Objects
│   ├── request/                #   7 DTO request (Register, Login, ChangePassword, ...)
│   └── response/               #   4 DTO response (ApiResponse, AuthResponse, UserDetail, Page)
├── entity/                     # JPA Entities
│   ├── User.java               #   Bảng users
│   ├── Role.java               #   Bảng roles
│   ├── RoleName.java           #   Enum: ADMIN, STAFF, CUSTOMER
│   └── UserStatus.java         #   Enum: ACTIVE, LOCKED
├── exception/                  # Xử lý ngoại lệ tập trung
│   ├── AppException.java       #   Custom business exception
│   └── GlobalExceptionHandler.java  # @RestControllerAdvice xử lý lỗi toàn cục
├── repository/                 # Tầng Repository (JPA)
│   ├── UserRepository.java     #   Truy vấn bảng users (JOIN FETCH, phân trang)
│   └── RoleRepository.java     #   Truy vấn bảng roles
├── security/                   # Bảo mật JWT
│   ├── JwtService.java         #   Tạo, giải mã, xác thực token
│   ├── JwtAuthenticationFilter.java  # OncePerRequestFilter xác thực mỗi request
│   ├── CustomUserDetails.java  #   Adapter User → UserDetails
│   └── CustomUserDetailsService.java # Load user từ DB cho Security
└── service/                    # Tầng Service (Business Logic)
    ├── AuthService.java        #   Interface nghiệp vụ xác thực
    ├── UserService.java        #   Interface nghiệp vụ người dùng
    ├── RateLimitService.java   #   Interface rate limiting
    └── impl/                   #   Implementations
        ├── AuthServiceImpl.java
        ├── UserServiceImpl.java
        └── RateLimitServiceImpl.java
```

---

## Getting Started

### Yêu cầu hệ thống

| Phần mềm | Phiên bản |
|---|---|
| Java JDK | 21+ |
| Maven | 3.9+ |
| PostgreSQL | 14+ |

### Các bước cài đặt

**1. Clone repository**

```bash
git clone https://github.com/[TODO: username]/user-services.git
cd user-services
```

**2. Tạo database PostgreSQL**

```sql
CREATE DATABASE "QLBookStore";
```

**3. Cấu hình biến môi trường**

Copy file `.env.example` thành `.env` và điền giá trị thực:

```bash
cp .env.example .env
```

Nội dung file `.env`:

```properties
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=QLBookStore
DB_USERNAME=postgres
DB_PASSWORD=your_password_here

# JWT
JWT_SECRET=your_base64_secret_key_at_least_256_bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

**4. Build và chạy**

```bash
# Build project
mvn clean install -DskipTests

# Chạy ứng dụng
mvn spring-boot:run
```

**5. Kiểm tra hoạt động**

- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- Health check: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)

> **Lưu ý**: Khi khởi động lần đầu, `DataInitializer` sẽ tự động tạo 3 vai trò (ADMIN, STAFF, CUSTOMER) và 1 tài khoản admin mặc định (`admin` / `Admin@123456`).

---

## Configuration

### Biến môi trường

| Biến | Bắt buộc | Mặc định | Mô tả |
|---|---|---|---|
| `DB_HOST` | Không | `localhost` | Host của PostgreSQL |
| `DB_PORT` | Không | `5432` | Port của PostgreSQL |
| `DB_NAME` | Không | `QLBookStore` | Tên database |
| `DB_USERNAME` | Không | `postgres` | Username kết nối DB |
| `DB_PASSWORD` | **Có** | `123456` | Password kết nối DB |
| `JWT_SECRET` | **Có** | (fallback có sẵn) | Secret key mã hóa JWT (Base64, ≥ 256 bit) |
| `JWT_EXPIRATION` | Không | `86400000` | Thời hạn Access Token (ms) — mặc định 24 giờ |
| `JWT_REFRESH_EXPIRATION` | Không | `604800000` | Thời hạn Refresh Token (ms) — mặc định 7 ngày |

### Cấu hình trong `application.properties`

| Key | Mặc định | Mô tả |
|---|---|---|
| `server.port` | `8081` | Port chạy service |
| `cors.allowed-origins` | `http://localhost:3000,http://localhost:4200` | Danh sách domain frontend được phép gọi API |
| `rate.limit.login.capacity` | `5` | Số lần đăng nhập tối đa trước khi bị chặn |
| `rate.limit.login.refill-duration-minutes` | `15` | Thời gian chờ reset giới hạn đăng nhập (phút) |
| `admin.default.username` | `admin` | Username tài khoản admin khởi tạo |
| `admin.default.password` | `Admin@123456` | Password tài khoản admin khởi tạo |
| `admin.default.email` | `admin@bookstore.com` | Email tài khoản admin khởi tạo |

---

## API Endpoints

> Tất cả response đều trả về cấu trúc `ApiResponse<T>`:
> ```json
> { "success": true, "message": "...", "data": { ... }, "timestamp": "..." }
> ```

### 1. Authentication (Public — không cần JWT)

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/auth/register` | Đăng ký tài khoản CUSTOMER mới |
| `POST` | `/api/auth/login` | Đăng nhập bằng username/email + password |
| `POST` | `/api/auth/refresh-token` | Cấp Access Token mới bằng Refresh Token |

### 2. User Profile (Yêu cầu JWT — `Bearer Token`)

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/users/me` | Lấy thông tin cá nhân của user đang đăng nhập |
| `PUT` | `/api/users/profile` | Cập nhật hồ sơ (họ tên, email, SĐT, địa chỉ) |
| `PUT` | `/api/users/change-password` | Đổi mật khẩu (cần nhập đúng mật khẩu cũ) |

### 3. Admin User Management (Yêu cầu JWT + role `ADMIN`)

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/admin/users` | Danh sách user (phân trang, tìm kiếm, lọc role/status) |
| `GET` | `/api/admin/users/{userId}` | Xem chi tiết 1 user |
| `PATCH` | `/api/admin/users/{userId}/status` | Khóa / Mở khóa tài khoản |
| `PATCH` | `/api/admin/users/{userId}/role` | Phân quyền vai trò (ADMIN/STAFF/CUSTOMER) |

---

## Usage Examples

### Đăng ký tài khoản mới

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nguyenvana",
    "password": "MyPass@123",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phone": "0912345678",
    "address": "Hà Nội"
  }'
```

**Response** (`201 Created`):

```json
{
  "success": true,
  "message": "Đăng ký tài khoản thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 2,
    "username": "nguyenvana",
    "email": "nguyenvana@example.com",
    "fullName": "Nguyễn Văn A",
    "role": "CUSTOMER"
  },
  "timestamp": "2026-10-16T08:00:00"
}
```

### Đăng nhập

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "admin",
    "password": "Admin@123456"
  }'
```

### Gọi API bảo mật (kèm JWT Token)

```bash
curl -X GET http://localhost:8081/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### Admin — Khóa tài khoản

```bash
curl -X PATCH http://localhost:8081/api/admin/users/2/status \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{ "status": "LOCKED" }'
```

---

## Roadmap

- [ ] Tích hợp Eureka Client cho Service Discovery
- [ ] Tích hợp API Gateway (Spring Cloud Gateway)
- [ ] Cache UserDetails bằng Redis để giảm tải DB
- [ ] Blacklist JWT Token (Redis) cho chức năng Logout thực sự
- [ ] Viết Unit Test & Integration Test (JUnit 5 + Mockito)
- [ ] Upload ảnh đại diện (avatar) cho user
- [ ] Chức năng quên mật khẩu qua email (OTP)

---

## Contributing

1. Fork project
2. Tạo feature branch (`git checkout -b feature/ten-tinh-nang`)
3. Commit thay đổi (`git commit -m 'Thêm tính năng XYZ'`)
4. Push lên branch (`git push origin feature/ten-tinh-nang`)
5. Tạo Pull Request

---

## License

Distributed under the **MIT License**. See `LICENSE` for more information.

---

## Contact

| Thông tin | Chi tiết                   |
|---|----------------------------|
| **Tên** | Nguyễn Quang Huy    |
| **Email** |  nquanghuycv@gmail.com
| **GitHub** | https://github.com/HuyBeros
| **Project Link** |  https://github.com/HuyBeros/UserServices

---

