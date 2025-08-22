package com.example.haus.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "APP-CONFIG")
public class AppConfig {
    @Bean
    public SendGrid sendGrid(@Value("${spring.sendGrid.apiKey}") String apiKey) {
        return new SendGrid(apiKey);
    }
}
