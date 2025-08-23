package com.example.haus.domain.response.user;

import com.example.haus.domain.entity.user.Gender;
import com.example.haus.domain.entity.user.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

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

    String email;

    Role role;

    Gender gender;

}
