package com.ecommerce.config;

import com.ecommerce.service.EmailService;
import com.ecommerce.util.OtpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Autowired
    private EmailService emailService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        String otp = OtpUtil.generateOtp();
        HttpSession session = request.getSession();
        session.setAttribute("otp", otp);
        session.setAttribute("otpTime", System.currentTimeMillis());
        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        session.setAttribute("role", role);
        String email = authentication.getName();
        session.setAttribute("loginEmail", email);
        emailService.sendOtpEmail(email, otp);
        response.sendRedirect("/login-otp");
    }
}