package com.ecommerce.service.impl;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CloudinaryService;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        Optional<Product> product = productRepository.findById(id);
        System.out.println("FOUND = " + product);
        return product.orElse(null);
    }

    @Override
    public Boolean deleteProduct(Integer id) {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            productRepository.delete(product.get());
            return true;
        }
        return false;
    }

    @Override
    public Product updateProduct(Product product,
                                 MultipartFile image) {
        Product dbProduct = getProductById(product.getId());
        String imageUrl;
        if (image.isEmpty()) {
            imageUrl = dbProduct.getImage();
        } else {
            imageUrl = cloudinaryService.uploadFile(image);}
        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setIsActive(product.getIsActive());
// Auto Calculate Discount Price
        double discountPrice =
                product.getPrice()
                        - (product.getPrice() * product.getDiscount() / 100.0);
        dbProduct.setDiscountPrice(discountPrice);
        dbProduct.setImage(imageUrl);
        return productRepository.save(dbProduct);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        List<Product>products=productRepository.findByIsActiveTrue();
        return products;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndIsActiveTrue(category);
    }

    @Override
    public List<Product> searchProduct(String keyword) {
        return productRepository
                .findByTitleContainingIgnoreCaseAndIsActiveTrue(keyword);
    }

    @Override
    public Page<Product> getAllActiveProductsPagination(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 8);
        return productRepository.findByIsActiveTrue(pageable);
    }

}