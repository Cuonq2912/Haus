package com.example.haus.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.haus.config.keycloak.KeycloakProperties;
import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.ErrorMessage;
import com.example.haus.constant.TokenType;
import com.example.haus.domain.entity.InvalidatedToken;
import com.example.haus.domain.entity.product.Cart;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.AuthMapper;
import com.example.haus.domain.dto.request.auth.*;
import com.example.haus.domain.dto.request.auth.otp.PendingRegistrationRequestDto;
import com.example.haus.domain.dto.request.auth.otp.PendingResetPasswordRequestDto;
import com.example.haus.domain.dto.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.dto.response.auth.LoginResponseDto;
import com.example.haus.domain.dto.response.auth.RefreshTokenResponseDto;
import com.example.haus.domain.dto.response.user.UserResponseDto;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.exception.KeycloakException;
import com.example.haus.exception.ResourceNotFoundException;
import com.example.haus.repository.CartRepository;
import com.example.haus.repository.InvalidatedTokenRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.security.CustomUserDetailsService;
import com.example.haus.service.AuthenticationService;
import com.example.haus.service.EmailService;
import com.example.haus.service.JwtService;
import com.example.haus.service.UserService;
import com.example.haus.util.OtpUtil;
import com.example.haus.util.keycloak.KeycloakUtil;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponse;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    KeycloakUtil keycloakUtil;

    KeycloakProperties keycloakProperties;

    JwtService jwtService;

    AuthMapper authMapper;

    EmailService emailService;

    InvalidatedTokenRepository invalidatedTokenRepository;

    UserRepository userRepository;

    CartRepository cartRepository;

    RestTemplate restTemplate;

    Map<String, PendingRegistrationRequestDto> pendingRegisterMap = new ConcurrentHashMap<>();

    Map<String, PendingResetPasswordRequestDto> pendingResetPasswordMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public LoginResponseDto authentication(LoginRequestDto request) {

        final String url = keycloakProperties.serverUrl() + "realms/" + keycloakProperties.realm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", keycloakProperties.clientId());
        params.add("client_secret", keycloakProperties.clientSecret());
        params.add("scope", "openid");
        params.add("username", request.getUsername());
        params.add("password", request.getPassword());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();

                String accessToken = (String) body.get("access_token");
                DecodedJWT decodedJWT = JWT.decode(accessToken);

                Map<String, Claim> claims = decodedJWT.getClaims();

                List<String> realmRoles = (List<String>) claims.get("realm_access")
                        .asMap().get("roles");

                return LoginResponseDto.builder()
                        .tokenType(CommonConstant.BEARER_TOKEN)
                        .userId(keycloakUtil.getUserId(request.getUsername()))
                        .role(realmRoles.toString().contains("ADMIN") ? "ADMIN" : "USER")
                        .accessToken(accessToken)
                        .refreshToken((String) body.get("refresh_token"))
                        .build();
            }
            else {
                log.error("Đăng nhập thất bại với username = {}", request.getUsername());
            }
        } catch (Exception ex) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_LOGIN_FAILED_IN_KEYCLOAK);
        }
        throw new InvalidDataException(ErrorMessage.Auth.ERR_USERNAME_PASSWORD_INCORRECT);
    }

    @Override
    public void logout(LogoutRequestDto request) {
        String jwtId = null;
        Date expirationTime = null;
        try {
            SignedJWT signedJwt = SignedJWT.parse(request.getToken());

            jwtId = signedJwt.getJWTClaimsSet().getJWTID();
            expirationTime = signedJwt.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(new InvalidatedToken(jwtId, expirationTime));

        } catch (ParseException ex) {
            log.error("Signed Jwt parsed fail, message = {}", ex.getMessage());
            throw new InvalidDataException(ErrorMessage.Auth.ERR_TOKEN_INVALIDATED);
        }
    }

    @Override
    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto request) {

        final String url = keycloakProperties.serverUrl() + "/realms/" + keycloakProperties.realm() + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("client_id", keycloakProperties.clientId());
        params.add("client_secret", keycloakProperties.clientSecret());
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", request.getRefreshToken());

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                String accessToken = (String) body.get("access_token");

                DecodedJWT decodedJWT = JWT.decode(accessToken);
                Map<String, Claim> claims = decodedJWT.getClaims();

                List<String> realmRoles = (List<String>) claims.get("realm_access")
                        .asMap().get("roles");

                return RefreshTokenResponseDto.builder()
                        .tokenType(CommonConstant.BEARER_TOKEN)
                        .accessToken(accessToken)
                        .refreshToken((String) body.get("refresh_token"))
                        .build();
            }
        } catch (Exception ex) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_LOGIN_FAILED_IN_KEYCLOAK);
        }
        throw new InvalidDataException(ErrorMessage.Auth.ERR_USERNAME_PASSWORD_INCORRECT);
    }

    @Override
    public void register(RegisterRequestDto request) {

        final String url = keycloakProperties.serverUrl() + "admin/realms/" + keycloakProperties.realm() + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, CommonConstant.BEARER_TOKEN + " " + keycloakUtil.getAdminToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.getUsername());
        user.put("enabled", true);
        user.put("email", request.getEmail());
        user.put("emailVerified", false);
        user.put("firstName", request.getFirstName());
        user.put("lastName", request.getLastName());
        user.put("credentials", List.of(Map.of(
                "type", "password",
                "value", request.getPassword(),
                "temporary", false
        )));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().isError()) {
                String errorBody = response.getBody();

                if (response.getStatusCode() == HttpStatus.CONFLICT && errorBody != null && !errorBody.isEmpty()) {
                    throw new KeycloakException(errorBody);
                }
                throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_CREATE_USER);
            }
        } catch (Exception ex) {
            throw new KeycloakException(ex.getMessage());
        }

        String otp = OtpUtil.generateOtp();

        PendingRegistrationRequestDto pending = new PendingRegistrationRequestDto();

        pending.setRequest(request);
        pending.setOtp(otp);
        pending.setExpireAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(5));

        pendingRegisterMap.put(request.getEmail(), pending);

        emailService.sendRegistrationOtpByEmail(request.getEmail(), request.getUsername(), otp);
    }

    @Override
    public UserResponseDto verifyOtpToRegister(VerifyOtpRequestDto request) {
        PendingRegistrationRequestDto pending = pendingRegisterMap.get(request.getEmail());

        if (pending == null){
            throw new InvalidDataException(ErrorMessage.Auth.ERR_PENDING_REGISTER_REQUEST_NULL);
        }

        if (pending.isExpired())
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_EXPIRED);

        if (!pending.getOtp().equals(request.getOtp()))
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_NOT_MATCH);

        if (!keycloakUtil.verifyEmail(keycloakUtil.getUserId(request.getEmail()), true)) {
            throw new KeycloakException(ErrorMessage.Auth.ERR_VERIFY_FAILED_IN_KEYCLOAK);
        }

        RegisterRequestDto req = pending.getRequest();

        User user = authMapper.registerRequestDtoToUser(req);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

        userRepository.save(user);
        cart = cartRepository.save(cart);

        pendingRegisterMap.remove(request.getEmail());

        return authMapper.userToUserResponseDto(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto request) {
        log.info(request.getEmail());

        if (!userRepository.existsUserByEmailAndIsDeletedFalse(request.getEmail()))
            throw new ResourceNotFoundException(ErrorMessage.User.ERR_EMAIL_NOT_EXISTED);

        String otp = OtpUtil.generateOtp();

        PendingResetPasswordRequestDto pending = new PendingResetPasswordRequestDto();

        pending.setRequest(request);
        pending.setOtp(otp);
        pending.setExpireAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(5));

        pendingResetPasswordMap.put(request.getEmail(), pending);

        emailService.sendForgotPasswordOtpByEmail(request.getEmail(), request.getEmail(), otp);
    }

    @Override
    public boolean verifyOtpToResetPassword(VerifyOtpRequestDto request) {
        PendingResetPasswordRequestDto pending = pendingResetPasswordMap.get(request.getEmail());

        if (pending == null)
            throw new InvalidDataException(ErrorMessage.Auth.ERR_PENDING_RESET_REQUEST_NULL);

        if (pending.isExpired())
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_EXPIRED);

        if (!pending.getOtp().equals(request.getOtp()))
            throw new InvalidDataException(ErrorMessage.Auth.ERR_OTP_NOT_MATCH);

        return pendingResetPasswordMap.containsKey(request.getEmail())
                && pendingResetPasswordMap.get(request.getEmail()).getOtp().equals(request.getOtp());
    }

    @Override
    public UserResponseDto resetPassword(ResetPasswordRequestDto request) {

        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new InvalidDataException(ErrorMessage.User.ERR_DUPLICATE_OLD_PASSWORD);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        pendingResetPasswordMap.remove(request.getEmail());

        return authMapper.userToUserResponseDto(user);
    }

}
