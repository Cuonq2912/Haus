package com.example.haus.util;

import com.example.haus.domain.dto.response.invoice.InvoiceResponseDto;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.qrcode.WriterException;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j(topic = "PDF-UTIL")
public class PdfUtil {

    private PdfUtil () {}

    //1. Mảng đơn vị
    private static final String[] units = {
            "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };

    // 2. Mảng start with 1
    private static final String[] teens = {
            "mười", "mười một", "mười hai", "mười ba", "mười bốn", "mười lăm", "mười sáu", "mười bảy", "mười tám", "mười chín"
    };

    // 3. Mảng chứa các từ hàng lớn (nghìn, triệu, tỷ)
    private static final String[] scales = {
            "", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"
    };

    public static String convert(long number) {
        if (number == 0) {
            return "Không đồng";
        }

        // Đảm bảo số là số dương
        if (number < 0) {
            return "Âm " + convert(-number);
        }

        String words = "";
        int scaleIndex = 0; // Hàng (nghìn, triệu, tỷ)

        while (number > 0) {
            // Lấy nhóm 3 chữ số cuối cùng
            int chunk = (int) (number % 1000);

            // Nếu nhóm 3 chữ số này khác 0, tiến hành đọc
            if (chunk != 0) {
                String chunkWords = convertChunk(chunk);

                // Thêm từ chỉ hàng (nghìn, triệu, tỷ, ...)
                if (scaleIndex > 0) {
                    chunkWords += " " + scales[scaleIndex];
                }

                // Nối vào chuỗi kết quả (nhóm lớn hơn đứng trước)
                words = new StringBuilder().append(chunkWords).append(" ").append(words).toString();
            }

            number /= 1000;
            scaleIndex++;
        }

        // Loại bỏ khoảng trắng thừa và viết hoa chữ cái đầu tiên
        words = words.trim();
        if (!words.isEmpty()) {
            words = words.substring(0, 1).toUpperCase() + words.substring(1).toLowerCase();
        }

        // Thêm "đồng" vào cuối nếu cần
        return words + " đồng";
    }

    /**
     * Hàm chuyển đổi một nhóm 3 chữ số (0-999) thành chữ
     * @param number Nhóm 3 chữ số
     * @return Chuỗi chữ Tiếng Việt cho nhóm đó
     */
    private static String convertChunk(int number) {
        String current = "";

        // Xử lý hàng trăm
        if (number > 99) {
            current += units[number / 100] + " trăm";
            number %= 100;
            if (number > 0) {
                current += " "; // Thêm khoảng trắng nếu vẫn còn hàng chục/đơn vị
            }
        }

        // Xử lý hàng chục và hàng đơn vị
        if (number > 0) {
            if (number < 10) {
                // Đơn vị
                if (current.endsWith("trăm ")) { // Nếu là '...trăm lẻ X'
                    current += "lẻ " + units[number];
                } else {
                    current += units[number];
                }
            } else if (number < 20) {
                // Hàng chục đặc biệt (10-19)
                current += teens[number - 10];
            } else {
                // Hàng chục và đơn vị còn lại (20-99)
                int tens = number / 10;
                int unit = number % 10;

                current += units[tens] + " mươi";

                if (unit > 0) {
                    if (unit == 1) {
                        current += " mốt"; // Ví dụ: hai mươi mốt
                    } else if (unit == 5) {
                        current += " lăm"; // Ví dụ: hai mươi lăm
                    } else {
                        current += " " + units[unit];
                    }
                }
            }
        }
        return current;
    }

    // Hàm tiện ích để tạo nhanh một PdfPCell từ một Element (Paragraph hoặc Table)
    public static PdfPCell createCell(Element element, int border, int colspan) {
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
    public static PdfPCell createCell(Element element, int border) {
        return createCell(element, border, 0);
    }

    // Hàm tiện ích cho bảng chi tiết để kiểm soát border, rowspan/colspan
    public static void addCellWithBorder(PdfPTable table, String text, Font font, int border, int alignment, int rowspan, int colspan) {
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

    public static void addImage(PdfPCell cell, Font font) {
        try {
            // Đường dẫn đến file ảnh trong thư mục resources
            String imagePath = "images/logo.png"; // hoặc "images/your_logo.jpg"

            // Sử dụng ClassLoader để đọc file ảnh từ classpath
            InputStream is = PdfUtil.class.getClassLoader().getResourceAsStream(imagePath);
            if (is == null) {
                throw new IOException("Logo image not found: " + imagePath);
            }

            // Tạo đối tượng Image từ mảng byte
            byte[] imageData = is.readAllBytes();
            Image logo = Image.getInstance(imageData);
            is.close(); // Đóng InputStream

            float desiredWidth = 60f; // Ví dụ: chiều rộng mong muốn
            float scaleFactor = desiredWidth / logo.getWidth();
            logo.scalePercent(scaleFactor * 100); // Scale ảnh theo tỷ lệ

            // Thêm ảnh vào cell
            cell.addElement(logo);

        } catch (IOException | BadElementException e) {
            // Xử lý lỗi nếu không tìm thấy ảnh hoặc ảnh không hợp lệ
            e.printStackTrace();
            cell.addElement(new Paragraph("LOGO ERROR", font)); // Hiển thị text lỗi thay thế
        }
    }

    public static Image generateQrCodeImage(String content, int width, int height) throws IOException, BadElementException, com.google.zxing.WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        // Cố gắng sử dụng nội dung (content) để tạo BitMatrix
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

        // Chuyển BitMatrix sang BufferedImage (Java AWT)
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Chuyển BufferedImage sang iText Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return Image.getInstance(baos.toByteArray());
    }

    //Tạo mã qr
    public static void generateQrCode(PdfPTable mainTable, InvoiceResponseDto data, Font font) throws DocumentException {
        // 2. TẠO VÀ CHÈN MÃ QR

        //Endpoint redirect
        String baseUrl = "https://haus.com.vn/invoice/view?id=";
        String invoiceId = String.valueOf(data.getResponseDto().getId());
        String qrContent = baseUrl + invoiceId;

        try {
            // Tạo hình ảnh QR Code
            Image qrCodeImage = PdfUtil.generateQrCodeImage(qrContent, 50, 50);

            PdfPCell qrCell = new PdfPCell(qrCodeImage);
            qrCell.setBorder(Rectangle.NO_BORDER);
            qrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            qrCell.setPaddingTop(5);

            // Thêm chú thích cho QR
            Paragraph qrNote = new Paragraph("Quét mã để xem hóa đơn điện tử\n(Scan to view E-Invoice)", font);
            qrNote.setAlignment(Element.ALIGN_CENTER);

            // Đặt QR và chú thích vào một bảng con 1 hàng 2 cột
            PdfPTable qrSubTable = new PdfPTable(2);
            qrSubTable.setWidths(new float[]{1.2f, 2.3f});

            // Cột 1: QR Code
            qrCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            qrSubTable.addCell(qrCell);
            qrSubTable.setWidthPercentage(100);

            // Cột 2: Chú thích (cần căn giữa theo chiều dọc)
            PdfPCell noteCell = PdfUtil.createCell(qrNote, Rectangle.NO_BORDER);
            noteCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            noteCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qrSubTable.addCell(noteCell);

            // Thêm bảng QR vào leftTable
            mainTable.addCell(PdfUtil.createCell(qrSubTable, Rectangle.NO_BORDER));

        } catch (com.google.zxing.WriterException | IOException | BadElementException e) {
            // Xử lý nếu việc tạo QR code thất bại
            log.error("Failed to generate QR Code for invoice {}: {}", invoiceId, e.getMessage());
            // Thêm dòng báo lỗi hoặc bỏ qua
            Paragraph errorNote = new Paragraph("Lỗi tạo mã QR (QR Code generation failed)", font);
            mainTable.addCell(PdfUtil.createCell(errorNote, Rectangle.NO_BORDER, Element.ALIGN_LEFT));
        } catch (Exception ex) {
            throw new DocumentException("Failed to generate QR Code for invoice " + invoiceId, ex);
        }
    }
}
