package com.ecommerce.service.impl;

import com.ecommerce.model.Coupon;
import com.ecommerce.repository.CouponRepository;
import com.ecommerce.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Integer id) {
        return couponRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCoupon(Integer id) {
        couponRepository.deleteById(id);
    }

    @Override
    public Coupon getCouponByCode(String couponCode) {
        return couponRepository.findByCouponCode(couponCode);
    }

    @Override
    public Double applyCoupon(String couponCode,
                              Double grandTotal) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode);
        if (coupon == null) {
            return null;
        }
        if (!coupon.getActive()) {
            return null;
        }
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            return null;
        }
        if (grandTotal < coupon.getMinimumAmount()) {
            return null;
        }
        Double discount = grandTotal * coupon.getDiscount() / 100;
        return grandTotal - discount;
    }
}