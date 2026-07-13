package com.ecommerce.service;

import jakarta.servlet.http.HttpServletResponse;

public interface InvoiceService {
    void generateInvoice(Integer orderId, HttpServletResponse response)
            throws Exception;
}