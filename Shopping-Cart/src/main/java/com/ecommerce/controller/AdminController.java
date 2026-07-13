package com.ecommerce.controller;

import com.ecommerce.model.*;
import com.ecommerce.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ecommerce.service.CloudinaryService;
import com.ecommerce.service.CouponService;
import com.ecommerce.model.Coupon;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CouponService couponService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("users",dashboardService.getTotalUsers());
        model.addAttribute("products",dashboardService.getTotalProducts());
        model.addAttribute("categories",dashboardService.getTotalCategories());
        model.addAttribute("orders",dashboardService.getTotalOrders());
        model.addAttribute("ordered",dashboardService.getStatusCount("Ordered"));
        model.addAttribute("packed",dashboardService.getStatusCount("Packed"));
        model.addAttribute("shipped",dashboardService.getStatusCount("Shipped"));
        model.addAttribute("delivered",dashboardService.getStatusCount("Delivered"));
        model.addAttribute("cancelled",dashboardService.getStatusCount("Cancelled"));
        model.addAttribute("revenue", dashboardService.getTotalRevenue());
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories=categoryService.getAllCategory();
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/AddProduct";
    }
    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(
            @ModelAttribute Category category,
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        category.setImageName(imageName);
        Boolean existCategory = categoryService.existCategory(category.getName());
        if (existCategory) {
            session.setAttribute("errorMsg", "Category already exists");
        } else {
            Category saveCategory = categoryService.saveCategory(category);
            if (saveCategory != null) {
                if (!file.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(
                            saveFile.getAbsolutePath()
                                    + File.separator
                                    + "category_img"
                                    + File.separator
                                    + file.getOriginalFilename());
                    Files.copy(
                            file.getInputStream(),
                            path,
                            StandardCopyOption.REPLACE_EXISTING);
                }
                session.setAttribute("succMsg",
                        "Category saved successfully");
            } else {
                session.setAttribute("errorMsg",
                        "Internal Server Error");
            }
        }
        return "redirect:/admin/category";
    }
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id,HttpSession session){
        Boolean deleteCategory=categoryService.deleteCategory(id);
        if(deleteCategory){
            session.setAttribute("succMsg","category delete success");
        }else{
            session.setAttribute("errorMsg","something wrong on server");
        }
return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m) {
        Category category = categoryService.getCategoryById(id);
        m.addAttribute("category", category);
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(
            @ModelAttribute Category category,
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty()
                ? oldCategory.getImageName()
                : file.getOriginalFilename();
        category.setImageName(imageName);
        Category updateCategory = categoryService.saveCategory(category);
        if (updateCategory != null) {
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(
                        saveFile.getAbsolutePath()
                                + File.separator
                                + "category_img"
                                + File.separator
                                + file.getOriginalFilename());
                Files.copy(
                        file.getInputStream(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);}

            session.setAttribute("succMsg",
                    "Category Updated Successfully");
        } else {
            session.setAttribute("errorMsg",
                    "Something went wrong");
        }
        return "redirect:/admin/loadEditCategory/" + category.getId();
    }

    // Save product

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {

        try {
            String imageUrl = cloudinaryService.uploadFile(file);
            product.setImage(imageUrl);
            product.setDiscount(0);
            product.setDiscountPrice((double) product.getPrice());
            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute(
                    "succMsg",
                    "Product Saved Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    "Something went wrong");
        }
        return "redirect:/admin/loadAddProduct";
    }

//    Start Products
    @GetMapping("/products")
    public String viewProducts(Model m) {
        List<Product> products = productService.getAllProducts();
        m.addAttribute("products", products);
        return "admin/products";
    }
//  for product delete
@GetMapping("/deleteProduct/{id}")
public String deleteProduct(@PathVariable Integer id,
                            HttpSession session) {

    Boolean deleteProduct = productService.deleteProduct(id);
    if (deleteProduct) {
        session.setAttribute("succMsg", "Product Deleted Successfully");
    } else {
        session.setAttribute("errorMsg", "Something went wrong on server");
    }
    return "redirect:/admin/products";
}

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable Integer id, Model m) {

        Product product = productService.getProductById(id);

        System.out.println("ID = " + id);
        System.out.println("PRODUCT = " + product);

        m.addAttribute("product", product);
        m.addAttribute("categories", categoryService.getAllCategory());

        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("file") MultipartFile image,
                                RedirectAttributes redirectAttributes) {

        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    "Invalid Discount! Please enter a value between 0 and 100.");

            return "redirect:/admin/editProduct/" + product.getId();
        }

        Product updateProduct = productService.updateProduct(product, image);

        if (updateProduct != null) {
            redirectAttributes.addFlashAttribute(
                    "succMsg",
                    "The product has been updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    "Failed to update product. Please try again.");
        }

        return "redirect:/admin/editProduct/" + product.getId();
    }
//End Products

@GetMapping("/orders")
public String orders(Model model) {
    List<ProductOrder> orders = orderService.getAllOrders();
    model.addAttribute("orders", orders);
    return "admin/orders";
    }
    @GetMapping("/update-order-status")
    public String updateOrderStatus(
            @RequestParam Integer id,
            @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return "redirect:/admin/orders";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDtls> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/update-status")
    public String updateUserStatus(
            @RequestParam Integer id,
            @RequestParam Boolean status) {
        userService.updateAccountStatus(id, status);
        return "redirect:/admin/users";
    }

    @GetMapping("/coupons")
    public String couponPage(Model model) {
        model.addAttribute("coupons", couponService.getAllCoupons());
        model.addAttribute("coupon", new Coupon());
        return "admin/coupon";

    }

    @PostMapping("/saveCoupon")
    public String saveCoupon(@ModelAttribute Coupon coupon, RedirectAttributes redirectAttributes) {
        couponService.saveCoupon(coupon);
        redirectAttributes.addFlashAttribute(
                "succMsg",
                "Coupon Saved Successfully");
        return "redirect:/admin/coupons";
    }

    @GetMapping("/deleteCoupon/{id}")
    public String deleteCoupon(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        couponService.deleteCoupon(id);
        redirectAttributes.addFlashAttribute(
                "succMsg",
                "Coupon Deleted Successfully");
        return "redirect:/admin/coupons";
    }

    @GetMapping("/editCoupon/{id}")
    public String editCoupon(@PathVariable Integer id, Model model) {
        model.addAttribute("coupon", couponService.getCouponById(id));
        model.addAttribute("coupons", couponService.getAllCoupons());
        return "admin/edit_coupon";
    }

    @PostMapping("/updateCoupon")
    public String updateCoupon(@ModelAttribute Coupon coupon, RedirectAttributes redirectAttributes) {
        couponService.saveCoupon(coupon);
        redirectAttributes.addFlashAttribute(
                "succMsg",
                "Coupon Updated Successfully");
        return "redirect:/admin/coupons";
    }

}