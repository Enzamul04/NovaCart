package com.ecommerce.repository;

import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByUser(UserDtls user);
    Long countByStatus(String status);
    @Query("SELECT SUM(o.totalPrice) FROM ProductOrder o")
    Double getTotalRevenue();
}