package com.example.haus.config;

import com.example.haus.constant.RoleConstant;
import com.example.haus.security.CustomUserDetailsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("prod")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProdSecurityConfig {

    @Value("${security.public-endpoints}")
    String[] publicEndpoints;

    @Value("${security.user-endpoints}")
    String[] userEndpoints;

    @Value("${security.admin-endpoints}")
    String[] adminEndpoints;

    @Value("${security.swagger-endpoints}")
    String[] swaggerEndpoints;

    @Value("${cors.allowed-origins:http://localhost:5173}")
    String corsAllowedOrigins;

    final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicEndpoints).permitAll()
                        .requestMatchers(swaggerEndpoints).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/sub").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/promotion").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/category-id/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/search").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/product/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/promotion/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/filter/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment/momo/callback").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment/momo/ipn-handler").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/review/top").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/product/review")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/review/*")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/review/*")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/product/favorites")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product/favorites/**")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/favorites")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/favorites/check/**")
                        .hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/product/review/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/order").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/order/**").hasAnyAuthority(RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/order/**").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(userEndpoints).hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                        .requestMatchers(adminEndpoints).hasAnyAuthority(RoleConstant.ADMIN)
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        return httpSecurity.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        return new UserDetailsJwtConverter(customUserDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank()).collect(Collectors.toList());
        allowedOrigins.add("https://sandbox.vnpayment.vn");
        allowedOrigins.add("https://test-payment.momo.vn");

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
