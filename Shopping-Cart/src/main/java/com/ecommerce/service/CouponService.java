package com.ecommerce.service;

import com.ecommerce.model.Coupon;

import java.util.List;

public interface CouponService {
    Coupon saveCoupon(Coupon coupon);
    List<Coupon> getAllCoupons();
    Coupon getCouponById(Integer id);
    void deleteCoupon(Integer id);
    Coupon getCouponByCode(String couponCode);
    Double applyCoupon(String couponCode, Double grandTotal);
}