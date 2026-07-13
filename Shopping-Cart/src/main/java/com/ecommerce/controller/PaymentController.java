package com.ecommerce.controller;

import com.ecommerce.config.CustomUser;
import com.ecommerce.dto.CouponDto;
import com.ecommerce.model.UserDtls;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.PaymentService;
import com.ecommerce.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping("/create-order")
    public String createOrder(@RequestParam Double amount) throws Exception {
        JSONObject order = paymentService.createOrder(amount);
        return order.toString();
    }
    @PostMapping("/verify")
    public String verifyPayment(Authentication authentication,
                                @RequestBody String payload,
                                HttpSession session) throws Exception {
        String result = paymentService.verifyPayment(payload);
        if (!"success".equals(result)) {
            return "failed";
        }
        JSONObject json = new JSONObject(payload);
        String paymentId = json.getString("razorpay_payment_id");
        String razorpayOrderId = json.getString("razorpay_order_id");
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        CouponDto coupon = (CouponDto) session.getAttribute("coupon");
        if (coupon == null) {
            orderService.saveOrder(
                    user.getId(),
                    "ONLINE",
                    paymentId,
                    razorpayOrderId,
                    null,
                    0.0,
                    null);
        } else {
            orderService.saveOrder(
                    user.getId(),
                    "ONLINE",
                    paymentId,
                    razorpayOrderId,
                    coupon.getCouponCode(),
                    coupon.getDiscountAmount(),
                    coupon.getFinalAmount());
            session.removeAttribute("coupon");
        }
        return "success";
    }
}