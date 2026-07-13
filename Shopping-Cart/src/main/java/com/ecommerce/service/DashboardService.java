package com.ecommerce.service;

public interface DashboardService {
    Long getTotalUsers();
    Long getTotalProducts();
    Long getTotalCategories();
    Long getTotalOrders();
    Long getStatusCount(String status);
    Double getTotalRevenue();
}
