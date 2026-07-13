package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review,Integer> {
    List<Review> findByProduct(Product product);
}