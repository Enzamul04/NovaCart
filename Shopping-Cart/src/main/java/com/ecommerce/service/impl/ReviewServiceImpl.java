package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.model.Review;
import com.ecommerce.model.UserDtls;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl
        implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Review saveReview(
            Integer productId,
            Integer userId,
            Integer rating,
            String reviewText) {
        Product product = productRepository.findById(productId).get();
        UserDtls user =userRepository.findById(userId).get();
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setReview(reviewText);
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewByProduct(Integer productId) {
        Product product = productRepository.findById(productId).get();
        return reviewRepository.findByProduct(product);
    }
}