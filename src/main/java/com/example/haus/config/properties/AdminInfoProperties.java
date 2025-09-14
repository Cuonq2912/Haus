package com.example.haus.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("admin")
@Getter
@Setter
public class AdminInfoProperties {
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
}
