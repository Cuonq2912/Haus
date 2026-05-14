# Haus Reference For `new-mini-haus` Based On `DeKT.md`

## Muc dich
Tai lieu nay tom tat cac flow va quy uoc trong he thong Haus hien tai de dung lam tham chieu khi xay dung he thong `new-mini-haus` theo yeu cau trong `DeKT.md`.

Muc tieu khong phai copy nguyen Haus. Muc tieu la hoc cach to chuc flow, validation, response, phan quyen, upload anh, gio hang, don hang, audit, soft delete, roi gian luoc cho dung de bai.

## Nguon tham chieu chinh trong Haus
- `src/main/java/com/example/haus/controller/AuthController.java`
- `src/main/java/com/example/haus/controller/CategoryController.java`
- `src/main/java/com/example/haus/controller/ProductController.java`
- `src/main/java/com/example/haus/controller/CartController.java`
- `src/main/java/com/example/haus/controller/OrderController.java`
- `src/main/java/com/example/haus/service/impl/AuthenticationServiceImpl.java`
- `src/main/java/com/example/haus/service/impl/CategoryServiceImpl.java`
- `src/main/java/com/example/haus/service/impl/ProductServiceImpl.java`
- `src/main/java/com/example/haus/service/impl/CartServiceImpl.java`
- `src/main/java/com/example/haus/service/impl/OrderServiceImpl.java`
- `src/main/java/com/example/haus/config/SecurityConfig.java`
- `src/main/java/com/example/haus/exception/GlobalExceptionHandler.java`
- `src/main/java/com/example/haus/domain/entity/BaseEntity.java`
- `src/main/java/com/example/haus/util/UploadFileUtil.java`
- `src/main/java/com/example/haus/base/ResponseUtil.java`
- `src/main/java/com/example/haus/base/RestApiV1.java`

## Tong quan kien truc Haus hien tai
Haus hien tai la mot backend thuong mai dien tu phuc tap hon de bai. Cac diem quan trong:
- Base API dung prefix `/api/v1` qua annotation `@RestApiV1`.
- Response thanh cong duoc dong goi qua `ResponseData<T>` va `ResponseUtil`.
- Security dang dung OAuth2 Resource Server va JWT, nhung auth goc di qua Keycloak.
- Auth khong chi co login/register ma con co refresh token, logout, OTP verify, forgot/reset password.
- Product cua Haus co them `ProductVariation`, `Media`, `Promotion`, `Review`, `Favorite`.
- Cart cua Haus gan 1-1 voi `User`; khi user duoc tao thi cart co the duoc tao san.
- Order cua Haus co `Payment`, `Address`, `Promotion`, xuat hoa don PDF, cap nhat trang thai va dong bo ton kho/sold quantity.
- Audit co `createdAt`, `updatedAt`, `deletedAt` trong `BaseEntity`.
- Soft delete hien tai trong Haus thuong dung `isDeleted`; de bai yeu cau `isActive`, vi vay `new-mini-haus` nen chuan hoa lai theo de bai.

## Mapping chuc nang: `DeKT.md` -> Haus -> `new-mini-haus`

| De bai | Haus hien tai | Cach ap dung cho `new-mini-haus` |
| --- | --- | --- |
| Dang ky, dang nhap JWT | Haus dung Keycloak + JWT + refresh cookie + OTP | Bo Keycloak, tu xay auth local bang Spring Security + JWT |
| ROLE_CUSTOMER, ROLE_ADMIN | Haus co role `USER`, `ADMIN` | Dat ten enum dung de bai: `ROLE_CUSTOMER`, `ROLE_ADMIN` |
| CRUD Category | Haus co `CategoryController`, `CategoryServiceImpl` | Giu flow CRUD, bo bot parent/subcategory neu muon don gian |
| CRUD Product + upload nhieu anh | Haus co `Product`, `Media`, upload util | Tao `ProductImage` rieng, luu vao `/uploads`, khong can Cloudinary |
| Tim kiem, loc, phan trang product | Haus co filter criteria + pageable | Ban mini chi can query theo ten, categoryId, minPrice, maxPrice, sort |
| Cart | Haus co `Cart`, `CartItem`, validate ton kho khi add/update | Giu y tuong nay, nhung chi can product khong can variation |
| Order | Haus co create order, history, admin update status | Giu flow, bo payment/promotion/address phuc tap neu de bai khong yeu cau |
| Validation | Haus dung DTO + annotation | Giu nguyen cach nay |
| Global exception | Haus co `GlobalExceptionHandler` | Giu nguyen y tuong, lam gon lai |
| DTO + Mapper | Haus dung MapStruct | Giu nguyen |
| Swagger | Haus co springdoc | Giu nguyen |
| Soft delete | Haus dung `isDeleted` o nhieu noi | Doi sang `isActive` de dung de bai |
| Audit | Haus co `createdAt`, `updatedAt`, `deletedAt` | Giu `createdAt`, `updatedAt`; `deletedAt` co the co them neu can |

## Nguyen tac quan trong khi tham chieu Haus
1. Khong copy nguyen do phuc tap cua Haus.
- Haus dang giai bai toan lon hon de bai.
- `new-mini-haus` nen uu tien dung, ro, de test, dung dung pham vi `DeKT.md`.

2. Giu flow nghiep vu, khong giu dependency thua.
- Flow tao user -> dang nhap -> cap token -> truy cap API bao ve la huu ich.
- Keycloak, OTP, refresh cookie, review, favorite, payment gateway la phan thua so voi de bai.

3. Chuan hoa ten entity/trang thai theo de bai.
- De bai yeu cau `isActive`, khong phai `isDeleted`.
- De bai yeu cau order status: `PENDING -> CONFIRMED -> SHIPPING -> DELIVERED -> CANCELLED`.
- Haus hien tai co them `PROCESSING`, `COMPLETED`, `RETURNED`, `REFUNDED`; khong nen dua vao mini version.

4. Thiet ke tu dau cho kha nang transaction.
- Add cart, update cart, create order, update order status deu la nhung diem de sinh loi race condition va sai ton kho.
- Can xu ly trong service layer va transaction ro rang.

## Flow 1: Quan ly nguoi dung va xac thuc

### Haus dang lam gi
- `AuthController` tach ro cac action: login, logout, refresh, register, verify OTP, forgot/reset password.
- `AuthenticationServiceImpl` login bang cach goi Keycloak lay token.
- Sau login, Haus decode JWT de lay role va neu local DB chua co user thi tu provision user noi bo va tao cart.
- Refresh token duoc luu trong cookie `HttpOnly`.

### `new-mini-haus` nen ap dung the nao
Dung flow don gian hon:
1. `POST /api/v1/auth/register`
- Nhan `username`, `email`, `password`, thong tin co ban.
- Validate trung `username`/`email`.
- Ma hoa password bang `BCryptPasswordEncoder`.
- Gan role mac dinh `ROLE_CUSTOMER`.
- Tao user active.
- Co the tao cart ngay sau khi tao user.

2. `POST /api/v1/auth/login`
- Nhan `usernameOrEmail` va `password`.
- Xac thuc user local.
- Tao JWT chua `sub`, `role`, `userId`.
- Tra ve response DTO gom token va thong tin user can thiet.

3. Phan quyen
- `ROLE_ADMIN`: CRUD category, CRUD product, xem tat ca order, update order status.
- `ROLE_CUSTOMER`: cart, create order, xem history cua chinh minh.

### Diem can luu y
- Khong can Keycloak cho `new-mini-haus`.
- Khong can OTP, refresh token cookie neu de bai khong yeu cau.
- Neu muon gon hon, login chi tra `accessToken`; chua can refresh token.
- Nen co `JwtAuthenticationFilter` + `SecurityFilterChain` + `UserDetailsService` noi bo.
- Cart nen duoc tao luc register de tranh null flow luc add to cart.

## Flow 2: Quan ly Category

### Haus dang lam gi
- `CategoryController` co them, sua, xoa, get by id, get all, search.
- `CategoryServiceImpl` check trung ten category.
- Khi xoa category, Haus chan xoa neu category dang duoc product su dung.
- Haus co parent/subcategory.

### `new-mini-haus` nen ap dung the nao
De bai chi bat buoc CRUD category, nen co 2 huong:
- Huong don gian nhat: category 1 cap, khong parent.
- Huong mo rong: category co parentId nullable.

Flow toi thieu de khuyen nghi:
1. `POST /api/v1/categories`
- Admin tao category.
- Check trung ten.

2. `PUT /api/v1/categories/{id}`
- Admin cap nhat ten/mo ta.
- Check category ton tai.

3. `DELETE /api/v1/categories/{id}`
- Neu dang co product active thuoc category thi khong xoa hard.
- Neu dung soft delete theo `isActive` thi set `isActive=false`.

4. `GET /api/v1/categories`
- Lay danh sach category active.

### Diem can luu y
- Neu de bai khong bat buoc parent category thi bo de giam do phuc tap.
- Khong nen hard delete category neu product dang tham chieu.
- Neu dung soft delete, truy van product/category mac dinh phai loc `isActive=true`.

## Flow 3: Quan ly Product

### Haus dang lam gi
- `ProductController` nhan `multipart/form-data`, trong do phan request la DTO, phan images la mang file.
- `ProductServiceImpl`:
  - check trung ten product
  - tao `productCode`
  - set audit time
  - mapping category
  - upload nhieu anh
  - luu media gan voi product
  - update product co xoa anh cu, them anh moi
  - delete product bang soft delete (`isDeleted=true`)
- Product listing ho tro pageable, sort, filter va search.

### `new-mini-haus` nen ap dung the nao
Do de bai khong yeu cau `ProductVariation`, ban mini nen don gian hoa nhu sau:
- `Product` gom: `id`, `name`, `description`, `price`, `stockQuantity`, `isActive`, `createdAt`, `updatedAt`, `category`, `images`.
- `ProductImage` gom: `id`, `imageUrl` hoac `fileName`, `product`.

Flow de xuat:
1. `POST /api/v1/products`
- Admin gui `multipart/form-data`.
- `request` la JSON DTO.
- `images` la list `MultipartFile`.
- Validate category ton tai.
- Validate `price > 0`, `stockQuantity >= 0`.
- Luu product truoc, sau do luu cac image.

2. `PUT /api/v1/products/{id}`
- Admin cap nhat thong tin co ban.
- Co the cho xoa anh cu bang danh sach imageIdsToDelete.
- Co the them anh moi.

3. `DELETE /api/v1/products/{id}`
- Khong xoa hard.
- Set `isActive=false`.

4. `GET /api/v1/products`
- Ho tro `page`, `size`, `sort`.
- Ho tro `keyword`, `categoryId`, `minPrice`, `maxPrice`.

5. `GET /api/v1/products/{id}`
- Lay chi tiet product va danh sach anh.

### Diem can luu y
- Haus upload len Cloudinary. `new-mini-haus` theo de bai phai luu vao `/uploads`, nen chi tham chieu flow upload, khong tham chieu storage provider.
- Can quy uoc ro file name an toan, tranh trung ten file, tranh path traversal.
- Nen tach bang `product_images` thay vi nhoi danh sach anh vao 1 cot string.
- Khi search va list, chi lay product `isActive=true`.
- Khi xoa product, nen quyet dinh ro cart item va order history se xu ly ra sao. Kinh nghiem thuc te: order history phai giu snapshot du lieu, khong phu thuoc product dang active hay khong.

## Flow 4: Gio hang

### Haus dang lam gi
- Cart gan 1-1 voi user.
- Add to cart check:
  - user ton tai
  - cart ton tai
  - quantity > 0
  - product variation ton tai
  - quantity trong cart + quantity moi khong vuot inventory
- Update cart cho phep doi variant va doi quantity.
- Remove item va clear cart la action tach rieng.

### `new-mini-haus` nen ap dung the nao
Voi de bai mini, khong can variation. Flow toi thieu:
1. `POST /api/v1/cart/items`
- User gui `productId`, `quantity`.
- Check product ton tai, active, stock du.
- Neu item da co trong cart thi cong don quantity.
- Neu vuot stock thi reject.

2. `GET /api/v1/cart`
- Lay full cart cua current user.

3. `PUT /api/v1/cart/items/{itemId}`
- Cap nhat quantity.
- Neu quantity <= 0 thi reject hoac co the quy uoc xoa item.

4. `DELETE /api/v1/cart/items/{itemId}`
- Xoa 1 san pham khoi gio.

### Diem can luu y
- Cart item nen luu don gia tham chieu hay khong? Goi y: cart chi can tham chieu product + quantity; gia chot cuoi cung nen duoc tinh lai luc tao order.
- Moi thao tac cart phai theo current logged-in user, khong nhan `userId` tu client.
- Can check stock luc add/update cart, nhung check lai lan nua luc create order vi stock co the da thay doi.

## Flow 5: Don hang

### Haus dang lam gi
- `OrderController`/`OrderServiceImpl` tao order tu request tong hop.
- Khi tao order:
  - xac dinh user
  - map order item
  - check inventory cho tung item
  - luu snapshot thong tin product vao `OrderItem`
  - gan payment, address, promotion
  - save order
- Update status co logic cap nhat ton kho/sold quantity tuy theo trang thai.
- Get all orders phan biet admin va user.

### `new-mini-haus` nen ap dung the nao
Flow de bai yeu cau gon hon:
1. `POST /api/v1/orders`
- Tao order tu gio hang current user.
- Doc toan bo cart items cua user.
- Neu gio rong thi reject.
- Re-check stock cho tung item.
- Tao `Order` voi status ban dau `PENDING`.
- Tao `OrderItem` voi snapshot:
  - productId
  - productName
  - unitPrice
  - quantity
  - lineTotal
- Tru ton kho ngay khi dat hang hoac tru khi `CONFIRMED`: can chon 1 chien luoc va giu nhat quan.

Khuyen nghi cho de bai mini:
- Tru stock ngay trong transaction create order.
- Neu sau nay admin `CANCELLED` don thi cong stock lai neu don chua `DELIVERED`.

2. `GET /api/v1/orders/my`
- Khach xem lich su don cua minh.

3. `GET /api/v1/orders`
- Admin xem tat ca don hang.
- Co pageable.
- Co the filter theo status.

4. `PATCH /api/v1/orders/{id}/status`
- Admin cap nhat status theo chuoi hop le:
  - `PENDING -> CONFIRMED`
  - `CONFIRMED -> SHIPPING`
  - `SHIPPING -> DELIVERED`
  - `PENDING/CONFIRMED -> CANCELLED`
- Khong nen cho nhay lung tung giua cac status.

### Diem can luu y
- Haus co nhieu status hon de bai. `new-mini-haus` phai tu gioi han lai.
- Don hang phai luu snapshot, vi ten/gia product co the thay doi sau nay.
- Toan bo create order phai nam trong transaction.
- Sau khi tao order thanh cong, can clear cart hoac xoa cac item da dat.
- Lich su don hang cua khach chi tra ve don cua current user.

## Yeu cau ky thuat tuong ung voi Haus va cach ap dung cho `new-mini-haus`

### 1. Spring Data JPA + Hibernate
- Haus dang dung day du JPA entity/repository/service.
- `new-mini-haus` nen theo cau truc chuan entity -> repository -> service -> controller.

### 2. Thiet ke database hop ly, it nhat 7 bang
Khuyen nghi cho `new-mini-haus`:
1. `users`
2. `categories`
3. `products`
4. `product_images`
5. `carts`
6. `cart_items`
7. `orders`
8. `order_items`

Neu muon mo rong:
- `refresh_tokens`
- `roles` neu khong dung enum

### 3. Validation
Hoc theo Haus:
- DTO request dat `@NotBlank`, `@NotNull`, `@Positive`, `@Min`.
- Khong validate trong controller bang if/else thu cong neu annotation da xu ly duoc.

### 4. Global exception
Hoc theo Haus:
- `@RestControllerAdvice`
- Tach `ResourceNotFoundException`, `InvalidDataException`, validation exception, access denied.

### 5. Response chuan
Haus dang dung wrapper `ResponseData<T>`.
`new-mini-haus` nen co format thong nhat, vi du:
```json
{
  "status": 200,
  "message": "Get product success",
  "data": { }
}
```

### 6. DTO + Mapper
- Haus dung MapStruct.
- `new-mini-haus` nen dung MapStruct ngay tu dau de tranh mapping tay qua nhieu service.

### 7. `application.yml`
- Haus tach config va dung profile.
- `new-mini-haus` nen toi thieu co:
  - datasource
  - jpa
  - jwt secret + expiration
  - upload dir
  - springdoc

### 8. Upload anh vao `/uploads`
- Haus cho tham chieu ve flow upload nhieu file.
- `new-mini-haus` phai doi sang local file system:
  - save file vao `uploads/products/...`
  - luu relative path/URL vao DB
  - expose static resource de xem anh

### 9. Swagger UI
- Haus dang co springdoc-openapi.
- `new-mini-haus` nen mo public Swagger route khi dev.

### 10. Pageable + Sort
- Haus da co `PaginationRequestDto`, `PaginationResponseDto`.
- `new-mini-haus` co the dung truc tiep `Pageable` cua Spring de gon hon.

### 11. Kiem tra ton kho khi dat hang
- Haus check ton kho trong cart va create order.
- `new-mini-haus` bat buoc check lai trong create order du da check khi add cart.

### 12. Soft delete `isActive`
- Haus co `isDeleted`; mini version nen dung `isActive` de dung de bai.
- Khuyen nghi:
  - `User.isActive`
  - `Category.isActive`
  - `Product.isActive`
- Query mac dinh phai bo qua ban ghi inactive.

### 13. Audit fields
- Haus co `BaseEntity` voi `createdAt`, `updatedAt`.
- `new-mini-haus` nen co `BaseEntity` tuong tu va cho cac entity chinh extend no.

## De xuat thiet ke `new-mini-haus` sat de bai nhung hoc tu Haus

## Cac entity nen co
- `User`
- `Category`
- `Product`
- `ProductImage`
- `Cart`
- `CartItem`
- `Order`
- `OrderItem`

## Cac enum nen co
- `Role`: `ROLE_CUSTOMER`, `ROLE_ADMIN`
- `OrderStatus`: `PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`

## Cac package nen co
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
├── exception
├── security
└── util
```

## De xuat endpoint toi thieu
### Auth
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Category
- `POST /api/v1/categories`
- `PUT /api/v1/categories/{id}`
- `DELETE /api/v1/categories/{id}`
- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`

### Product
- `POST /api/v1/products`
- `PUT /api/v1/products/{id}`
- `DELETE /api/v1/products/{id}`
- `GET /api/v1/products`
- `GET /api/v1/products/{id}`

### Cart
- `POST /api/v1/cart/items`
- `GET /api/v1/cart`
- `PUT /api/v1/cart/items/{itemId}`
- `DELETE /api/v1/cart/items/{itemId}`

### Order
- `POST /api/v1/orders`
- `GET /api/v1/orders/my`
- `GET /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `PATCH /api/v1/orders/{id}/status`

## Thu tu trien khai hop ly cho `new-mini-haus`
1. Tao skeleton project, config MySQL, JPA, Swagger, upload dir.
2. Tao base classes: `BaseEntity`, exception, response wrapper.
3. Tao security: JWT, role, filter, config.
4. Tao module auth/user.
5. Tao category.
6. Tao product + upload image.
7. Tao cart.
8. Tao order + order item + transaction + stock.
9. Hoan thien validation, pageable, sort, soft delete.
10. Viet `README2.md` huong dan chay.

## Cac by can tranh khi hoc tu Haus
- Khong dua Keycloak vao mini project neu de bai khong bat buoc.
- Khong dua `ProductVariation`, `Promotion`, `Payment gateway`, `Review`, `Favorite` vao pham vi dau tien.
- Khong de logic stock chi check trong cart; phai check lai luc create order.
- Khong hard delete product/category neu dang co quan he nghiep vu quan trong.
- Khong tra response lung tung tung endpoint; phai thong nhat format.
- Khong de client gui `userId` cho cac API cua current user.
- Khong cap nhat order status tu do; phai validate state transition.

## Prompt de len plan trien khai `new-mini-haus`
```text
Doc file `DeKT.md` va file tham chieu `new-mini-haus/HAUS_REFERENCE_FOR_DEKT.md`. Hay lap mot ke hoach trien khai chi tiet cho he thong `new-mini-haus` bang Spring Boot 3 + Spring Data JPA + MySQL, bam sat de bai trong `DeKT.md` va tham chieu flow/thiet ke trong file md.

Yeu cau plan:
- Chi nam trong pham vi de bai bat buoc, khong mo rong sang cac tinh nang khong can thiet.
- Neu Haus co phan phuc tap hon de bai thi phai chi ro phan nao se bo, phan nao se gian luoc.
- Thiet ke database toi thieu 7 bang va liet ke ro cot chinh, quan he, chi so/index can co.
- Liet ke package structure, entity, DTO, mapper, repository, service, controller can tao.
- Liet ke endpoint API cu the cho auth, category, product, cart, order.
- Mo ta ro flow nghiep vu cho register/login, CRUD category, CRUD product + upload nhieu anh, cart, create order tu cart, order history, admin update order status.
- Mo ta ro chien luoc security JWT, role `ROLE_CUSTOMER` va `ROLE_ADMIN`, route public/protected.
- Mo ta ro validation, global exception, response wrapper, soft delete `isActive`, audit fields `createdAt`/`updatedAt`.
- De xuat thu tu implement theo giai doan sao cho co the test duoc tung phan.
- Neu co rui ro ky thuat hoac diem de sinh loi, phai chi ro va dua cach giam rui ro.
- Output duoi dang markdown, co checklist theo tung giai doan, du chi tiet de co the implement truc tiep.
```

## Prompt de implement theo plan
```text
Doc `DeKT.md`, doc `new-mini-haus/HAUS_REFERENCE_FOR_DEKT.md`, va doc plan trien khai da duoc tao cho `new-mini-haus`. Hay implement toan bo he thong `new-mini-haus` trong workspace hien tai theo dung plan va dung pham vi de bai.

Yeu cau implement:
- Dung Spring Boot 3, Spring Data JPA, MySQL, Lombok, MapStruct, springdoc-openapi.
- Cau hinh bang `application.yml`.
- Tao day du entity, repository, DTO, mapper, service, controller, security, exception handler, response wrapper.
- Auth phai dung JWT noi bo, khong dung Keycloak.
- Phan quyen dung `ROLE_CUSTOMER` va `ROLE_ADMIN`.
- Product phai ho tro upload nhieu anh vao thu muc `/uploads` va luu metadata vao DB.
- Product listing phai co phan trang, tim kiem theo ten, loc theo category, minPrice, maxPrice, va sort co ban.
- Cart phai ho tro them, xem, cap nhat so luong, xoa item.
- Order phai tao tu cart trong transaction, check lai ton kho, luu snapshot order item, xoa/clear cart sau khi tao, cho phep khach xem lich su, admin xem tat ca va cap nhat status theo dung transition `PENDING -> CONFIRMED -> SHIPPING -> DELIVERED -> CANCELLED`.
- Dung validation annotation, global exception handler, response format thong nhat, soft delete bang `isActive`, audit fields `createdAt` va `updatedAt`.
- Tao Swagger UI hoat dong duoc.
- Tao `README2.md` huong dan chay local.
- Neu trong qua trinh implement co diem nao trong plan khong con hop ly voi codebase thuc te, hay tu dieu chinh theo huong don gian hon nhung van dung de bai, roi ghi ro thay doi.
- Sau khi code xong, hay tu chay build/test neu kha thi va bao cao nhung gi da hoan thanh, nhung gi chua the verify.

Lam viec truc tiep tren code, khong chi viet mo ta.
```
