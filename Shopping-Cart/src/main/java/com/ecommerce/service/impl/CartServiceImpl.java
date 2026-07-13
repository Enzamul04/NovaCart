package com.ecommerce.service.impl;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDtls;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        UserDtls user = userRepository.findById(userId).get();
        Product product = productRepository.findById(productId).get();
        Cart cart = cartRepository.findByUserAndProduct(user, product);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(1);
            cart.setTotalPrice(product.getDiscountPrice());
        }else{
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(
                    cart.getQuantity() * product.getDiscountPrice()
            );
        }
        return cartRepository.save(cart);
    }

    @Override
    public List<Cart> getCartByUser(Integer userId) {
        UserDtls user = userRepository.findById(userId).get();
        return cartRepository.findByUser(user);
    }

    @Override
    public Integer getCountCart(Integer userId) {
        return 0;
    }

    @Override
    public void updateQuantity(String action, Integer cartId) {
        Cart cart = cartRepository.findById(cartId).get();
        if (action.equals("in")) {
            cart.setQuantity(cart.getQuantity() + 1);
        } else if (action.equals("de")) {
            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
            }
        }
        cart.setTotalPrice(
                cart.getQuantity() * cart.getProduct().getDiscountPrice()
        );
        cartRepository.save(cart);
    }
    @Override
    public void removeCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }
    @Override
    public Double getGrandTotal(Integer userId) {
        List<Cart> carts = getCartByUser(userId);
        Double total = 0.0;
        for (Cart cart : carts) {
            total += cart.getTotalPrice();
        }
        return total;
    }
}