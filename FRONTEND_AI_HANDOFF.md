# Hướng Dẫn Bàn Giao Cho Frontend Và AI

Tài liệu này mô tả cách thiết lập dự án, cách dùng contract backend, và phần việc mà team frontend và AI cần thực hiện.

## 1. Tổng Quan Dự Án

Backend là một hệ thống quản lý bãi xe viết bằng Spring Boot, dùng đăng nhập theo session và phân quyền theo role.

Phần backend đã hoàn thành trong phạm vi bàn giao hiện tại:

- Xác thực và đăng nhập theo session
- Quản lý tài khoản người dùng
- Quản lý phương tiện
- Quản lý hạ tầng:
  - Building
  - Floor
  - Zone
  - Slot
- Reservation
- Allocation strategy
- Check-in
- Check-out
- Pricing và tính phí
- Payment
- Incident
- Report
- Mock AI nhận diện biển số

Ngoài phạm vi hiện tại:

- Feedback

## 2. Cách Setup Dự Án

### Yêu cầu môi trường

- Java 21 trở lên
- Có thông tin kết nối Supabase PostgreSQL
- Có source dự án Spring Boot

### Biến môi trường backend

Thiết lập các biến sau trước khi chạy backend:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Nếu dùng file `.env`, đặt file đó ở thư mục gốc của project.

### Chạy backend

Chạy ứng dụng Spring Boot, sau đó mở:

- `http://localhost:8080`

Backend có sẵn các trang test HTML trong `src/main/resources/static`.

## 3. Các Trang Test Có Sẵn

Các trang test hiện có:

- `/test-hub.html`
- `/auth-test.html`
- `/infra-test.html`
- `/reservation-test.html`
- `/session-flow-test.html`
- `/user-test.html`
- `/vehicle-test.html`
- `/payment-test.html`
- `/incident-test.html`
- `/report-test.html`
- `/ai-test.html`

Nên mở `/test-hub.html` để vào nhanh tất cả trang test.

## 4. Contract Response Chung

Hầu hết API trả về theo envelope chung:

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "timestamp": "2026-06-07T00:00:00Z",
  "status": 200
}
```

Error response cũng theo format tương tự, chỉ khác `success = false`.

Frontend nên đọc dữ liệu từ `data` thay vì bám trực tiếp vào entity nếu đã có DTO.

## 5. Xác Thực Và Role

### Luồng đăng nhập

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

Hệ thống dùng session phía server, không dùng JWT.

### Tài khoản seed mặc định

- `manager / manager123`
- `staff / staff123`
- `driver / driver123`
- `admin / admin123`

### Các role

- `DRIVER`
- `STAFF`
- `MANAGER`
- `SYSTEM_ADMIN`

### Quy ước hiển thị theo role

- `DRIVER`
  - chỉ thấy dữ liệu cá nhân: reservation, vehicle, payment, profile
- `STAFF`
  - thấy màn hình vận hành: check-in, check-out, incident, payment
- `MANAGER`
  - thấy vận hành, hạ tầng, pricing, report, user management
- `SYSTEM_ADMIN`
  - thấy toàn bộ chức năng quản trị

## 6. Việc Frontend Cần Làm

Frontend nên là một ứng dụng duy nhất, có route theo role và layout thay đổi theo quyền.

### Các màn hình nên có

- Public
  - Login
  - Availability / pricing overview
- Driver
  - Dashboard
  - My Vehicles
  - My Reservations
  - My Payments
  - My Incidents
- Staff
  - Operations Dashboard
  - Check-in
  - Check-out
  - Active Sessions
  - Incident Management
- Manager
  - Manager Dashboard
  - Infrastructure Management
  - Reservation Management
  - Pricing Management
  - Reports
  - User Management
- System Admin
  - Admin Dashboard
  - Full User Management
  - Full Infrastructure Management
  - Reports
  - System Settings

### Kiến trúc frontend nên làm

- Một layout chung
- Sidebar / topbar đổi theo role
- Route guard theo role
- Một lớp API client dùng chung
- Một helper để unwrap `ApiResponse<T>`

### Ưu tiên frontend

1. Login và lưu session
2. Route theo role
3. Reservation cho driver
4. Check-in / check-out cho staff
5. Hạ tầng và report cho manager
6. User và vehicle management

## 7. Danh Sách API Frontend Cần Dùng

### Auth

- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

### User management

- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `PATCH /api/users/{id}/status`
- `PATCH /api/users/{id}/password`

### Vehicle management

- `POST /api/vehicles`
- `GET /api/vehicles/me`
- `GET /api/vehicles`
- `GET /api/vehicles/{id}`
- `PUT /api/vehicles/{id}`
- `DELETE /api/vehicles/{id}`

### Infrastructure

- `POST /api/buildings`
- `GET /api/buildings`
- `GET /api/buildings/{id}`
- `PUT /api/buildings/{id}`
- `DELETE /api/buildings/{id}`
- `POST /api/floors`
- `GET /api/floors`
- `GET /api/floors/{id}`
- `PUT /api/floors/{id}`
- `DELETE /api/floors/{id}`
- `POST /api/zones`
- `GET /api/zones`
- `GET /api/zones/{id}`
- `PUT /api/zones/{id}`
- `DELETE /api/zones/{id}`
- `POST /api/slots`
- `GET /api/slots`
- `GET /api/slots/{id}`
- `PUT /api/slots/{id}`
- `DELETE /api/slots/{id}`

### Reservation

- `POST /api/reservations`
- `GET /api/reservations/me`
- `GET /api/reservations/{id}`
- `DELETE /api/reservations/{id}`
- `GET /api/reservations`
- `GET /api/reservations/availability?buildingCode=BLD-001`

### Session flow

- `POST /api/sessions/check-in`
- `POST /api/sessions/check-out`

### Pricing và fee calculation

- `POST /api/pricing/policies`
- `PUT /api/pricing/policies/{id}`
- `GET /api/pricing/policies/{id}`
- `GET /api/pricing/policies`
- `GET /api/pricing/policies/active/{vehicleType}`
- `POST /api/pricing/fees/calculate`

### Payment

- `POST /api/payments`
- `GET /api/payments/{id}`
- `GET /api/payments/session/{parkingSessionId}`
- `GET /api/payments`

### Incident

- `POST /api/incidents`
- `GET /api/incidents`
- `GET /api/incidents/{id}`
- `GET /api/incidents/session/{parkingSessionId}`
- `PATCH /api/incidents/{id}/close`

### Reports

- `GET /api/reports/dashboard?from=&to=`
- `GET /api/reports/revenue?from=&to=`
- `GET /api/reports/occupancy`
- `GET /api/reports/incidents?from=&to=`

### Mock AI

- `POST /api/ai/plate-recognition/mock`

## 8. Quy Tắc Frontend Cần Tôn Trọng

### Session auth

- Dùng cookie của trình duyệt
- Khi gọi API phải có `credentials: 'include'`
- Không dùng JWT

### Quy tắc cho driver

- Driver chỉ được quản lý xe của chính mình
- Driver chỉ thấy reservation, payment, incident của chính mình

### Quy tắc cho manager và admin

- Manager và admin được quản lý user và hạ tầng
- Chỉ manager và admin được xem danh sách toàn bộ user và toàn bộ vehicle

### Quy tắc reservation

- `CAR` bắt buộc có `zoneId` và `slotId`
- `MOTORBIKE` bắt buộc có `zoneId`
- Kiểm tra thời gian dùng `FutureOrPresent`

### Quy tắc check-in

- AI là tùy chọn, chỉ dùng để nhận diện biển số
- AI không chọn slot hoặc zone
- Backend mới là nơi quyết định allocation

### Quy tắc check-out

- Check-out phải dùng `parkingSessionId` đang ACTIVE
- Phí được tính trong lúc check-out
- Nếu upload ảnh biển số thì gửi lên backend để đối chiếu lại nếu cần

## 9. Việc AI Cần Làm

Team AI chỉ cần bám theo contract nhận diện biển số của backend.

### Contract input

Backend cần:

- ảnh biển số dưới dạng bytes
- tên file gốc
- content type
- fallback plate number

### Contract output

Backend cần AI trả về:

- `plateNumber`
- `confidence`
- `provider`

### Quy tắc AI

- AI chỉ nhận diện biển số
- AI không chọn chỗ đỗ
- AI không sửa reservation, payment hay session
- Toàn bộ workflow nghiệp vụ vẫn do backend xử lý

### Mock endpoint đang có

- `POST /api/ai/plate-recognition/mock`

Nên dùng mock endpoint này trước để frontend và backend làm UI/test trước khi AI thật sẵn sàng.

## 10. Thứ Tự Triển Khai Khuyến Nghị

### Frontend

1. Auth và layout theo role
2. Màn reservation cho driver
3. Màn check-in / check-out cho staff
4. Màn hạ tầng cho manager
5. Màn report
6. Màn user và vehicle management
7. Màn incident và payment

### AI

1. Dùng mock endpoint trước
2. Làm adapter OCR thật
3. Thay mock bằng provider thật
4. Trả về confidence và provider một cách nhất quán

## 11. Ghi Chú Phối Hợp

- Backend là nguồn đúng duy nhất cho rule và validation.
- Frontend không nên tự viết lại logic nghiệp vụ cốt lõi.
- AI không được tự quyết định workflow nghiệp vụ.
- Feedback không nằm trong scope hiện tại.

## 12. File Ngắn Gọn Nên Đọc Trước

- [FRONTEND_CHECKLIST.md](D:\java\parking-system\FRONTEND_CHECKLIST.md)
- [AI_CHECKLIST.md](D:\java\parking-system\AI_CHECKLIST.md)
