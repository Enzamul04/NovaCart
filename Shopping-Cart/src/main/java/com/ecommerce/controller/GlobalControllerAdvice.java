package com.ecommerce.controller;

import com.ecommerce.config.CustomUser;
import com.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private CategoryService categoryService;
    @ModelAttribute
    public void loggedUserDetails(Authentication authentication,
                                  Model model) {
        model.addAttribute("categories",
                categoryService.getAllActiveCategory());

        if (authentication != null &&
                authentication.getPrincipal() instanceof CustomUser customUser) {
            model.addAttribute("loggedUser", customUser);
        }
    }
}