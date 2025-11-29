package com.example.haus.config;

import com.example.haus.constant.RoleConstant;
import com.example.haus.security.CustomUserDetailsService;
import com.example.haus.security.CustomizePreFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!prod")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${security.public-endpoints}")
    String[] publicEndpoints;

    @Value("${security.user-endpoints}")
    String[] userEndpoints;

    @Value("${security.admin-endpoints}")
    String[] adminEndpoints;

    @Value("${security.swagger-endpoints}")
    String[] swaggerEndpoints;

    final CustomUserDetailsService customUserDetailsService;

    final CustomizePreFilter customizePreFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
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
                                .requestMatchers(HttpMethod.GET, "/api/v1/order").hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers(HttpMethod.PATCH, "/api/v1/order/**").hasAnyAuthority(RoleConstant.ADMIN)
                                .requestMatchers(HttpMethod.POST, "/api/v1/orders").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                                .requestMatchers(HttpMethod.GET, "/api/v1/order/**").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                                .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                                .requestMatchers(userEndpoints).hasAnyAuthority(RoleConstant.USER, RoleConstant.ADMIN)
                                .requestMatchers(adminEndpoints).hasAnyAuthority(RoleConstant.ADMIN)
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(customizePreFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://ahistorical-undelusory-soren.ngrok-free.dev",
                "https://nonrestrained-karima-unexclaiming.ngrok-free.dev/",
                "https://83fj4w28-5173.asse.devtunnels.ms/",
                "https://sandbox.vnpayment.vn",
                "https://test-payment.momo.vn")); // domain FE + ngrok + VNPay + MoMo
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
}
