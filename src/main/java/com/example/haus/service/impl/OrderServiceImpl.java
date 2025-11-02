package com.example.haus.service.impl;

import com.example.haus.constant.AddressType;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.OrderStatus;
import com.example.haus.domain.dto.order.OrderAllRequestDto;
import com.example.haus.domain.dto.order.OrderItemRequestDto;
import com.example.haus.domain.dto.order.OrderRequestDto;
import com.example.haus.domain.dto.order.PaymentRequestDto;
import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.response.invoice.InvoiceItemDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.product.OrderResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.domain.entity.address.Address;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.OrderItem;
import com.example.haus.domain.entity.product.ProductVariation;
import com.example.haus.domain.entity.product.Promotion;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentStatus;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.entity.user.User;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.domain.mapper.*;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.*;
import com.example.haus.service.OrderService;
import com.example.haus.util.PaginationUtil;
import com.example.haus.util.PdfUtil;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "ORDER-SERVICE")
public class OrderServiceImpl implements OrderService {

    AddressMapper addressMapper;

    AddressRepository addressRepository;

    OrderItemMapper orderItemMapper;

    OrderRepository orderRepository;

    OrderMapper orderMapper;

    PaymentMapper paymentMapper;

    ProductMapper productMapper;

    UserMapper userMapper;

    ProductVariationMapper productVariationMapper;

    PromotionMapper promotionMapper;

    MediaMapper mediaMapper;

    UserRepository userRepository;

    PaymentRepository paymentRepository;

    OrderItemRepository orderItemRepository;

    ProductVariationRepository productVariationRepository;

    PromotionRepository promotionRepository;

    private static double totalPrice = 0;


    @Override
    @Transactional(readOnly = true)
    public PaginationResponseDto<OrderResponseDto> getAllOrders(PaginationRequestDto paginationRequest, String status) {

        Pageable pageable = PageRequest.of(
                paginationRequest.getPageNum(),
                paginationRequest.getPageSize());

        Page<Order> orderPage;

        if (status != null && !status.trim().isEmpty()) {
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidDataException(ErrorMessage.Order.ERR_INVALID_ORDER_STATUS);
            }
            orderPage = orderRepository.findByStatus(orderStatus, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        List<OrderResponseDto> orderResponseList = orderPage.getContent().stream()
                .map(this::convertToOrderResponseDto)
                .toList();

        return PaginationUtil.createPaginationResponse(orderPage, paginationRequest, orderResponseList);
    }

    @Override
    public OrderResponseDto getOrderById(Long id) {

        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        return orderMapper.orderToOrderResponse(order);

    }

    @Override
    @Transactional
    public OrderResponseDto updateStatusOrderById(Long id, String status) {
        if (id == null || id <= 0) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        if (status == null || status.trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED);
        }

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidDataException(ErrorMessage.Order.ERR_INVALID_ORDER_STATUS);
        }

        order.setStatus(orderStatus);

        Payment payment = order.getPayment();
        if (payment != null) {
            switch (orderStatus) {
                case PENDING, PROCESSING, DELIVERED, CONFIRMED -> payment.setStatus(PaymentStatus.PENDING);
                case COMPLETED, RETURNED -> payment.setStatus(PaymentStatus.COMPLETED);
                case CANCELLED -> payment.setStatus(PaymentStatus.CANCELLED);
                case REFUNDED -> payment.setStatus(PaymentStatus.REFUNDED);
                default -> {
                    throw new InvalidDataException(ErrorMessage.Payment.STATUS_IS_NOT_SUPPORT);
                }
            }
        }

        Order updatedOrder = orderRepository.save(order);

        return convertToOrderResponseDto(updatedOrder);
    }

    private OrderResponseDto convertToOrderResponseDto(Order order) {
        OrderResponseDto dto = OrderResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .deliveryDate(order.getDeliveryDate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        if (order.getUser() != null) {
            dto.setUser(convertToUserInfo(order.getUser()));
        }

        if (order.getPromotion() != null) {
            dto.setPromotion(convertToPromotionInfo(order.getPromotion()));
        }

        if (order.getPayment() != null) {
            dto.setPayment(convertToPaymentInfo(order.getPayment()));
        }

        return dto;
    }

    private OrderResponseDto.UserInfo convertToUserInfo(User user) {
        return OrderResponseDto.UserInfo.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    private OrderResponseDto.PromotionInfo convertToPromotionInfo(Promotion promotion) {
        return OrderResponseDto.PromotionInfo.builder()
                .code(promotion.getPromotionCode())
                .discountPercent(promotion.getDiscountPercent() != null
                        ? promotion.getDiscountPercent().intValue()
                        : null)
                .build();
    }

    private OrderResponseDto.PaymentInfo convertToPaymentInfo(Payment payment) {
        return OrderResponseDto.PaymentInfo.builder()
                .amount(payment.getAmount())
                .type(payment.getType())
                .status(payment.getStatus())
                .build();
    }

    @Override
    public Long createOrder(String username, OrderAllRequestDto orderAllRequestDto) {
        if (userRepository.existsUserByUsernameAndIsDeletedFalse(username)) {
            throw new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED);
        }

        User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElseThrow(
                () -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED)
        );

        Order order = orderMapper.orderRequestDtoToOrder(orderAllRequestDto.getOrder());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = orderAllRequestDto.getOrderItems().stream().map(orderItemRequestDto -> {
            ProductVariation productVariation = productVariationRepository.findByIdAndIsDeletedFalse(orderItemRequestDto.getProductVariationId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Product.ERR_PRODUCT_VARIATION_NOT_EXISTED));

            OrderItem orderItem = orderItemMapper.orderItemRequestDtoToOrderItem(orderItemRequestDto);
            orderItem.setProductVariation(productVariation);
            return orderItemRepository.save(orderItem);
        }).toList();


        Payment payment = paymentMapper.paymentRequestDtoToPayment(orderAllRequestDto.getPaymentRequestDto());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(orderAllRequestDto.getOrder().getTotalAmount());
        payment.setExpireAt(new Date(System.currentTimeMillis() + 1000 * 60 * 20));

        Promotion promotion = promotionRepository.findByIdAndIsDeletedFalse(orderAllRequestDto.getOrder().getPromotionId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Promotion.ERR_PROMOTION_NOT_EXISTED));

        List<Address> addresses = orderAllRequestDto.getOrder().getAddresses().stream().map(addressRequestDto -> {
            Address address = addressRepository.findByIdAndIsDeletedFalse(addressRequestDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Address.ERR_ADDRESS_NOT_FOUND));
            address.setIsSelected(addressRequestDto.getIsSelected());
            address.setType(AddressType.ORDER);
            return address;
        }).toList();

        //Relation

        order.setUser(user);
        order.setOrderItems(orderItems);
        order.setPayment(paymentRepository.save(payment));
        order.setPromotion(promotion);
        order.setAddresses(addresses);

        return orderRepository.save(order).getId();
    }

    @Override
    @jakarta.transaction.Transactional
    public InvoiceResponseDto getInvoiceDetails(Long orderId) {

        Order order = orderRepository.findOrderDetailsForInvoice(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_COMPLETED);
        }

        User user = order.getUser();
        Payment payment = order.getPayment();

        List<InvoiceItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> {
                    Double total = item.getQuantity() * item.getPriceAtSale();

                    return InvoiceItemDto.builder()
                            //Product
                            .productId(item.getProductVariation().getProduct().getId())
                            .productCode(item.getProductVariation().getProduct().getProductCode())
                            .productName(item.getProductVariation().getProduct().getProductName())
                            .description(item.getProductVariation().getProduct().getDescription())

                            //Product variant
                            .productVariationId(item.getProductVariation().getId())
                            .inventoryQuantity(item.getQuantity())
                            .total(total)
                            .color(item.getProductVariation().getColor())
                            .size(item.getProductVariation().getSize())
                            .price(item.getPriceAtSale())
                            .media(mediaMapper.mediaToMediaResponse(item.getProductVariation().getMedia()))
                            .build();
                })
                .toList();

        InvoiceResponseDto.InvoiceResponseDtoBuilder builder = InvoiceResponseDto.builder();

        builder.responseDto(orderMapper.orderToOrderResponseDto(order));
        builder.user(userMapper.userToUserResponseDto(user));
        builder.payment(paymentMapper.paymentToPaymentResponseDto(payment));

        if (order.getPromotion() != null) {
            builder.promotion(promotionMapper.promotionToPromotionResponseDto(order.getPromotion()));
        }

        builder.items(itemDtos);

        return builder.build();
    }

    // Trong OrderServiceImpl.java

    @Override
    @jakarta.transaction.Transactional
    public byte[] generateInvoicePdf(Long orderId) throws DocumentException, IOException {
        InvoiceResponseDto invoiceData = getInvoiceDetails(orderId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Kích thước A4
        Document document = new Document(PageSize.A4, 30, 30, 15, 15);
        PdfWriter.getInstance(document, baos);
        document.open();

        // --- 1. CHUẨN BỊ FONT ---
        // Trong generateInvoicePdf(Long orderId)

// --- 1. CHUẨN BỊ FONT ---
// ĐỔI sang tải từ Classpath
        try (InputStream is = getClass().getResourceAsStream("/fonts/font-UTF-8.ttf")) {
            if (is == null) {
                // Log lỗi hoặc ném ngoại lệ nếu font không được tìm thấy
                log.error("Font file not found in classpath: /fonts/font-UTF-8.ttf");
                throw new IOException("Font file not found.");
            }
            byte[] fontData = is.readAllBytes();
            String FONT_NAME_FOR_ITEXT = "font-UTF-8.ttf";

            // Tải BaseFont từ byte array
            BaseFont baseFont = BaseFont.createFont(
                    FONT_NAME_FOR_ITEXT,
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontData,
                    null);

            Font titleFont = new Font(baseFont, 20, Font.BOLD, BaseColor.BLUE);
            Font subTitleFont = new Font(baseFont, 14, Font.BOLD, BaseColor.BLACK);
            Font normalFont = new Font(baseFont, 14, Font.NORMAL, BaseColor.BLACK);
            Font boldFont = new Font(baseFont, 14, Font.BOLD, BaseColor.BLACK);
            Font smallNormalFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.BLACK);
            Font smallBoldFont = new Font(baseFont, 12, Font.BOLD, BaseColor.BLACK);

            // --- 2. XÂY DỰNG CẤU TRÚC (SỬ DỤNG TABLE CHÍNH) ---
            // Sử dụng một PdfPTable chính để kiểm soát toàn bộ bố cục
            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setWidthPercentage(100);
//        mainTable.getDefaultCell().setBorderWidth(10f);
            mainTable.getDefaultCell().setBorder(Rectangle.BOX); // Thêm viền ngoài cho toàn bộ hóa đơn
            mainTable.getDefaultCell().setPadding(0);

            // Thêm các phần tử vào mainTable
            mainTable.addCell(createHeaderCell(invoiceData, titleFont, normalFont, baseFont));
            mainTable.addCell(createSellerBuyerInfoCell(invoiceData, boldFont, normalFont, smallNormalFont));
            mainTable.addCell(createItemsTable(invoiceData, smallBoldFont, smallNormalFont, smallBoldFont));
            mainTable.addCell(createTotalAndSignatureCell(invoiceData, boldFont, normalFont, smallNormalFont));

            document.add(mainTable);
            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            // Xử lý lỗi IO
            throw new IOException("Failed to load font for PDF generation.", e);
        }

    }

    private PdfPCell createHeaderCell(InvoiceResponseDto data, Font titleFont, Font normalFont, BaseFont baseFont) throws DocumentException, IOException {
        // 3 cột: Logo, Tiêu đề, Số Serial
        PdfPTable headerTable = new PdfPTable(3);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{2f, 5f, 2.5f});

        // 1. Logo Cell (Cột 1)
        // Thường sử dụng Image.getInstance() nếu có logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setBorder(Rectangle.NO_BORDER);
        PdfUtil.addImage(logoCell, normalFont);

        // 2. Title Cell (Cột 2)
        PdfPTable titleSubTable = new PdfPTable(1);
        titleSubTable.setWidthPercentage(100);

        // Tiêu đề
        Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(PdfUtil.createCell(title, Rectangle.NO_BORDER));

        // Sub Title
        Paragraph subTitle = new Paragraph("SALES INVOICE", normalFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(PdfUtil.createCell(subTitle, Rectangle.NO_BORDER));

        // Ngày tháng
        LocalDate orderDate = data.getResponseDto().getOrderDate();
        String dateStr = String.format("Ngày(day) %d tháng(month) %d năm(year) %d",
                orderDate.getDayOfMonth(), orderDate.getMonthValue(), orderDate.getYear()); // Lấy từ Order date của bạn
        Paragraph date = new Paragraph(dateStr, new Font(baseFont, 10, Font.NORMAL));
        date.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(PdfUtil.createCell(date, Rectangle.NO_BORDER));

        PdfPCell titleCell = PdfUtil.createCell(titleSubTable, Rectangle.NO_BORDER);

        // 3. Serial Cell (Cột 3)
        PdfPTable serialSubTable = new PdfPTable(1);
        Paragraph serial = new Paragraph("Mẫu số (Serial No.): 2C25TTU", new Font(baseFont, 8, Font.NORMAL));
        Paragraph invoiceNo = new Paragraph("Số hóa đơn (Invoice No.): " + data.getResponseDto().getId(), new Font(baseFont, 8, Font.BOLD));
        serialSubTable.addCell(PdfUtil.createCell(serial, Rectangle.NO_BORDER, Element.ALIGN_CENTER));
        serialSubTable.addCell(PdfUtil.createCell(invoiceNo, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        //QR CODE
        Font smallNormalFont = new Font(baseFont, 6, Font.NORMAL, BaseColor.BLACK);
        PdfUtil.generateQrCode(serialSubTable, data, smallNormalFont);

        PdfPCell serialCell = PdfUtil.createCell(serialSubTable, Rectangle.NO_BORDER);
        serialCell.setVerticalAlignment(Element.ALIGN_TOP);
        serialCell.setPaddingTop(10);

        headerTable.addCell(logoCell);
        headerTable.addCell(titleCell);
        headerTable.addCell(serialCell);

        // Cell chứa toàn bộ header table
        PdfPCell mainCell = new PdfPCell(headerTable);
        mainCell.setBorder(Rectangle.BOTTOM); // Chỉ có viền dưới
        mainCell.setPadding(5);
        return mainCell;
    }

    private PdfPCell createSellerBuyerInfoCell(InvoiceResponseDto data, Font boldFont, Font normalFont, Font smallNormalFont) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(1);
        infoTable.setWidthPercentage(100);

        // Hàm tiện ích để thêm cặp Label: Value
        BiConsumer<PdfPTable, String> addRow = (table, text) -> {
            try {
                PdfPCell cell = PdfUtil.createCell(new Paragraph(text, smallNormalFont), Rectangle.NO_BORDER, Element.ALIGN_LEFT);
                cell.setPaddingTop(1);
                cell.setPaddingRight(1);
                cell.setPaddingBottom(1);
                table.addCell(cell);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        };

        // --- 1. THÔNG TIN NGƯỜI BÁN (SELLER) ---
        PdfPTable sellerTable = new PdfPTable(new float[]{4f, 6f});
        sellerTable.setWidthPercentage(100);

        // Tiêu đề
        Paragraph titleSeller = new Paragraph("Người bán (Seller)", boldFont);
        titleSeller.setAlignment(Element.ALIGN_LEFT);
        sellerTable.addCell(PdfUtil.createCell(titleSeller, Rectangle.NO_BORDER, 2));

        // Dữ liệu người bán (Giả sử bạn có dữ liệu người bán từ cấu hình/hệ thống)
        // Ví dụ:
        addRow.accept(sellerTable, "Mã số thuế: " + "TAX" + data.getResponseDto().getId()); // Sử dụng tạm order ID
        addRow.accept(sellerTable, "MST (Tax Code): 0110329220");
        addRow.accept(sellerTable, "Tên đơn vị (Seller): CÔNG TY TNHH NỘI THẤT HAUS");
        addRow.accept(sellerTable, "Địa chỉ (Address): Nhà lô B11, số 9A, ngõ 181 đường Xuân Thủy, Cầu Giấy, Hà Nội");

        PdfPCell sellerCell = PdfUtil.createCell(sellerTable, Rectangle.NO_BORDER);
        sellerCell.setPaddingTop(5);
        sellerCell.setPaddingRight(5);
        sellerCell.setPaddingBottom(5);
        infoTable.addCell(sellerCell);

        // --- 2. THÔNG TIN NGƯỜI MUA (BUYER) ---
        PdfPTable buyerTable = new PdfPTable(new float[]{4f, 6f});
        buyerTable.setWidthPercentage(100);

        // Tiêu đề
        Paragraph buyerTitle = new Paragraph("Người mua (Co. name)", boldFont);
        buyerTable.addCell(PdfUtil.createCell(buyerTitle, Rectangle.NO_BORDER, 2));

        // Dữ liệu người mua (Lấy từ order.getUser())
        UserResponseDto user = data.getUser();

        addRow.accept(buyerTable, "Tên khách hàng (Full name customer): " + user.getFirstName() + " " + user.getLastName());
        addRow.accept(buyerTable, "Điện thoại (Phone number): " + user.getPhone());
        addRow.accept(buyerTable, "Email/Facebook: " + user.getEmail());
        addRow.accept(buyerTable, "Địa chỉ (Address): " + user.getUsername());

        PdfPCell buyerCell = PdfUtil.createCell(buyerTable, Rectangle.NO_BORDER);
        buyerCell.setPaddingTop(5);
        buyerCell.setPaddingRight(5);
        buyerCell.setPaddingBottom(5);
        infoTable.addCell(buyerCell);

        PdfPCell mainCell = new PdfPCell(infoTable);
        mainCell.setBorder(Rectangle.BOTTOM); // Chỉ có viền dưới
        mainCell.setPaddingBottom(5);
        return mainCell;
    }

    private PdfPCell createItemsTable(InvoiceResponseDto data, Font headerFont, Font normalFont, Font boldFont) throws DocumentException {
        // 6 cột: STT, Tên hàng, ĐVT, SL, Đơn giá, Thành tiền
        PdfPTable itemsTable = new PdfPTable(6);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{0.7f, 4f, 1f, 1.3f, 1.5f, 2f});
        itemsTable.setSpacingBefore(0f);
        itemsTable.setSpacingAfter(0f);

        // --- Header Row ---
        // Tạo 2 hàng header để giống mẫu

        // Hàng 1: Tiêu đề cột chính
        PdfUtil.addCellWithBorder(itemsTable, "STT", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        PdfUtil.addCellWithBorder(itemsTable, "Tên hàng, dịch vụ\n(Name of good or services)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        PdfUtil.addCellWithBorder(itemsTable, "ĐVT\n(Unit)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        PdfUtil.addCellWithBorder(itemsTable, "Số lượng\n(Quantity)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        PdfUtil.addCellWithBorder(itemsTable, "Đơn giá\n(Unit Price)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        PdfUtil.addCellWithBorder(itemsTable, "Thành tiền\n(Amount)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);

        // --- Data Rows ---
        int count = 1;
        double subTotal = 0;
        for (InvoiceItemDto item : data.getItems()) {
            subTotal += item.getTotal();

            // Cột 1
            PdfUtil.addCellWithBorder(itemsTable, String.valueOf(count++), normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 2 (Tên SP + Biến thể)
            String productName = item.getProductName() + " (" + item.getColor() + "/" + item.getSize() + ")";
            PdfUtil.addCellWithBorder(itemsTable, productName, normalFont, Rectangle.BOX, Element.ALIGN_LEFT, 0, 0);

            // Cột 3 (ĐVT) - Giả sử là "Sản phẩm" hoặc "Khóa"
            PdfUtil.addCellWithBorder(itemsTable, "Sản phẩm", normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 4 (SL)
            PdfUtil.addCellWithBorder(itemsTable, String.valueOf(item.getInventoryQuantity()), normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 5 (Đơn giá)
            PdfUtil.addCellWithBorder(itemsTable, String.format("%,.0f", item.getPrice()), normalFont, Rectangle.BOX, Element.ALIGN_RIGHT, 0, 0);

            // Cột 6 (Thành tiền)
            PdfUtil.addCellWithBorder(itemsTable, String.format("%,.0f", item.getTotal()), normalFont, Rectangle.BOX, Element.ALIGN_RIGHT, 0, 0);
        }

        // --- Empty Rows (Giống mẫu) ---
        // Thêm các hàng trống để làm đầy trang (tùy chọn)
        for (int i = 0; i < (data.getItems().size() < 5 ? 5 - data.getItems().size() : 1); i++) {
            for (int j = 0; j < 6; j++) {
                PdfUtil.addCellWithBorder(itemsTable, " ", normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);
            }
        }

        // --- Tổng cộng (Total Row) ---
        totalPrice = (subTotal - (data.getPromotion() != null ? data.getPromotion().getDiscountPercent() : 0L) + data.getResponseDto().getShippingFee());

        // Total price
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng: (Total price):", boldFont));
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setColspan(5);
        totalLabelCell.setBorder(Rectangle.BOX);
        totalLabelCell.setPadding(7);
        itemsTable.addCell(totalLabelCell);

        PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%,.0f", subTotal), normalFont));
        totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueCell.setBorder(Rectangle.BOX);
        totalValueCell.setPadding(7);
        itemsTable.addCell(totalValueCell);

        // Discount Percent
        if (data.getPromotion() != null) {
            PdfPCell discountCell = new PdfPCell(new Phrase("Giảm giá (Discount)", boldFont));
            discountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            discountCell.setColspan(5);
            discountCell.setBorder(Rectangle.BOX);
            discountCell.setPadding(7);
            itemsTable.addCell(discountCell);

            double discountValue = data.getPromotion().getDiscountPercent() * subTotal;
            PdfPCell discountValueCell = new PdfPCell(new Phrase("-" + String.format("%,.0f", discountValue), normalFont));
            discountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            discountValueCell.setBorder(Rectangle.BOX);
            discountValueCell.setPadding(7);
            itemsTable.addCell(discountValueCell);
        }

        // Shipping fee
        PdfPCell shippingFeeCell = new PdfPCell(new Phrase("Phí vận chuyển (Shipping fee)", boldFont));
        shippingFeeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        shippingFeeCell.setColspan(5);
        shippingFeeCell.setBorder(Rectangle.BOX);
        shippingFeeCell.setPadding(7);
        itemsTable.addCell(shippingFeeCell);

        PdfPCell shippingFeeValueCell = new PdfPCell(new Phrase(String.format("%,.0f", data.getResponseDto().getShippingFee()), normalFont));
        shippingFeeValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        shippingFeeValueCell.setBorder(Rectangle.BOX);
        shippingFeeValueCell.setPadding(7);
        itemsTable.addCell(shippingFeeValueCell);

        // Total price
        PdfPCell totalFinalLabelCell = new PdfPCell(new Phrase("Tổng cộng tiền cần thanh toán (Total payment):", boldFont));
        totalFinalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalFinalLabelCell.setColspan(5);
        totalFinalLabelCell.setBorder(Rectangle.BOX);
        totalFinalLabelCell.setPadding(7);
        itemsTable.addCell(totalFinalLabelCell);

        PdfPCell totalValueFinalLabelCell = new PdfPCell(new Phrase(String.format("%,.0f", totalPrice), normalFont));
        totalValueFinalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValueFinalLabelCell.setBorder(Rectangle.BOX);
        totalValueFinalLabelCell.setPadding(7);
        itemsTable.addCell(totalValueFinalLabelCell);

        //Payment method
        PdfPCell paymentMethodLabelCell = new PdfPCell(new Phrase("Phương thức thanh toán (Payment method):", boldFont));
        paymentMethodLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        paymentMethodLabelCell.setColspan(5);
        paymentMethodLabelCell.setBorder(Rectangle.BOX);
        paymentMethodLabelCell.setPadding(7);
        itemsTable.addCell(paymentMethodLabelCell);

        PdfPCell paymentMethodValueCell = new PdfPCell(new Phrase(data.getPayment().getType() == PaymentType.ONLINE_PAYMENT ? "Thanh toán bằng ngân hàng" : "Thanh toán khi nhân hàng", normalFont));
        paymentMethodValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        paymentMethodValueCell.setBorder(Rectangle.BOX);
        paymentMethodValueCell.setPadding(7);
        itemsTable.addCell(paymentMethodValueCell);

        // Bọc bảng trong một cell chính
        PdfPCell mainCell = new PdfPCell(itemsTable);
        mainCell.setBorder(Rectangle.NO_BORDER);
        mainCell.setPadding(0);
        return mainCell;
    }

    private PdfPCell createTotalAndSignatureCell(InvoiceResponseDto data, Font boldFont, Font normalFont, Font smallNormalFont) throws DocumentException {
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{5f, 5f});

        // --- Cột 1: Số tiền viết bằng chữ và Người mua ---
        PdfPTable leftTable = new PdfPTable(1);
        leftTable.setWidthPercentage(100);

        // Chuyển số thành chữ (Cần một hàm tiện ích để làm điều này, ở đây ta giả lập)
        String amountInWords = PdfUtil.convert((long) totalPrice);
        Paragraph words = new Paragraph("Số tiền viết bằng chữ (Amount in words): " + amountInWords, smallNormalFont);
        leftTable.addCell(PdfUtil.createCell(words, Rectangle.NO_BORDER, Element.ALIGN_LEFT));

        Paragraph buyerTitle = new Paragraph("Người mua hàng (Buyer)", boldFont);
        buyerTitle.setAlignment(Element.ALIGN_CENTER);
        buyerTitle.setSpacingBefore(15f);
        leftTable.addCell(PdfUtil.createCell(buyerTitle, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Vùng chữ ký người mua (để trống)
        leftTable.addCell(PdfUtil.createCell(new Phrase("\n\n\n", normalFont), Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        PdfPCell leftCell = PdfUtil.createCell(leftTable, Rectangle.NO_BORDER);

        // --- Cột 2: Người bán và Chữ ký điện tử ---
        PdfPTable rightTable = new PdfPTable(1);
        rightTable.setWidthPercentage(100);

        // Người bán
        Paragraph sellerTitle = new Paragraph("Người bán hàng (Seller)", boldFont);
        sellerTitle.setAlignment(Element.ALIGN_CENTER);
        sellerTitle.setSpacingBefore(15f);
        sellerTitle.setSpacingAfter(15f);
        rightTable.addCell(PdfUtil.createCell(sellerTitle, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Tên công ty (Chữ ký)
        Paragraph companyName = new Paragraph("CÔNG TY TNHH NỘI THẬT HAUS", boldFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        rightTable.addCell(PdfUtil.createCell(companyName, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Ngày ký
        LocalDate currentDate = LocalDate.now();
        Paragraph signDate = new Paragraph(String.format("Ngày: %d/%d/%d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear()), smallNormalFont);
        signDate.setAlignment(Element.ALIGN_CENTER);
        rightTable.addCell(PdfUtil.createCell(signDate, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        PdfPCell rightCell = PdfUtil.createCell(rightTable, Rectangle.NO_BORDER);

        footerTable.addCell(leftCell);
        footerTable.addCell(rightCell);

        // Ghi chú cuối cùng (Cần một bảng 1 cột)
        Paragraph note = new Paragraph("(Cần kiểm tra đối chiếu khi giao, nhận hóa đơn)", smallNormalFont);
        note.setAlignment(Element.ALIGN_CENTER);
        PdfPCell cellBottom = PdfUtil.createCell(note, Rectangle.NO_BORDER, Element.ALIGN_TOP);
        footerTable.addCell(cellBottom);

        // Cell chính
        PdfPCell mainCell = new PdfPCell();
        mainCell.addElement(footerTable);
        mainCell.addElement(new Paragraph("\n")); // Khoảng cách
        mainCell.setBorder(Rectangle.TOP); // Chỉ có viền trên
        mainCell.setPaddingTop(5);
        mainCell.setPaddingRight(5);
        return mainCell;
    }
}
