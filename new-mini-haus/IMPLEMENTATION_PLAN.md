# Implementation Plan For `new-mini-haus`

## Scope
Xay dung backend Spring Boot 3 quan ly cua hang dien tu theo `DeKT.md`, chi gom cac chuc nang bat buoc:
- Auth JWT noi bo
- Role `ROLE_CUSTOMER`, `ROLE_ADMIN`
- CRUD category
- CRUD product + upload nhieu anh vao `/uploads`
- Cart
- Order tu cart
- Validation, global exception, DTO + MapStruct, Swagger, pageable, soft delete `isActive`, audit fields

Khong dua vao pham vi giai doan nay:
- Keycloak
- OTP
- refresh token cookie
- payment gateway
- promotion
- product variation
- review
- favorite
- parent/subcategory neu khong can cho de bai

## Kien truc muc tieu
- Nen tao mot project backend doc lap trong `new-mini-haus`
- Package goc de xuat: `com.newminihaus`
- Pattern: `controller -> service -> repository -> entity`
- Mapping request/response qua DTO + MapStruct
- Bao mat bang Spring Security + JWT filter
- Cac API tra ve wrapper response thong nhat

## Database Design

### Bang 1: `users`
Muc dich: luu tai khoan dang nhap va role.

Cot chinh:
- `id` bigint PK
- `username` varchar unique not null
- `email` varchar unique not null
- `password` varchar not null
- `full_name` varchar nullable
- `role` enum/string not null
- `is_active` boolean not null default true
- `created_at` datetime not null
- `updated_at` datetime not null

Index can co:
- unique index `uk_users_username`
- unique index `uk_users_email`
- index `idx_users_role`

### Bang 2: `categories`
Muc dich: phan loai san pham.

Cot chinh:
- `id` bigint PK
- `name` varchar unique not null
- `description` text nullable
- `is_active` boolean not null default true
- `created_at`
- `updated_at`

Index can co:
- unique index `uk_categories_name`
- index `idx_categories_is_active`

### Bang 3: `products`
Muc dich: luu san pham.

Cot chinh:
- `id` bigint PK
- `name` varchar not null
- `description` text nullable
- `price` decimal(15,2) not null
- `stock_quantity` int not null
- `category_id` bigint FK not null
- `is_active` boolean not null default true
- `created_at`
- `updated_at`

Index can co:
- index `idx_products_category_id`
- index `idx_products_name`
- index `idx_products_price`
- index `idx_products_is_active`

### Bang 4: `product_images`
Muc dich: luu metadata anh cua product.

Cot chinh:
- `id` bigint PK
- `product_id` bigint FK not null
- `file_name` varchar not null
- `file_path` varchar not null
- `image_url` varchar nullable
- `created_at`
- `updated_at`

Index can co:
- index `idx_product_images_product_id`

### Bang 5: `carts`
Muc dich: moi user co 1 cart.

Cot chinh:
- `id` bigint PK
- `user_id` bigint FK unique not null
- `created_at`
- `updated_at`

Index can co:
- unique index `uk_carts_user_id`

### Bang 6: `cart_items`
Muc dich: cac dong san pham trong gio hang.

Cot chinh:
- `id` bigint PK
- `cart_id` bigint FK not null
- `product_id` bigint FK not null
- `quantity` int not null
- `created_at`
- `updated_at`

Index can co:
- index `idx_cart_items_cart_id`
- index `idx_cart_items_product_id`
- unique index `uk_cart_items_cart_product` tren `(cart_id, product_id)`

### Bang 7: `orders`
Muc dich: don hang cua user.

Cot chinh:
- `id` bigint PK
- `user_id` bigint FK not null
- `order_code` varchar unique not null
- `status` enum/string not null
- `total_amount` decimal(15,2) not null
- `created_at`
- `updated_at`

Index can co:
- unique index `uk_orders_order_code`
- index `idx_orders_user_id`
- index `idx_orders_status`
- index `idx_orders_created_at`

### Bang 8: `order_items`
Muc dich: snapshot san pham trong order.

Cot chinh:
- `id` bigint PK
- `order_id` bigint FK not null
- `product_id` bigint nullable
- `product_name` varchar not null
- `unit_price` decimal(15,2) not null
- `quantity` int not null
- `line_total` decimal(15,2) not null
- `created_at`
- `updated_at`

Index can co:
- index `idx_order_items_order_id`
- index `idx_order_items_product_id`

## Quan he entity
- `User` 1-1 `Cart`
- `User` 1-n `Order`
- `Category` 1-n `Product`
- `Product` 1-n `ProductImage`
- `Cart` 1-n `CartItem`
- `Product` 1-n `CartItem`
- `Order` 1-n `OrderItem`

## Package Structure
```text
com.newminihaus
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── repository
├── service
│   ├── impl
├── mapper
├── security
├── exception
└── util
```

## Base Components Can Tao Truoc

### `BaseEntity`
Dung cho:
- `createdAt`
- `updatedAt`

Khuyen nghi dung:
- `@MappedSuperclass`
- `@CreationTimestamp`
- `@UpdateTimestamp`

### Response wrapper
De xuat:
```json
{
  "status": 200,
  "message": "Success",
  "data": {}
}
```

Can co:
- `ApiResponse<T>`
- `ResponseUtil`

### Exception classes
Can co toi thieu:
- `ResourceNotFoundException`
- `BadRequestException`
- `ConflictException`
- `UnauthorizedException`
- `ForbiddenException`

### `GlobalExceptionHandler`
Bat cac nhom loi:
- validation: `MethodArgumentNotValidException`, `ConstraintViolationException`
- `ResourceNotFoundException`
- `ConflictException`
- `BadRequestException`
- `AccessDeniedException`
- `Exception`

## Security Plan

### Muc tieu
- Auth local, khong Keycloak
- JWT chua user id, username, role
- Bao ve API theo role

### Can tao
- `CustomUserDetailsService`
- `JwtService`
- `JwtAuthenticationFilter`
- `SecurityConfig`
- `UserPrincipal` hoac dung `UserDetails`

### Route policy
Public:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /v3/api-docs/**`
- `GET /swagger-ui/**`
- `GET /swagger-ui.html`
- `GET /uploads/**`
- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `GET /api/v1/products`
- `GET /api/v1/products/{id}`

Customer/Admin:
- cart APIs
- `POST /api/v1/orders`
- `GET /api/v1/orders/my`
- `GET /api/v1/orders/{id}` neu la chu don hoac admin

Admin only:
- category create/update/delete
- product create/update/delete
- `GET /api/v1/orders`
- `PATCH /api/v1/orders/{id}/status`

### JWT payload de xuat
- `sub`: username
- `userId`
- `role`
- `iat`
- `exp`

## API Design

### Auth
#### `POST /api/v1/auth/register`
Request:
- `username`
- `email`
- `password`
- `fullName`

Flow:
1. Validate input
2. Check username/email trung
3. Encode password
4. Create user role `ROLE_CUSTOMER`
5. Create cart cho user
6. Return user response

#### `POST /api/v1/auth/login`
Request:
- `usernameOrEmail`
- `password`

Flow:
1. Tim user theo username hoac email
2. Kiem tra `isActive`
3. Match password
4. Generate JWT
5. Return token + thong tin user

### Category
#### `POST /api/v1/categories`
- Admin
- Check name unique trong category active

#### `PUT /api/v1/categories/{id}`
- Admin
- Check category ton tai va active
- Check ten moi khong trung

#### `DELETE /api/v1/categories/{id}`
- Admin
- Neu category dang duoc product active su dung thi reject hoac chi deactivate sau khi xem xet
- Khuyen nghi: cho soft delete neu khong con product active tham chieu

#### `GET /api/v1/categories`
- Public
- Lay danh sach category active

#### `GET /api/v1/categories/{id}`
- Public
- Lay chi tiet category active

### Product
#### `POST /api/v1/products`
- Admin
- `multipart/form-data`
- Part `request`: JSON DTO
- Part `images`: multiple files

Flow:
1. Validate DTO
2. Check category ton tai va active
3. Save `Product`
4. Validate va save tung image vao `/uploads/products`
5. Save `ProductImage`
6. Return full product response

#### `PUT /api/v1/products/{id}`
- Admin

Flow:
1. Check product active ton tai
2. Update text fields
3. Neu co `imageIdsToDelete`, xoa file va metadata
4. Neu co image moi, upload them
5. Return product response

#### `DELETE /api/v1/products/{id}`
- Admin
- Soft delete `isActive=false`

#### `GET /api/v1/products`
- Public
- Params:
  - `page`
  - `size`
  - `sortBy`
  - `sortDir`
  - `keyword`
  - `categoryId`
  - `minPrice`
  - `maxPrice`

Flow:
- Query chi lay product active
- Ho tro phan trang + sort + filter

#### `GET /api/v1/products/{id}`
- Public
- Lay chi tiet product active + images + category

### Cart
#### `POST /api/v1/cart/items`
- Customer/Admin
- Request: `productId`, `quantity`

Flow:
1. Tim current user
2. Tim cart cua user
3. Check product ton tai, active
4. Check `quantity > 0`
5. Check stock du
6. Neu item da co thi cong don, neu chua co thi tao moi
7. Return cart response

#### `GET /api/v1/cart`
- Lay cart hien tai cua current user

#### `PUT /api/v1/cart/items/{itemId}`
- Cap nhat quantity
- Check item thuoc current user
- Check stock

#### `DELETE /api/v1/cart/items/{itemId}`
- Xoa item khoi cart cua current user

### Order
#### `POST /api/v1/orders`
- Tao order tu toan bo cart hien tai

Flow chi tiet:
1. Tim current user va cart
2. Neu cart rong -> reject
3. Tai lai danh sach product tu DB
4. Check tat ca product van active va du stock
5. Tao `Order` status `PENDING`
6. Tao `OrderItem` snapshot tu cart item
7. Tinh `totalAmount`
8. Tru stock ngay trong transaction
9. Save order + order items
10. Clear cart
11. Return order response

#### `GET /api/v1/orders/my`
- Customer xem lich su don cua minh
- Pageable

#### `GET /api/v1/orders`
- Admin xem tat ca don
- Ho tro filter `status`
- Pageable

#### `GET /api/v1/orders/{id}`
- Customer chi xem duoc don cua minh
- Admin xem duoc moi don

#### `PATCH /api/v1/orders/{id}/status`
- Admin cap nhat status

Transition hop le:
- `PENDING -> CONFIRMED`
- `CONFIRMED -> SHIPPING`
- `SHIPPING -> DELIVERED`
- `PENDING -> CANCELLED`
- `CONFIRMED -> CANCELLED`

Khong hop le:
- `DELIVERED -> CANCELLED`
- `SHIPPING -> PENDING`
- Nhay coc status

Xu ly stock khi cancel:
- Neu da tru stock luc create order, thi khi cancel o `PENDING` hoac `CONFIRMED` can cong stock lai
- Khong cong stock lai neu don da `DELIVERED`

## DTO Plan

### Auth DTO
Request:
- `RegisterRequest`
- `LoginRequest`

Response:
- `AuthResponse`
- `UserResponse`

### Category DTO
Request:
- `CategoryCreateRequest`
- `CategoryUpdateRequest`

Response:
- `CategoryResponse`

### Product DTO
Request:
- `ProductCreateRequest`
- `ProductUpdateRequest`

Response:
- `ProductResponse`
- `ProductImageResponse`
- `PageResponse<ProductResponse>` hoac dung wrapper chung

### Cart DTO
Request:
- `AddCartItemRequest`
- `UpdateCartItemRequest`

Response:
- `CartResponse`
- `CartItemResponse`

### Order DTO
Request:
- `UpdateOrderStatusRequest` hoac `status` query param

Response:
- `OrderResponse`
- `OrderItemResponse`

## Mapper Plan
Can co MapStruct mapper:
- `UserMapper`
- `CategoryMapper`
- `ProductMapper`
- `ProductImageMapper`
- `CartMapper`
- `OrderMapper`
- `OrderItemMapper`

Nguyen tac:
- Mapper khong chua business logic
- Snapshot order item nen map co chu y gan gia tri tu `Product` vao `OrderItem`

## Repository Plan
Can tao:
- `UserRepository`
- `CategoryRepository`
- `ProductRepository`
- `ProductImageRepository`
- `CartRepository`
- `CartItemRepository`
- `OrderRepository`
- `OrderItemRepository`

Query can co san:
- find by username/email active
- find active categories
- search active products theo keyword/category/price
- find cart by user id with items
- find orders by user id pageable
- find all orders pageable + status

Khuyen nghi:
- Product search co the lam bang `JpaSpecificationExecutor<Product>` de de ghep filter

## Upload Plan

### Muc tieu
- Luu file local vao `/uploads/products`
- Luu metadata vao `product_images`
- Expose anh qua static resource

### Can tao
- `FileStorageProperties`
- `FileStorageService`
- `WebMvcConfig` de map `/uploads/**`

### Rule file
- Chi nhan anh hop le: jpg, jpeg, png, webp
- Validate size file
- Tao ten file unique: `uuid-original-name-sanitized`
- Chan path traversal

## Validation Plan

### User
- `username`: `@NotBlank`
- `email`: `@Email`, `@NotBlank`
- `password`: `@NotBlank`, `@Size(min=6)` hoac hon neu can

### Category
- `name`: `@NotBlank`

### Product
- `name`: `@NotBlank`
- `price`: `@Positive`
- `stockQuantity`: `@Min(0)`
- `categoryId`: `@NotNull`

### Cart
- `productId`: `@NotNull`
- `quantity`: `@Positive`

### Order status
- Validate gia tri enum hop le
- Validate transition hop le trong service

## Soft Delete Strategy
Dung `isActive` theo de bai.

Ap dung cho:
- `User`
- `Category`
- `Product`

Rule:
- Query public va business mac dinh chi lay ban ghi `isActive=true`
- Delete API cua category/product thuc chat la deactivate
- Neu user inactive thi khong login duoc

## Audit Strategy
Dung `BaseEntity` cho:
- `User`
- `Category`
- `Product`
- `ProductImage`
- `Cart`
- `CartItem`
- `Order`
- `OrderItem`

## `application.yml` Plan
Can co:
- `server.port`
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`
- `spring.jpa.show-sql`
- `spring.servlet.multipart.max-file-size`
- `spring.servlet.multipart.max-request-size`
- `app.jwt.secret`
- `app.jwt.expiration`
- `app.upload.dir`
- springdoc config neu can

## Giai doan implement

### Giai doan 1: Bootstrap project
Checklist:
- [ ] Tao skeleton Spring Boot 3 trong `new-mini-haus`
- [ ] Them dependency: web, validation, security, data-jpa, mysql, lombok, mapstruct, springdoc
- [ ] Tao `application.yml`
- [ ] Tao package goc `com.newminihaus`

Output verify:
- App start duoc
- Swagger mo duoc

### Giai doan 2: Base framework
Checklist:
- [ ] Tao `BaseEntity`
- [ ] Tao response wrapper
- [ ] Tao exception classes
- [ ] Tao `GlobalExceptionHandler`
- [ ] Tao config upload folder + static resource mapping

Output verify:
- API sample tra dung format response
- Validation loi tra ve on dinh

### Giai doan 3: Security + Auth
Checklist:
- [ ] Tao `User`, `Role`
- [ ] Tao `UserRepository`
- [ ] Tao `CustomUserDetailsService`
- [ ] Tao `JwtService`
- [ ] Tao `JwtAuthenticationFilter`
- [ ] Tao `SecurityConfig`
- [ ] Implement register/login
- [ ] Auto create cart khi register

Output verify:
- Dang ky duoc
- Dang nhap lay JWT duoc
- API protected yeu cau token dung

### Giai doan 4: Category
Checklist:
- [ ] Tao entity/repository/dto/mapper/service/controller cho category
- [ ] CRUD category
- [ ] Soft delete bang `isActive`

Output verify:
- Admin CRUD duoc
- Public list/get duoc

### Giai doan 5: Product + upload anh
Checklist:
- [ ] Tao `Product`, `ProductImage`
- [ ] Tao repository/dto/mapper/service/controller
- [ ] Implement create/update/delete/detail/list
- [ ] Implement upload nhieu anh vao `/uploads/products`
- [ ] Implement pageable + sort + search/filter

Output verify:
- Upload nhieu anh duoc
- Product list loc theo `keyword`, `categoryId`, `minPrice`, `maxPrice`
- Chi lay product active

### Giai doan 6: Cart
Checklist:
- [ ] Tao `CartItem` flow add/get/update/delete
- [ ] Validate ownership theo current user
- [ ] Validate stock khi them/cap nhat

Output verify:
- User thao tac cart duoc
- Khong vuot ton kho

### Giai doan 7: Order
Checklist:
- [ ] Tao `Order`, `OrderItem`
- [ ] Create order tu cart trong transaction
- [ ] Snapshot order item
- [ ] Tru stock
- [ ] Clear cart sau khi tao
- [ ] My order history
- [ ] Admin list all orders
- [ ] Admin update status theo transition hop le
- [ ] Restore stock khi cancel trong truong hop hop le

Output verify:
- Tao order duoc
- Cart bi clear
- Stock thay doi dung
- Role phan biet dung

### Giai doan 8: Hoan thien va tai lieu
Checklist:
- [ ] Rà soat validation va exception
- [ ] Rà soat Swagger annotation
- [ ] Tao `README2.md`
- [ ] Chay build/test

Output verify:
- Build xanh
- README2 du de chay local

## Rui ro ky thuat va cach giam rui ro

### 1. Sai stock do race condition
Rui ro:
- Hai request cung dat 1 san pham.

Giam rui ro:
- Tao order trong transaction.
- Re-check stock ngay truoc khi tru stock.
- Neu can, dung locking o repository cho product khi tao order.

### 2. Upload file gay loi hoac de file rac
Rui ro:
- DB save thanh cong nhung file save that bai, hoac nguoc lai.

Giam rui ro:
- Save file truoc, neu DB fail thi co cleanup file vua tao.
- Khi xoa image, xoa ca metadata va file vat ly.

### 3. Soft delete lam lo du lieu inactive
Rui ro:
- Query quen loc `isActive=true`.

Giam rui ro:
- Dat ten method repository ro rang: `findByIdAndIsActiveTrue`, `findAllByIsActiveTrue`.
- Review toan bo list/detail API.

### 4. Order status update sai transition
Rui ro:
- Admin co the dat status bat hop le neu chi parse enum.

Giam rui ro:
- Tao ham `validateStatusTransition(from, to)` trong service.

### 5. Product bi deactivate nhung cart van tham chieu
Rui ro:
- Cart co item khong con hop le.

Giam rui ro:
- Khi create order va get cart, check lai product con active.
- Co the loai bo item invalid khoi cart khi user mo cart.

## Tieu chi hoan thanh
Plan duoc xem la dat khi implementation sau nay dap ung:
- Swagger chay duoc
- Register/login JWT chay duoc
- Role customer/admin tach ro
- CRUD category chay duoc
- CRUD product + upload nhieu anh chay duoc
- Product list co pageable + search/filter/sort
- Cart day du add/get/update/delete
- Order tao tu cart, check stock, snapshot item, clear cart
- Admin xem all orders va update status dung transition
- Response chuan, validation chuan, exception chuan
- Soft delete `isActive` va audit fields duoc ap dung
- Co `README2.md`

## Thu tu thao tac tiep theo
1. Tao skeleton project `new-mini-haus` theo plan nay.
2. Implement theo giai doan 1 -> 8, moi giai doan build lai 1 lan.
3. Truoc khi sang frontend hoac du lieu seed, chot on dinh toan bo API va Swagger.
