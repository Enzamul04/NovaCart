package com.ecommerce.service;
import com.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Integer id);
    Boolean deleteProduct(Integer id);
    Product updateProduct(Product product, MultipartFile image);
    List<Product> getAllActiveProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> searchProduct(String keyword);
    Page<Product> getAllActiveProductsPagination(Integer pageNo);
}