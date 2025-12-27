package com.example.haus.config;

import com.example.haus.interceptor.RateLimitInterceptor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebMvcConfig implements WebMvcConfigurer {

  RateLimitInterceptor rateLimitInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

      registry.addInterceptor(rateLimitInterceptor)
          .addPathPatterns("/api/**", "/auth/**")
          .excludePathPatterns(
              "/swagger-ui/**",
              "/swagger-ui.html",
              "/v3/api-docs/**",
              "/swagger-resources/**",
              "/webjars/**",
              "/actuator/**");
  }
}
