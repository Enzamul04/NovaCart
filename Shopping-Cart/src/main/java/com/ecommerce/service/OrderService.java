package com.ecommerce.service;

import com.ecommerce.model.ProductOrder;

import java.util.List;

public interface OrderService {
    void saveOrder(
            Integer userId,
            String paymentType,
            String paymentId,
            String razorpayOrderId,
            String couponCode,
            Double discountAmount,
            Double finalAmount);
    List<ProductOrder> getOrders(Integer userId);
    List<ProductOrder> getAllOrders();
    void updateOrderStatus(Integer orderId, String status);
}
