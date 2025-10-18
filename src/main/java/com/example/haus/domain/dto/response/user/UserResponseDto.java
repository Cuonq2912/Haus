package com.example.haus.domain.dto.response.user;

import com.example.haus.domain.entity.user.Gender;
import com.example.haus.domain.entity.user.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {

    String id;

    String username;

    String firstName;

    String lastName;

    String dateOfBirth;

    String avatarLink;

    String phone;

    String email;

    Role role;

    Gender gender;

    String nationality;

    Long cartId;
}
