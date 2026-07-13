package com.ecommerce.controller;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDtls;
import com.ecommerce.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.service.CloudinaryService;
import java.util.Collections;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CloudinaryService cloudinaryService;


    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productService.getAllActiveProducts();
        Collections.shuffle(products);
        if(products.size() > 8){
            products = products.subList(0,8);
        }
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot_password";

    }

    @GetMapping("/products")
    public String products( @RequestParam(defaultValue = "0") Integer pageNo,
                            Model model) {
        List<Category> categories = categoryService.getAllActiveCategory();
        Page<Product> page = productService.getAllActiveProductsPagination(pageNo);
        model.addAttribute("categories", categories);
        model.addAttribute("products", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("paramValue", "All");
        return "product";
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.getReviewByProduct(id));
        return "view_product";
    }

    @GetMapping("/products/{category}")
    public String productsByCategory(
            @PathVariable String category, Model m) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getProductsByCategory(category);

        m.addAttribute("categories", categories);
        m.addAttribute("products", products);
        m.addAttribute("selectedCategory", category);
        m.addAttribute("paramValue", category);
        return "product";
    }

    // User Registration

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user,
                           @RequestParam(value = "profile_image", required = false)
                           MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        System.out.println("PinCode = " + user.getPinCode());

        try {
            Boolean exists = userService.existsEmail(user.getEmail());
            if (exists) {
                redirectAttributes.addFlashAttribute(
                        "errorMsg",
                        "Email already exists");
            } else {
                String imageUrl = "https://res.cloudinary.com/<YOUR_CLOUD_NAME>/image/upload/default_profile.png";
                if (file != null && !file.isEmpty()) {
                    imageUrl = cloudinaryService.uploadFile(file);
                }
                user.setProfileImage(imageUrl);
                user.setRole("ROLE_USER");
                UserDtls saveUser = userService.saveUser(user);
                if (saveUser != null) {
                    redirectAttributes.addFlashAttribute(
                            "succMsg",
                            "Registration Successful");
                } else {
                    redirectAttributes.addFlashAttribute(
                            "errorMsg",
                            "Something went wrong");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR = " + e.getMessage());
            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    e.getMessage());
        }
        return "redirect:/register";
    }

    @PostMapping("/send-otp")
    public String sendOtp(
            @RequestParam String email,
            HttpSession session,
            Model model) {
        UserDtls user = userService.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("errorMsg",
                    "Invalid Email");
            return "forgot_password";
        }
        int otp = new Random().nextInt(900000) + 100000;
        emailService.sendEmail(
                email,
                "Password Reset OTP",
                "Your OTP is : " + otp
        );
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        return "verify_otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam Integer otp,
            HttpSession session,
            Model model) {
        Integer sessionOtp = (Integer) session.getAttribute("otp");
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            return "reset_password";
        } else {
            model.addAttribute("errorMsg",
                    "Invalid OTP");
            return "verify_otp";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String password,
            HttpSession session,
            Model model) {
        String email = (String) session.getAttribute("email");
        userService.updatePassword(email, password);
        session.removeAttribute("otp");
        session.removeAttribute("email");
        model.addAttribute("succMsg",
                "Password Updated Successfully");
        return "login";
    }

    @GetMapping("/search")
    public String searchProduct(
            @RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProduct(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "search_result";
    }

}