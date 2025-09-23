package com.example.haus.config;

import com.sendgrid.SendGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "APP-CONFIG")
public class AppConfig implements WebMvcConfigurer {

    private final MultipartJacksonHttpMessageConverter multipartJacksonHttpMessageConverter;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(multipartJacksonHttpMessageConverter);
    }

    @Bean
    public SendGrid sendGrid(@Value("${spring.sendGrid.apiKey}") String apiKey) {
        return new SendGrid(apiKey);
    }
}
