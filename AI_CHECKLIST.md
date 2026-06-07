# Checklist Cho AI

## 1. Setup

- [ ] Dùng backend mock endpoint trước.
- [ ] Không tự quyết định logic nghiệp vụ.
- [ ] Chỉ xử lý nhận diện biển số.
- [ ] Trả kết quả theo contract backend đã định.

## 2. Contract đầu vào

- [ ] Ảnh biển số dưới dạng bytes.
- [ ] Tên file gốc.
- [ ] Content type.
- [ ] Fallback plate number.

## 3. Contract đầu ra

- [ ] `plateNumber`
- [ ] `confidence`
- [ ] `provider`

## 4. Quy tắc bắt buộc

- [ ] AI không chọn slot.
- [ ] AI không chọn zone.
- [ ] AI không tạo reservation.
- [ ] AI không tạo payment.
- [ ] AI không update session state trực tiếp.

## 5. Việc cần làm theo thứ tự

- [ ] Dùng `/api/ai/plate-recognition/mock` để test luồng trước.
- [ ] Làm adapter OCR thật.
- [ ] Giữ format output ổn định.
- [ ] Khi thay model, không làm đổi contract phía backend.

