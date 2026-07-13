package com.ecommerce.repository;

import com.ecommerce.model.Product;
import com.ecommerce.model.UserDtls;
import com.ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    Wishlist findByUserAndProduct(UserDtls user, Product product);
    List<Wishlist> findByUser(UserDtls user);
}