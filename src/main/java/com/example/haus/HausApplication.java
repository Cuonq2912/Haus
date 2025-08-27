package com.example.haus;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.core.env.Environment;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = "com.example.haus")
public class HausApplication {

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

}
