package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String mobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    private String password;
    private String profileImage;
    private String role;

    //For suspicious activity
    private Integer failedAttempt = 0;
    private boolean accountNonLocked = true;
    private LocalDateTime lockTime;
}