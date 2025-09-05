package com.example.haus.domain.request.admin;

import com.example.haus.domain.entity.user.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class UpdateUserRequestDto {
    @Schema(description = "Tên đăng nhập", example = "username")
    String username;

    @Schema(description = "Mật khẩu", example = "password")
    String password;

    @Schema(description = "Tên", example = "Quân")
    String firstName;

    @Schema(description = "Họ", example = "Bùi")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    String lastName;

    @Schema(description = "Ngày sinh", example = "19/05/2005")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd/MM/yyyy")
    Date dateOfBirth;

    @Schema(description = "Email", example = "example@gmail.com")
    String email;


    @Schema(description = "Quốc tịch", example = "Việt nam")
    String nationality;

    @Schema(description = "Vai trò", example = "USER")
    Role role;

    Boolean isActive;
}
