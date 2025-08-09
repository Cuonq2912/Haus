package com.example.haus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "com.example.haus")
public class HausApplication {

    public static void main(String[] args) {
        SpringApplication.run(HausApplication.class, args);
    }

}
