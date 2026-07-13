package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.model.UserDtls;
import com.ecommerce.model.Wishlist;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishlistRepository;
import com.ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Wishlist saveWishlist(Integer productId, Integer userId) {
        UserDtls user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Wishlist wishlist = wishlistRepository.findByUserAndProduct(user, product);
        if (wishlist == null) {
            wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setProduct(product);
            return wishlistRepository.save(wishlist);
        }
        return wishlist;
    }
    @Override
    public List<Wishlist> getWishlistByUser(Integer userId) {
        UserDtls user = userRepository.findById(userId).get();
        return wishlistRepository.findByUser(user);
    }
    @Override
    public void removeWishlist(Integer id) {
        wishlistRepository.deleteById(id);
    }

}