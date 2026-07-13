package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderId;
    private LocalDate orderDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDtls user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
    private String couponCode;
    private Double discountAmount;
    private Double finalAmount;
    private String paymentType;
    private String status;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String mobileNo;
    private String paymentId;
    private String razorpayOrderId;
    private String paymentStatus;
}