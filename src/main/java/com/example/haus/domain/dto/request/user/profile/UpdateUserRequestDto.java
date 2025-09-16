package com.example.haus.domain.dto.request.user.profile;

import com.example.haus.domain.entity.user.Gender;
import com.example.haus.domain.validator.GenderSubset;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequestDto {

    @Schema(description = "Tên đăng nhập", example = "username")
    String username;

    @Schema(description = "Tên", example = "Quân")
    String firstName;

    @Schema(description = "Họ", example = "Bùi")
    String lastName;

    @Schema(description = "Ngày sinh", example = "19/05/2005")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;

    @Schema(description = "Email", example = "example@gmail.com")
    String email;

    @Schema(description = "Điện thoại", example = "0123456789")
    String phone;

    @Schema(description = "Giới tính", example = "MALE / FEMALE / OTHER")
    @GenderSubset(anyOf = {Gender.MALE, Gender.FEMALE, Gender.OTHER})
    Gender gender;

    @Schema(description = "Quốc tịch", example = "Việt Nam")
    String nationality;

}
