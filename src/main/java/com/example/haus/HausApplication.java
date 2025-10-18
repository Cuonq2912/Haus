package com.example.haus;

import com.example.haus.config.properties.AdminInfoProperties;
import com.example.haus.domain.entity.product.Cart;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.repository.CartRepository;
import com.example.haus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "com.example.haus")
@EnableConfigurationProperties({AdminInfoProperties.class})
public class HausApplication {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CartRepository cartRepository;

    public static void main(String[] args) {
        Environment env = SpringApplication.run(HausApplication.class, args).getEnvironment();
        String appName = env.getProperty("spring.application.name");
        if (appName != null) {
            appName = appName.toUpperCase();
        }
        String port = env.getProperty("server.port");
        log.info("-------------------------START {} Application------------------------------", appName);
        log.info("   Application         : {}", appName);
        log.info("   Url swagger-ui      : http://localhost:{}/swagger-ui.html", port);
        log.info("-------------------------START SUCCESS {} Application------------------------------", appName);
    }

    @Bean
    CommandLineRunner init(AdminInfoProperties adminInfo) {
        return args -> {
            if(userRepository.count() == 0) {
                User admin = User.builder()
                        .username(adminInfo.getUsername())
                        .password(passwordEncoder.encode(adminInfo.getPassword()))
                        .firstName(adminInfo.getFirstName())
                        .lastName(adminInfo.getLastName())
                        .email(adminInfo.getEmail())
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);

                Cart cart = new Cart();
                cart.setUser(admin);
                admin.setCart(cart);

                cartRepository.save(cart);

                log.info("admin created successful with name: {} and password = {}", admin.getUsername(), admin.getPassword());
            }
        };
    }

}
