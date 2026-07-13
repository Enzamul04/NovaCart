package com.ecommerce.service;

public interface EmailService {
    void sendEmail(String to, String subject, String message);
    void sendHtmlEmail(String to, String subject, String body);
    void sendOtpEmail(String to, String otp);
}
