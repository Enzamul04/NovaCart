package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String couponCode;
    private Double discount;
    private Double minimumAmount;
    private LocalDate expiryDate;
    private Boolean active;
}