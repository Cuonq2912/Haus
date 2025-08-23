package com.example.haus.config;

import com.example.haus.constant.RoleConstant;
import com.example.haus.security.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {

    @Value("${security.public-endpoints}")
    String[] PUBLIC_END_POINT;

    @Value("${security.user-endpoints}")
    String[] ADMIN_END_POINT;

    @Value("${security.admin-endpoints}")
    String[] USER_END_POINT;

    @Value("${security.swagger-endpoints}")
    String[] OPEN_API;

    final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_END_POINT).permitAll()
                .requestMatchers(ADMIN_END_POINT).hasAuthority(RoleConstant.ADMIN)
                .requestMatchers(USER_END_POINT).hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                .requestMatchers(OPEN_API).permitAll()
                .anyRequest().authenticated());

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return httpSecurity.build();
    }



}
