package com.example.haus.service;

public interface EmailService {
    void sendRegistrationOtpByEmail(String to, String name, String otp);

    void sendForgotPasswordOtpByEmail(String to, String name, String otp);
}
