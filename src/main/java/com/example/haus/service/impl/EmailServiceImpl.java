package com.example.haus.service.impl;

import com.example.haus.service.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailServiceImpl implements EmailService {
    private final SendGrid sendGrid;

    @Value("${spring.sendGrid.apiKey}")
    private String apiKey;

    @Value("${spring.sendGrid.fromEmail}")
    private String from;

    @Value("${spring.sendGrid.templateRegistrationId}")
    private String templateRegistrationId;

    @Value("${spring.sendGrid.templateForgotId}")
    private String templateForgotId;

    @Value("${spring.sendGrid.imagePrev}")
    private String imagePrev;

    @Value("${spring.sendGrid.imageNext}")
    private String imageNext;

    @Value("${spring.sendGrid.logo}")
    private String logo;

    @Override
    public void sendRegistrationOtpByEmail(String to, String name, String otp) {
        log.info("Send email verification for username = {}", name);

        Email fromEmail = new Email(from, "HAUS");
        Email toEmail = new Email(to);
        String subject = "Xác thực tài khoản";


        //Tạo Dynamic Template data
        Map<String, String> dynamicTemplateData = new HashMap<>();
        dynamicTemplateData.put("NAME", name);
        dynamicTemplateData.put("OTP_CODE", otp);
        dynamicTemplateData.put("IMAGE_PREV", imagePrev);
        dynamicTemplateData.put("IMAGE_NEXT", imageNext);
        dynamicTemplateData.put("LOGO_LINK", logo);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add dynamic Template data;
        dynamicTemplateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateRegistrationId);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            request.setEndpoint("mail/send");
            Response response = sendGrid.api(request);

            if(response.getStatusCode() == 202) {
                log.info("Sending email verification successfully");
            } else {
                log.info("Sending email failed");
            }
        } catch (Exception ex) {
            log.info("Sending email verification failed, message = {}", ex);
        }
    }

    @Override
    public void sendForgotPasswordOtpByEmail(String to, String name, String otp) {
        log.info("Sending email forgot password by email = {}", to);

        Email fromEmail = new Email(from, "HAUS");
        Email toEmail = new Email(to);
        String subject = "Quên mật khẩu";


        //Tạo Dynamic Template data
        Map<String, String> dynamicTemplateData = new HashMap<>();
        dynamicTemplateData.put("NAME", name);
        dynamicTemplateData.put("OTP_CODE", otp);
        dynamicTemplateData.put("IMAGE_PREV", imagePrev);
        dynamicTemplateData.put("IMAGE_NEXT", imageNext);
        dynamicTemplateData.put("LOGO_LINK", logo);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        //Add dynamic Template data;
        dynamicTemplateData.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateForgotId);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setBody(mail.build());
            request.setEndpoint("mail/send");
            Response response = sendGrid.api(request);

            if(response.getStatusCode() == 202) {
                log.info("Sending email forgot password successfully");
            } else {
                log.info("Sending email forgot password failed");
            }
        } catch (Exception ex) {
            log.info("Sending email forgot password failed, message = {}", ex);
        }
    }
}
