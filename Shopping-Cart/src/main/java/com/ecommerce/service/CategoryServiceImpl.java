package com.ecommerce.service;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepositary;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepositary.save(category);
    }

    @Override
    public Boolean existCategory(String name) {
        return categoryRepositary.existsByName(name);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepositary.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {
        Category category = categoryRepositary.findById(id).orElse(null);
        if (category != null) {
            categoryRepositary.delete(category);
            return true;
        }
        return false;
    }
    @Override
    public Boolean deleteCategory(String name) {
        return null;
    }
    @Override
    public Category getCategoryById(int id) {
        return categoryRepositary.findById(id).orElse(null);
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories=categoryRepositary.findByIsActiveTrue();
        return categories;
    }
}