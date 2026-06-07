# Checklist Cho Frontend

## 1. Setup

- [ ] Clone hoặc lấy source frontend.
- [ ] Chạy backend Spring Boot ở `http://localhost:8080`.
- [ ] Dùng session auth, không dùng JWT.
- [ ] Gọi API với `credentials: 'include'`.
- [ ] Đọc response theo envelope `ApiResponse<T>`.

## 2. Những màn hình phải có
-[Landing pages]
- [ ] Login
- [ ] Dashboard theo role
- [ ] Driver
  - [ ] My Vehicles
  - [ ] My Reservations
  - [ ] My Payments
  - [ ] My Incidents
- [ ] Staff
  - [ ] Check-in
  - [ ] Check-out
  - [ ] Active Sessions
  - [ ] Incident Management
- [ ] Manager
  - [ ] Infrastructure Management
  - [ ] Reservation Management
  - [ ] Pricing Management
  - [ ] Reports
  - [ ] User Management
- [ ] System Admin
  - [ ] Full User Management
  - [ ] Full Infrastructure Management
  - [ ] Reports
  - [ ] System Settings

## 3. Điều bắt buộc

- [ ] Route guard theo role.
- [ ] Sidebar / menu thay đổi theo role.
- [ ] Không tự viết lại rule nghiệp vụ của backend.
- [ ] Driver chỉ thấy dữ liệu của chính họ.
- [ ] Manager/Admin mới được thấy màn quản trị toàn cục.

## 4. Flow quan trọng

- [ ] Login xong lấy `GET /api/auth/me`.
- [ ] Giữ session bằng cookie của trình duyệt.
- [ ] Tạo reservation theo `vehicleType`.  Có chức năng chụp ảnh
- [ ] Check-in có thể upload ảnh biển số.
- [ ] Check-out phải dùng `parkingSessionId`.
- [ ] Report chỉ mở cho `MANAGER` và `SYSTEM_ADMIN`.

## 5. Trang test nội bộ nên dùng

- [ ] `/test-hub.html`
- [ ] `/auth-test.html`
- [ ] `/infra-test.html`
- [ ] `/reservation-test.html`
- [ ] `/session-flow-test.html`
- [ ] `/user-test.html`
- [ ] `/vehicle-test.html`
- [ ] `/payment-test.html`
- [ ] `/incident-test.html`
- [ ] `/report-test.html`
- [ ] `/ai-test.html`

