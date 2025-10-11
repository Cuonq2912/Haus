package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.domain.dto.response.invoice.InvoiceItemDto;
import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.domain.entity.product.Order;
import com.example.haus.domain.entity.product.payment.Payment;
import com.example.haus.domain.entity.product.payment.PaymentType;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.*;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.OrderRepository;
import com.example.haus.service.OrderService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

@Service
@Slf4j(topic = "JWT-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;

    OrderMapper orderMapper;

    PaymentMapper paymentMapper;

    ProductMapper productMapper;

    UserMapper userMapper;

    ProductVariationMapper productVariationMapper;

    PromotionMapper promotionMapper;

    MediaMapper mediaMapper;

    @Override
    @Transactional
    public InvoiceResponseDto getInvoiceDetails(Long orderId) {

        Order order = orderRepository.findOrderDetailsForInvoice(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.Order.ERR_ORDER_NOT_EXISTED));

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
    @Transactional
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
            mainTable.addCell(createItemsTable(invoiceData, smallBoldFont, smallNormalFont, boldFont));
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
        headerTable.setWidths(new float[]{2f, 6f, 3f});

        // 1. Logo Cell (Cột 1)
        // Thường sử dụng Image.getInstance() nếu có logo
        PdfPCell logoCell = new PdfPCell();
        logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setBorder(Rectangle.NO_BORDER);
        addImage(logoCell, normalFont);

        // 2. Title Cell (Cột 2)
        PdfPTable titleSubTable = new PdfPTable(1);
        titleSubTable.setWidthPercentage(100);

        // Tiêu đề
        Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(createCell(title, Rectangle.NO_BORDER));

        // Sub Title
        Paragraph subTitle = new Paragraph("SALES INVOICE", normalFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(createCell(subTitle, Rectangle.NO_BORDER));

        // Ngày tháng
        LocalDate orderDate = data.getResponseDto().getOrderDate();
        String dateStr = String.format("Ngày(day) %d tháng(month) %d năm(year) %d",
                orderDate.getDayOfMonth(), orderDate.getMonthValue(), orderDate.getYear()); // Lấy từ Order date của bạn
        Paragraph date = new Paragraph(dateStr, normalFont);
        date.setAlignment(Element.ALIGN_CENTER);
        titleSubTable.addCell(createCell(date, Rectangle.NO_BORDER));

        PdfPCell titleCell = createCell(titleSubTable, Rectangle.NO_BORDER);

        // 3. Serial Cell (Cột 3)
        PdfPTable serialSubTable = new PdfPTable(1);
        Paragraph serial = new Paragraph("Mẫu số - Ký hiệu (Serial No.): 2C25TTU", new Font(baseFont, 8, Font.NORMAL));
        Paragraph invoiceNo = new Paragraph("Số hóa đơn (Invoice No.): " + data.getResponseDto().getId(), new Font(baseFont, 8, Font.BOLD));
        serialSubTable.addCell(createCell(serial, Rectangle.NO_BORDER, Element.ALIGN_CENTER));
        serialSubTable.addCell(createCell(invoiceNo, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        PdfPCell serialCell = createCell(serialSubTable, Rectangle.NO_BORDER);
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
                PdfPCell cell = createCell(new Paragraph(text, smallNormalFont), Rectangle.NO_BORDER, Element.ALIGN_LEFT);
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
        sellerTable.addCell(createCell(titleSeller, Rectangle.NO_BORDER, 2));

        // Dữ liệu người bán (Giả sử bạn có dữ liệu người bán từ cấu hình/hệ thống)
        // Ví dụ:
        addRow.accept(sellerTable, "Mã số thuế: " + "TAX" + data.getResponseDto().getId()); // Sử dụng tạm order ID
        addRow.accept(sellerTable, "MST (Tax Code): 0110329220");
        addRow.accept(sellerTable, "Tên đơn vị (Seller): CÔNG TY TNHH NỘI THẤT HAUS");
        addRow.accept(sellerTable, "Địa chỉ (Address): Nhà lô B11, số 9A, ngõ 181 đường Xuân Thủy, Cầu Giấy, Hà Nội");

        PdfPCell sellerCell = createCell(sellerTable, Rectangle.NO_BORDER);
        sellerCell.setPaddingTop(5);
        sellerCell.setPaddingRight(5);
        sellerCell.setPaddingBottom(5);
        infoTable.addCell(sellerCell);

        // --- 2. THÔNG TIN NGƯỜI MUA (BUYER) ---
        PdfPTable buyerTable = new PdfPTable(new float[]{4f, 6f});
        buyerTable.setWidthPercentage(100);

        // Tiêu đề
        Paragraph buyerTitle = new Paragraph("Người mua (Co. name)", boldFont);
        buyerTable.addCell(createCell(buyerTitle, Rectangle.NO_BORDER, 2));

        // Dữ liệu người mua (Lấy từ order.getUser())
        UserResponseDto user = data.getUser();

        addRow.accept(buyerTable, "Tên khách hàng (Full name customer): " + user.getFirstName() + " " + user.getLastName());
        addRow.accept(buyerTable, "Điện thoại (Phone number): " + user.getPhone());
        addRow.accept(buyerTable, "Email/Facebook: " + user.getEmail());
        addRow.accept(buyerTable, "Địa chỉ (Address): " + user.getUsername());

        PdfPCell buyerCell = createCell(buyerTable, Rectangle.NO_BORDER);
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
        addCellWithBorder(itemsTable, "STT", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        addCellWithBorder(itemsTable, "Tên hàng, dịch vụ\n(Name of good or services)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        addCellWithBorder(itemsTable, "ĐVT\n(Unit)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        addCellWithBorder(itemsTable, "Số lượng\n(Quantity)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        addCellWithBorder(itemsTable, "Đơn giá\n(Unit Price)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);
        addCellWithBorder(itemsTable, "Thành tiền\n(Amount)", headerFont, Rectangle.BOX, Element.ALIGN_CENTER, 2, 1);

        // --- Data Rows ---
        int count = 1;
        double subTotal = 0.0;
        for (InvoiceItemDto item : data.getItems()) {
            subTotal += item.getTotal();

            // Cột 1
            addCellWithBorder(itemsTable, String.valueOf(count++), normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 2 (Tên SP + Biến thể)
            String productName = item.getProductName() + " (" + item.getColor() + "/" + item.getSize() + ")";
            addCellWithBorder(itemsTable, productName, normalFont, Rectangle.BOX, Element.ALIGN_LEFT, 0, 0);

            // Cột 3 (ĐVT) - Giả sử là "Sản phẩm" hoặc "Khóa"
            addCellWithBorder(itemsTable, "Sản phẩm", normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 4 (SL)
            addCellWithBorder(itemsTable, String.valueOf(item.getInventoryQuantity()), normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);

            // Cột 5 (Đơn giá)
            addCellWithBorder(itemsTable, String.format("%,.0f", item.getPrice()), normalFont, Rectangle.BOX, Element.ALIGN_RIGHT, 0, 0);

            // Cột 6 (Thành tiền)
            addCellWithBorder(itemsTable, String.format("%,.0f", item.getTotal()), normalFont, Rectangle.BOX, Element.ALIGN_RIGHT, 0, 0);
        }

        // --- Empty Rows (Giống mẫu) ---
        // Thêm các hàng trống để làm đầy trang (tùy chọn)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                addCellWithBorder(itemsTable, " ", normalFont, Rectangle.BOX, Element.ALIGN_CENTER, 0, 0);
            }
        }

        // --- Tổng cộng (Total Row) ---
        double finalTotal = subTotal - (data.getPromotion() != null ? data.getPromotion().getDiscountPercent() : 0.0) + data.getResponseDto().getShippingFee();

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
        PdfPCell discountCell = new PdfPCell(new Phrase("Giảm giá (Discount)", boldFont));
        discountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        discountCell.setColspan(5);
        discountCell.setBorder(Rectangle.BOX);
        discountCell.setPadding(7);
        itemsTable.addCell(discountCell);

        PdfPCell discountValueCell = new PdfPCell(new Phrase(data.getPromotion().getDiscountPercent().toString() + "% tổng tiền", normalFont));
        discountValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        discountValueCell.setBorder(Rectangle.BOX);
        discountValueCell.setPadding(7);
        itemsTable.addCell(discountValueCell);

        // Total price
        PdfPCell totalFinalLabelCell = new PdfPCell(new Phrase("Tổng cộng tiền cần thanh toán (Total payment):", boldFont));
        totalFinalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalFinalLabelCell.setColspan(5);
        totalFinalLabelCell.setBorder(Rectangle.BOX);
        totalFinalLabelCell.setPadding(7);
        itemsTable.addCell(totalFinalLabelCell);

        PdfPCell totalValueFinalLabelCell = new PdfPCell(new Phrase(String.format("%,.0f", finalTotal), normalFont));
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

        PdfPCell paymentMethodValueCell = new PdfPCell(new Phrase(data.getPayment().getType() == PaymentType.BANK_TRANSFER ? "Thanh toán bằng ngân hàng" : "Thanh toán khi nhân hàng", normalFont));
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
        String amountInWords = data.getResponseDto().getTotalAmount().toString();
        Paragraph words = new Paragraph("Số tiền viết bằng chữ (Amount in words): " + amountInWords, normalFont);
        leftTable.addCell(createCell(words, Rectangle.NO_BORDER, Element.ALIGN_LEFT));

        Paragraph buyerTitle = new Paragraph("Người mua hàng (Buyer)", boldFont);
        buyerTitle.setAlignment(Element.ALIGN_CENTER);
        buyerTitle.setSpacingBefore(15f);
        leftTable.addCell(createCell(buyerTitle, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Vùng chữ ký người mua (để trống)
        leftTable.addCell(createCell(new Phrase("\n\n\n", normalFont), Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        PdfPCell leftCell = createCell(leftTable, Rectangle.NO_BORDER);

        // --- Cột 2: Người bán và Chữ ký điện tử ---
        PdfPTable rightTable = new PdfPTable(1);
        rightTable.setWidthPercentage(100);

        // Người bán
        Paragraph sellerTitle = new Paragraph("Người bán hàng (Seller)", boldFont);
        sellerTitle.setAlignment(Element.ALIGN_CENTER);
        sellerTitle.setSpacingBefore(15f);
        sellerTitle.setSpacingAfter(15f);
        rightTable.addCell(createCell(sellerTitle, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Ký điện tử và ngày
        Paragraph signedNote = new Paragraph("Đã được ký điện tử bởi\n(Signed digitally by)", smallNormalFont);
        signedNote.setAlignment(Element.ALIGN_CENTER);
        rightTable.addCell(createCell(signedNote, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Tên công ty (Chữ ký)
        Paragraph companyName = new Paragraph("CÔNG TY TNHH NỘI THẬT HAUS", boldFont);
        companyName.setAlignment(Element.ALIGN_CENTER);
        rightTable.addCell(createCell(companyName, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        // Ngày ký
        LocalDate currentDate = LocalDate.now();
        Paragraph signDate = new Paragraph(String.format("Ngày: %d/%d/%d", currentDate.getDayOfMonth(), currentDate.getMonthValue(), currentDate.getYear()), normalFont);
        signDate.setAlignment(Element.ALIGN_CENTER);
        rightTable.addCell(createCell(signDate, Rectangle.NO_BORDER, Element.ALIGN_CENTER));

        PdfPCell rightCell = createCell(rightTable, Rectangle.NO_BORDER);

        footerTable.addCell(leftCell);
        footerTable.addCell(rightCell);

        // Ghi chú cuối cùng (Cần một bảng 1 cột)
        PdfPTable bottomNote = new PdfPTable(1);
        bottomNote.setWidthPercentage(100);
        Paragraph note = new Paragraph("(Cần kiểm tra đối chiếu khi giao, nhận hóa đơn)", smallNormalFont);
        note.setAlignment(Element.ALIGN_CENTER);
        PdfPCell cellBottom = createCell(note, Rectangle.NO_BORDER, Element.ALIGN_BOTTOM);
        cellBottom.setPaddingTop(30);
        bottomNote.addCell(cellBottom);

        // Cell chính
        PdfPCell mainCell = new PdfPCell();
        mainCell.addElement(footerTable);
        mainCell.addElement(new Paragraph("\n")); // Khoảng cách
        mainCell.addElement(bottomNote);
        mainCell.setBorder(Rectangle.TOP); // Chỉ có viền trên
        mainCell.setPaddingTop(5);
        mainCell.setPaddingRight(5);
        mainCell.setPaddingBottom(5);
        return mainCell;
    }

    // Hàm tiện ích để tạo nhanh một PdfPCell từ một Element (Paragraph hoặc Table)
    private PdfPCell createCell(Element element, int border, int colspan) throws DocumentException {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element);
        cell.setBorder(border);
        cell.setPadding(2);
        if (colspan > 0) {
            cell.setColspan(colspan);
        }
        return cell;
    }

    // Overload để chỉ truyền Element và Border
    private PdfPCell createCell(Element element, int border) throws DocumentException {
        return createCell(element, border, 0);
    }

    // Hàm tiện ích cho bảng chi tiết để kiểm soát border, rowspan/colspan
    private void addCellWithBorder(PdfPTable table, String text, Font font, int border, int alignment, int rowspan, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorder(border);
        cell.setPadding(5);
        if (rowspan > 0) {
            cell.setRowspan(rowspan);
        }
        if (colspan > 0) {
            cell.setColspan(colspan);
        }
        table.addCell(cell);
    }

    private void addImage(PdfPCell cell, Font font) {
        try {
            // Đường dẫn đến file ảnh trong thư mục resources
            String imagePath = "images/logo.png"; // hoặc "images/your_logo.jpg"

            // Sử dụng ClassLoader để đọc file ảnh từ classpath
            InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (is == null) {
                throw new IOException("Logo image not found: " + imagePath);
            }

            // Tạo đối tượng Image từ mảng byte
            byte[] imageData = is.readAllBytes();
            Image logo = Image.getInstance(imageData);
            is.close(); // Đóng InputStream

            // Tùy chỉnh kích thước ảnh để vừa với ô
            // Bạn có thể thiết lập kích thước cố định hoặc scale theo phần trăm
            float desiredWidth = 60f; // Ví dụ: chiều rộng mong muốn
            float scaleFactor = desiredWidth / logo.getWidth();
            logo.scalePercent(scaleFactor * 100); // Scale ảnh theo tỷ lệ

            // Hoặc scale theo một giá trị cố định (ví dụ 50% kích thước gốc)
            // logo.scalePercent(50);

            // Thêm ảnh vào cell
            cell.addElement(logo);

        } catch (IOException | BadElementException e) {
            // Xử lý lỗi nếu không tìm thấy ảnh hoặc ảnh không hợp lệ
            e.printStackTrace();
            cell.addElement(new Paragraph("LOGO ERROR", font)); // Hiển thị text lỗi thay thế
        }
    }

}
