package com.ecommerce.service;

import com.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {
    public Category saveCategory(Category category);
public Boolean existCategory(String name);
    public  List<Category> getAllCategory();
    public Boolean deleteCategory(int id);
    Boolean deleteCategory(String name);
    Category getCategoryById(int id);
    List<Category> getAllActiveCategory();
}
