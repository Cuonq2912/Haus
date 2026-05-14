Xây dựng một ứng dụng Spring Boot 3 quản lý cửa hàng bán hàng điện tử sử dụng Spring 
Data JPA + MySQL. 
Chức năng yêu cầu (Phải làm) 
1. Quản lý Người dùng & Xác thực 
• Đăng ký, Đăng nhập (sử dụng JWT) 
• Phân quyền: ROLE_CUSTOMER và ROLE_ADMIN 
2. Quản lý Danh mục (Category) 
• Thêm, Sửa, Xóa, Lấy danh sách danh mục 
3. Quản lý Sản phẩm (Product) 
• Thêm sản phẩm (có upload nhiều ảnh) 
• Sửa, Xóa, Lấy danh sách sản phẩm (có phân trang) 
• Tìm kiếm theo tên, lọc theo danh mục, khoảng giá 
• Xem chi tiết sản phẩm 
4. Giỏ hàng (Cart) 
• Thêm sản phẩm vào giỏ 
• Xem giỏ hàng, cập nhật số lượng, xóa sản phẩm khỏi giỏ 
5. Quản lý Đơn hàng (Order) 
• Tạo đơn hàng từ giỏ hàng 
• Xem lịch sử đơn hàng của khách hàng 
• Admin: Xem tất cả đơn hàng + Cập nhật trạng thái đơn hàng (PENDING → 
CONFIRMED → SHIPPING → DELIVERED → CANCELLED) 
6. Các yêu cầu kỹ thuật bắt buộc: 
• Sử dụng Spring Data JPA + Hibernate 
• Thiết kế Database hợp lý (ít nhất 7 bảng) 
• Xử lý Validation (@Valid, @NotBlank, @Positive…) 
• Xử lý Exception toàn cục (GlobalExceptionHandler) 
• Trả về Response chuẩn (ResponseEntity + DTO) 
• Sử dụng Lombok 
• Cấu hình trong application.yml 
• Upload và lưu ảnh sản phẩm vào thư mục /uploads 
• Sử dụng DTO + Mapper (MapStruct) 
• Swagger UI (springdoc-openapi) 
• Phân trang + Sort (Pageable) 
• Kiểm tra tồn kho khi đặt hàng 
• Soft Delete (isActive) 
• Audit fields (createdAt, updatedAt) 
Cấu trúc dự án gợi ý 
text 
com.electroshop 
├── config 
├── controller 
├── dto 
├── entity 
├── repository 
├── service 
├── exception 
└── util 
Cách nộp bài
README2.md huong dan chay