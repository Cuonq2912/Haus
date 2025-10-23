package com.example.haus.domain.dto.request.product;

import com.example.haus.constant.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductRequestDto {

    @Schema(description = "Tên sản phẩm", example = "Ghế sofa phòng khách")
    String productName;

    @Schema(description = "Giá", example = "199.99")
    @Positive(message = ErrorMessage.MUST_BE_POSITIVE)
    Double price;

    @Schema(description = "Mô tả sản phẩm", example = "Ghế sofa cao cấp, chất liệu da thật, thiết kế hiện đại")
    String description;

    @Schema(description = "Mô tả chi tiết sản phẩm",
            example = """
                    Ghế có tay vịn BONDHOLMEN từ IKEA mang đến sự kết hợp hoàn hảo giữa phong cách hiện đại và sự thoải mái tối ưu cho không gian ngoài trời của bạn.               
                    Với thiết kế độc đáo, khung nhôm chắc chắn được sơn tĩnh điện màu be trang nhã và mặt ngồi, lưng tựa được đan bằng các sợi polyester bền bỉ.                   
                    Chiều rộng: 61 cm                   
                    Chiều sâu: 69 cm                    
                    Chiều cao: 81 cm
                    Chiều rộng mặt ngồi: 48 cm
                    Chiều sâu mặt ngồi: 50 cm
                    Chiều cao mặt ngồi: 42 cm
                    Chiều cao tay vịn: 63 cm
                    """
    )
    String detailDescription;

    @Schema(description = "Tên các thể loại", example = "[\"Phòng ngủ\", \"Phòng khách\"]")
    List<String> categories;

    @Schema(description = "Chất liệu sản phẩm", example = "metal")
    String material;

    @Schema(description = "Danh sách ID của các hình ảnh cần xóa", example = "[1, 2, 3]")
    List<Long> imageIdsToDelete;
}
