package com.ecommerce.controller;

import com.ecommerce.dto.CouponDto;
import com.ecommerce.model.Coupon;
import com.ecommerce.service.CouponService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping("/apply")
    public Map<String, Object> applyCoupon(
            @RequestParam String code,
            @RequestParam Double total,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Coupon coupon = couponService.getCouponByCode(code);
        if (coupon == null) {
            response.put("status", "failed");
            response.put("message", "Invalid Coupon");
            return response;
        }

        if (!coupon.getActive()) {
            response.put("status", "failed");
            response.put("message", "Coupon Inactive");
            return response;
        }

        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            response.put("status", "failed");
            response.put("message", "Coupon Expired");
            return response;
        }

        if (total < coupon.getMinimumAmount()) {
            response.put("status", "failed");
            response.put("message",
                    "Minimum order ₹" + coupon.getMinimumAmount());
            return response;
        }
        Double discount = total * coupon.getDiscount() / 100;
        Double finalAmount = total - discount;
        CouponDto dto = new CouponDto();
        dto.setCouponCode(coupon.getCouponCode());
        dto.setDiscountAmount(discount);
        dto.setFinalAmount(finalAmount);
        session.setAttribute("coupon", dto);
        response.put("status", "success");
        response.put("total", finalAmount);
        response.put("discount", discount);
        return response;
    }
}