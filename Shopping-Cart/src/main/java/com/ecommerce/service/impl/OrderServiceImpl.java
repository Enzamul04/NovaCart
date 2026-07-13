package com.ecommerce.service.impl;

import com.ecommerce.model.Cart;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDtls;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void saveOrder(
            Integer userId,
            String paymentType,
            String paymentId,
            String razorpayOrderId,
            String couponCode,
            Double discountAmount,
            Double finalAmount)  {
        UserDtls user = userRepository.findById(userId).get();
        List<Cart> carts = cartRepository.findByUser(user);
        for (Cart cart : carts) {
            ProductOrder order = new ProductOrder();
            order.setUser(user);
            order.setProduct(cart.getProduct());
            order.setQuantity(cart.getQuantity());
            order.setPrice(cart.getProduct().getDiscountPrice());
            order.setTotalPrice(cart.getTotalPrice());
            order.setCouponCode(couponCode);
            order.setDiscountAmount(discountAmount);
            if (finalAmount != null) {
                order.setFinalAmount(finalAmount);
            } else {
                order.setFinalAmount(cart.getTotalPrice());
            }
            order.setOrderDate(LocalDate.now());
            order.setStatus("Ordered");
            order.setPaymentType(paymentType);
            order.setPaymentId(paymentId);
            order.setRazorpayOrderId(razorpayOrderId);
            if ("ONLINE".equals(paymentType)) {
                order.setPaymentStatus("PAID");
            }
            else if ("DEMO".equals(paymentType)) {
                order.setPaymentStatus("DEMO_SUCCESS");
            }
            else {
                order.setPaymentStatus("PENDING");
            }
            order.setOrderId(UUID.randomUUID().toString());
            orderRepository.save(order);
        }
        String body = """
<html>
<body style="font-family:Arial">
<h2 style="color:#0d6efd;">
🎉 Order Placed Successfully
</h2>
<p>Hello <b>%s</b>,</p>
<p>Thank you for shopping with <b>NovaCart</b>.</p>
<p>Your order has been placed successfully.</p>

<hr>
<p><b>Payment Type :</b> %s</p>
<p><b>Total Items :</b> %d</p>
<p><b>Order Date :</b> %s</p>
<hr>
<p>We will notify you once your order is shipped.</p>
<br>
<p>
Regards,<br>
<b>NovaCart Team</b>
</p>
</body>
</html>
""".formatted(
                user.getName(),
                paymentType,
                carts.size(),
                LocalDate.now()
        );

        emailService.sendHtmlEmail(
                user.getEmail(),
                "NovaCart - Order Confirmed",
                body
        );
        cartRepository.deleteAll(carts);
    }

    @Override
    public List<ProductOrder> getOrders(Integer userId) {
        UserDtls user = userRepository.findById(userId).get();
        return orderRepository.findByUser(user);
    }
    @Override
    public List<ProductOrder> getAllOrders() {
        return orderRepository.findAll();
    }
    @Override
    public void updateOrderStatus(Integer orderId, String status) {
        ProductOrder order =orderRepository.findById(orderId).get();
        order.setStatus(status);
        orderRepository.save(order);
        UserDtls user = order.getUser();
        String body = """
<h2>Order Status Updated</h2>
<p>Hello %s,</p>
<p>Your order has been placed successfully.</p>
<p><b>Order Status :</b> %s</p>
<p><b>Payment :</b> %s</p>
<p><b>Coupon :</b> %s</p>
<p><b>Discount :</b> ₹ %.2f</p>
<p><b>Final Amount :</b> ₹ %.2f</p>
<p>Thank you for shopping with us.</p>
""".formatted(
                user.getName(),
                order.getStatus(),
                order.getPaymentType(),
                order.getCouponCode() == null ? "N/A" : order.getCouponCode(),
                order.getDiscountAmount() == null ? 0.0 : order.getDiscountAmount(),
                order.getFinalAmount() == null ? order.getTotalPrice() : order.getFinalAmount()
        );
        emailService.sendHtmlEmail(
                user.getEmail(),
                "NovaCart - Order Status Updated",
                body);
    }
}