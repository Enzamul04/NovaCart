package com.ecommerce.service.impl;

import com.ecommerce.repository.CategoryRepository;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl
        implements DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductOrderRepository orderRepository;

    @Override
    public Long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public Long getTotalProducts() {
        return productRepository.count();
    }
    @Override
    public Long getTotalCategories() {
        return categoryRepository.count();
    }

    @Override
    public Long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public Long getStatusCount(String status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public Double getTotalRevenue() {
        Double revenue = orderRepository.getTotalRevenue();
        return revenue == null ? 0.0 : revenue;
    }

}