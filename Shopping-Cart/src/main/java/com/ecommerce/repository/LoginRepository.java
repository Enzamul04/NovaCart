package com.ecommerce.repository;

import com.ecommerce.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<UserDtls, Integer> {

    UserDtls findByEmail(String email);

}