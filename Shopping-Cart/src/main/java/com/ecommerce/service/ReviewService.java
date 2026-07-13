package com.ecommerce.service;

import com.ecommerce.model.Review;
import java.util.List;

public interface ReviewService {
     Review saveReview(
            Integer productId,
            Integer userId,
            Integer rating,
            String review);
    List<Review> getReviewByProduct(Integer productId);
}