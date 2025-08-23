package com.example.haus.service.impl;

import com.example.haus.constant.CommonConstant;
import com.example.haus.constant.RoleConstant;
import com.example.haus.constant.TokenType;
import com.example.haus.domain.entity.InvalidatedToken;
import com.example.haus.domain.entity.user.Role;
import com.example.haus.domain.entity.user.User;
import com.example.haus.domain.mapper.AuthMapper;
import com.example.haus.domain.request.auth.*;
import com.example.haus.domain.request.auth.otp.PendingRegistrationRequestDto;
import com.example.haus.domain.request.auth.otp.PendingResetPasswordRequestDto;
import com.example.haus.domain.request.auth.otp.VerifyOtpRequestDto;
import com.example.haus.domain.response.auth.LoginResponseDto;
import com.example.haus.domain.response.auth.RefreshTokenResponseDto;
import com.example.haus.domain.response.user.UserResponseDto;
import com.example.haus.exception.InternalServerException;
import com.example.haus.exception.InvalidDataException;
import com.example.haus.repository.InvalidatedTokenRepository;
import com.example.haus.repository.UserRepository;
import com.example.haus.security.CustomUserDetailsService;
import com.example.haus.service.AuthenticationService;
import com.example.haus.service.EmailService;
import com.example.haus.service.JwtService;
import com.example.haus.service.UserService;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {

    JwtService jwtService;

    AuthMapper authMapper;

    CustomUserDetailsService userDetailsService;

    EmailService emailService;

    AuthenticationManager authenticationManager;

    UserService userService;

    InvalidatedTokenRepository invalidatedTokenRepository;

    UserRepository userRepository;

    Map<String, PendingRegistrationRequestDto> pendingRegisterMap = new ConcurrentHashMap<>();

    Map<String, PendingResetPasswordRequestDto> pendingResetPasswordMap = new ConcurrentHashMap<>();

    @Override
    public LoginResponseDto authentication(LoginRequestDto request) {
        log.info("-----------GetAccessToken-------------");
        try {
            //Verify username password with authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword()));

            log.info("isAuthenticated = {}", authentication.isAuthenticated());
            log.info("Authorities = {}", authentication.getAuthorities());

            // If verify success, save in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (DisabledException e) {
            log.error("Tài khoản đã bị vô hiệu hóa");
            throw new BadCredentialsException("Tài khoản đã bị khóa"); // hoặc custom exception của bạn
        } catch (AuthenticationException e) {
            log.error("Xác thực thất bại: {}", e.getMessage());
            throw new InternalAuthenticationServiceException("Sai tài khoản hoặc mật khẩu");
        }
        var user = userService.findByUsername(request.getUsername());

        //Create accessToken and refreshToken
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString())));

        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString())));

        //save to redis if use (Best practise) -> Although you can use both redis and db

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void logout(LogoutRequestDto request) {
        String JwtId = null;
        Date expirationTime = null;
        try {
            SignedJWT signedJwt = SignedJWT.parse(request.getToken());

            JwtId = signedJwt.getJWTClaimsSet().getJWTID();
            expirationTime = signedJwt.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(new InvalidatedToken(JwtId, expirationTime));

        } catch (ParseException ex) {
            log.error("Signed Jwt parsed fail, message = {}", ex.getMessage());
        }
    }

    @Override
    public RefreshTokenResponseDto refresh(RefreshTokenResponseDto request) {
        String refreshToken = request.getRefreshToken();

        String username = jwtService.extractUserName(refreshToken, TokenType.REFRESH_TOKEN);

        if (jwtService.isExpired(refreshToken, TokenType.REFRESH_TOKEN)) {
            throw new InvalidDataException("Refresh Token đã hết hạn");
        }

        if (!jwtService.isValid(refreshToken, TokenType.ACCESS_TOKEN, username)) {
            throw new InvalidDataException("Refresh Token không hợp lệ");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString())));

        return RefreshTokenResponseDto.builder()
                .tokenType(CommonConstant.BEARER_TOKEN)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void register(RegisterRequestDto request) {
        if (userRepository.existsUserByUsername(request.getUsername()))
            throw new InvalidDataException("Username đã tồn tại, không thể đăng ký với tài khoản này");

//        if (userRepository.existsUserByEmail(request.getEmail()))
//            throw new VsException(HttpStatus.CONFLICT, ErrorMessage.User.ERR_EMAIL_EXISTED);

        String otp = generateOtp();

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

        if (pending == null)
            throw new InvalidDataException("Registration storage pending request không được null");

        if (pending.isExpired())
            throw new InternalServerException("Mã otp đã quá hạn");
        if (!pending.getOtp().equals(request.getOtp()))
            throw new InvalidDataException("Mã otp không khớp");

        RegisterRequestDto req = pending.getRequest();

        User user = authMapper.registerRequestDtoToUser(req);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        pendingRegisterMap.remove(request.getEmail());

        return authMapper.userToUserResponseDto(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDto request) {
        if (!userRepository.existsUserByUsername(request.getEmail()))
            throw new UsernameNotFoundException("Username not found");

        String otp = generateOtp();

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
            throw new InvalidDataException("Pending Reset Password Request không được null");

        if (pending.isExpired())
            throw new InternalServerException("Mã otp đã hết hạn");

        if (!pending.getOtp().equals(request.getOtp()))
            throw new InvalidDataException("Mã otp nhập không trùng khớp");

        return pendingResetPasswordMap.containsKey(request.getEmail())
                && pendingResetPasswordMap.get(request.getEmail()).getOtp().equals(request.getOtp());
    }

    @Override
    public UserResponseDto resetPassword(ResetPasswordRequestDto request) {

        if (!request.getNewPassword().equals(request.getReEnterPassword()))
            throw new InvalidDataException("Mật khẩu nhập lại không khớp");

        User user = userRepository.findByUsername(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new InvalidDataException("Không được nhập trùng với mật khẩu cũ");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        pendingResetPasswordMap.remove(request.getEmail());

        return authMapper.userToUserResponseDto(user);
    }

    @Override
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
