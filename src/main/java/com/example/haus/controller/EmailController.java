package com.example.haus.controller;

import com.example.haus.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL-CONTROLLER")
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;


    @PostMapping("/send-verification-email")
    public void sendVerificationEmail(@RequestParam String to, @RequestParam String name, @RequestParam String otp) {
        try {
            emailService.sendRegistrationOtpByEmail(to, name, otp);
            log.info("Verification email sent successfully!");
        } catch (Exception e) {
            log.info("Failed to send verification email.");
        }
    }

    @PostMapping("/verification-account")
    public void sendVerificationAccount(@RequestParam String to, @RequestParam String name, @RequestParam String otp) {
        try {
            emailService.sendRegistrationOtpByEmail(to, name, otp);
            log.info("Verification email sent successfully!");
        } catch (Exception e) {
            log.info("Failed to send verification email.");
        }
    }
}
