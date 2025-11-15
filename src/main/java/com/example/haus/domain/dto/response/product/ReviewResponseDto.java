package com.example.haus.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponseDto {

    Long id;
    Integer rating;
    String content;
    
    Long productId;
    String productName;
    
    Long orderItemId;
    
    UserInfo user;
    
    Date createdAt;
    Date updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UserInfo {
        String userId;
        String username;
        String firstName;
        String lastName;
    }
}
