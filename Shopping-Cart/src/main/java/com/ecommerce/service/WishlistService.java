package com.ecommerce.service;

import com.ecommerce.model.Wishlist;

import java.util.List;

public interface WishlistService {
    Wishlist saveWishlist(Integer productId, Integer userId);
    List<Wishlist> getWishlistByUser(Integer userId);
    void removeWishlist(Integer id);
}