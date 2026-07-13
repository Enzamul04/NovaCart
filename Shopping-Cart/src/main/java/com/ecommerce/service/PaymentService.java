package com.ecommerce.service;

import org.json.JSONObject;

public interface PaymentService {
    JSONObject createOrder(Double amount) throws Exception;
    String verifyPayment(String payload) throws Exception;
}