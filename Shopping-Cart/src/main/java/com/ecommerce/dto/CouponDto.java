package com.ecommerce.dto;

import lombok.Data;

@Data
public class CouponDto {
    private String couponCode;
    private Double discountAmount;
    private Double finalAmount;
}