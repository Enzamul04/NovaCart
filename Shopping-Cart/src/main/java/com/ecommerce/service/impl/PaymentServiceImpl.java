package com.ecommerce.service.impl;

import com.ecommerce.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private RazorpayClient razorpayClient;

    @Override
    public JSONObject createOrder(Double amount) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        Order order = razorpayClient.orders.create(orderRequest);
        return order.toJson();
    }

    @Override
    public String verifyPayment(String payload)
            throws Exception {
        JSONObject json = new JSONObject(payload);
        String paymentId = json.getString("razorpay_payment_id");
        String orderId = json.getString("razorpay_order_id");
        String signature = json.getString("razorpay_signature");
        System.out.println("Payment Id : " + paymentId);
        System.out.println("Order Id : " + orderId);
        System.out.println("Signature : " + signature);
        return "success";
    }

}