package com.example.haus.service.impl;

import com.example.haus.constant.ErrorMessage;
import com.example.haus.exception.KeycloakException;
import com.example.haus.service.EmailService;
import com.example.haus.util.LogSanitizerUtil;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailServiceImpl implements EmailService {
    private final SendGrid sendGrid;

    @Value("${spring.sendGrid.fromEmail}")
    private String from;

    @Value("${spring.sendGrid.imagePrev}")
    private String imagePrev;

    @Value("${spring.sendGrid.imageNext}")
    private String imageNext;

    @Value("${spring.sendGrid.logo}")
    private String logo;

    private String registrationTemplate;
    private String forgotPasswordTemplate;

    @PostConstruct
    public void init() {
        try {
            registrationTemplate = loadTemplate("templates/email/registration-otp.html");
            forgotPasswordTemplate = loadTemplate("templates/email/forgot-password-otp.html");
            log.info("Email templates loaded successfully");
        } catch (IOException e) {
            log.error("Failed to load email templates: {}", e.getMessage(), e);
        }
    }

    private String loadTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    private String processTemplate(String template, String name, String otp) {
        return template.replace("{{NAME}}", name).replace("{{OTP_CODE}}", otp).replace("{{IMAGE_PREV}}", imagePrev)
                .replace("{{IMAGE_NEXT}}", imageNext).replace("{{LOGO_LINK}}", logo);
    }

    @Override
    public void sendRegistrationOtpByEmail(String to, String name, String otp) {
        log.info("Send registration OTP email to identifier={}", LogSanitizerUtil.maskIdentifier(to));

        Email fromEmail = new Email(from, "HAUS");
        Email toEmail = new Email(to);
        String subject = "Xác thực tài khoản - HAUS";

        String htmlContent = processTemplate(registrationTemplate, name, otp);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            request.setEndpoint("mail/send");
            Response response = sendGrid.api(request);
            log.info("SendGrid Response Code: {}", response.getStatusCode());
            log.info("SendGrid Response Body: {}", LogSanitizerUtil.sanitizeSensitiveText(response.getBody()));
            log.info("SendGrid Response Headers: {}", response.getHeaders());
            if (response.getStatusCode() == 202) {
                log.info("Sending email verification successfully");
            } else {
                log.error("Sending email failed with status: {}", response.getStatusCode());
                throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_OTP_EMAIL);
            }
        } catch (Exception ex) {
            log.error("Sending email verification failed, message = {}\n", ex.getMessage(), ex);
            if (ex instanceof KeycloakException keycloakException) {
                throw keycloakException;
            }
            throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_OTP_EMAIL);
        }
    }

    @Override
    public void sendForgotPasswordOtpByEmail(String to, String name, String otp) {
        log.info("Send forgot password OTP email to identifier={}", LogSanitizerUtil.maskIdentifier(to));

        Email fromEmail = new Email(from, "HAUS");
        Email toEmail = new Email(to);
        String subject = "Đặt lại mật khẩu - HAUS";

        String htmlContent = processTemplate(forgotPasswordTemplate, name, otp);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            request.setEndpoint("mail/send");
            Response response = sendGrid.api(request);
            log.info("SendGrid Response Code: {}", response.getStatusCode());
            log.info("SendGrid Response Body: {}", LogSanitizerUtil.sanitizeSensitiveText(response.getBody()));
            if (response.getStatusCode() == 202) {
                log.info("Sending email forgot password successfully");
            } else {
                log.error("Sending email forgot password failed with status: {}", response.getStatusCode());
                throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_OTP_EMAIL);
            }
        } catch (Exception ex) {
            log.error("Sending email forgot password failed, message = {}", ex.getMessage(), ex);
            if (ex instanceof KeycloakException keycloakException) {
                throw keycloakException;
            }
            throw new KeycloakException(ErrorMessage.Auth.ERR_CAN_NOT_SEND_OTP_EMAIL);
        }
    }
}
