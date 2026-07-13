package com.ecommerce.controller;

import com.ecommerce.service.EmailService;
import com.ecommerce.util.OtpUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OtpController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/login-otp")
    public String otpPage() {
        return "verify_login_otp";
    }

    @PostMapping("/verify-login-otp")
    public String verifyOtp(@RequestParam String otp,
                            HttpSession session,
                            Model model) {
        String sessionOtp = (String) session.getAttribute("otp");
        String role = (String) session.getAttribute("role");
        Long otpTime = (Long) session.getAttribute("otpTime");

        if (otpTime == null ||
                (System.currentTimeMillis() - otpTime) > 300000) {
            session.removeAttribute("otp");
            session.removeAttribute("otpTime");
            model.addAttribute("errorMsg",
                    "OTP Expired. Please Resend OTP.");
            return "verify_otp";
        }

        if (sessionOtp != null && sessionOtp.equals(otp)) {
            session.removeAttribute("otp");
            session.removeAttribute("otpTime");
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/";
            }
            if ("ROLE_USER".equals(role)) {
                return "redirect:/user/";
            }
            return "redirect:/login";
        }
        model.addAttribute("errorMsg",
                "Invalid OTP");
        return "verify_otp";
    }

    @GetMapping("/resend-login-otp")
    public String resendOtp(HttpSession session) {
        String email = (String) session.getAttribute("loginEmail");
        if (email == null) {
            return "redirect:/login";
        }
        String otp = OtpUtil.generateOtp();
        session.setAttribute("otp", otp);
        session.setAttribute("otpTime", System.currentTimeMillis());
        emailService.sendOtpEmail(email, otp);
        return "redirect:/login-otp";
    }
}