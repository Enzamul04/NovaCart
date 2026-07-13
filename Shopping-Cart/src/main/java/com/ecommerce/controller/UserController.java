package com.ecommerce.controller;

import com.ecommerce.config.CustomUser;
import com.ecommerce.model.Cart;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDtls;
import com.ecommerce.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import com.ecommerce.dto.CouponDto;


import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/")
    public String home(){
     return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid,
            Authentication authentication) {
        CustomUser loggedUser =
                (CustomUser) authentication.getPrincipal();
        String email = loggedUser.getUsername();
        UserDtls user = userService.getUserByEmail(email);
        cartService.saveCart(pid, user.getId());
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCart(Authentication authentication, Model model) {

        CustomUser loggedUser = (CustomUser) authentication.getPrincipal();
        String email = loggedUser.getUsername();
        UserDtls user = userService.getUserByEmail(email);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        Double grandTotal = cartService.getGrandTotal(user.getId());
        model.addAttribute("carts", carts);
        model.addAttribute("grandTotal", grandTotal);
        return "user/cart";
    }

    @GetMapping("/updateQuantity")
    public String updateQuantity(
            @RequestParam String action,
            @RequestParam Integer cid) {
        cartService.updateQuantity(action, cid);
        return "redirect:/user/cart";
    }
    @GetMapping("/removeCart")
    public String removeCart(@RequestParam Integer cid) {
        cartService.removeCart(cid);
        return "redirect:/user/cart";
    }
    @GetMapping("/orders")
    public String orderPage(Authentication authentication,
                            Model model) {
        CustomUser loggedUser = (CustomUser) authentication.getPrincipal();
        String email = loggedUser.getUsername();
        UserDtls user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
        List<Cart> carts =
                cartService.getCartByUser(user.getId());
        model.addAttribute("carts", carts);
        Double grandTotal =
                cartService.getGrandTotal(user.getId());
        model.addAttribute("grandTotal", grandTotal);
        return "user/orders";
    }

    @GetMapping("/placeOrder")
    public String placeOrder(Authentication authentication,
                             HttpSession session) {
        CustomUser loggedUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(loggedUser.getUsername());
        CouponDto coupon = (CouponDto) session.getAttribute("coupon");
        if (coupon == null) {
            orderService.saveOrder(
                    user.getId(),
                    "COD",
                    null,
                    null,
                    null,
                    0.0,
                    null);
        } else {
            orderService.saveOrder(
                    user.getId(),
                    "COD",
                    null,
                    null,
                    coupon.getCouponCode(),
                    coupon.getDiscountAmount(),
                    coupon.getFinalAmount());
            session.removeAttribute("coupon");
        }
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "user/success";
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication,
                           Model model) {
        CustomUser loggedUser = (CustomUser) authentication.getPrincipal();
        String email = loggedUser.getUsername();
        UserDtls user = userService.getUserByEmail(email);

        List<ProductOrder> orders = orderService.getOrders(user.getId());
        model.addAttribute("orders", orders);
        return "user/my_orders";
    }

    @PostMapping("/saveReview")
    public String saveReview(
            @RequestParam Integer pid,
            @RequestParam Integer rating,
            @RequestParam String review,
            Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user =
                userService.getUserByEmail(customUser.getUsername());
        reviewService.saveReview(
                pid, user.getId(), rating, review);
        return "redirect:/product/" + pid;
    }
    @GetMapping("/addWishlist")
    public String addWishlist(@RequestParam Integer pid,
                              Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        wishlistService.saveWishlist(pid, user.getId());
        return "redirect:/product/" + pid;
    }

    @GetMapping("/wishlist")
    public String wishlist(Authentication authentication, Model model) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        model.addAttribute("wishlists",
                wishlistService.getWishlistByUser(user.getId()));
        return "user/wishlist";
    }
    @GetMapping("/removeWishlist")
    public String removeWishlist(@RequestParam Integer id) {
        wishlistService.removeWishlist(id);
        return "redirect:/user/wishlist";
    }

    @GetMapping("/invoice")
    public void downloadInvoice(@RequestParam Integer orderId,
            HttpServletResponse response)
            throws Exception {
        invoiceService.generateInvoice(orderId, response);
    }

    @GetMapping("/demoPayment")
    public String demoPayment(Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        orderService.saveOrder(
                user.getId(),
                "DEMO",
                "DEMO-PAY-" + System.currentTimeMillis(),
                "DEMO-ORDER-" + System.currentTimeMillis(),
                null,
                0.0,
                null);
        return "redirect:/user/success";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Authentication authentication, Model model) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(customUser.getUsername());
        model.addAttribute("user", user);
        return "user/edit_profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(
            @ModelAttribute UserDtls user,
            @RequestParam(value = "img", required = false)
            MultipartFile img,
            RedirectAttributes redirectAttributes) {
        userService.updateProfile(user, img);
        redirectAttributes.addFlashAttribute(
                "succMsg",
                "Profile Updated Successfully");
        return "redirect:/user/profile";
    }

    @GetMapping("/buyNow")
    public String buyNow(@RequestParam Integer pid, Authentication authentication) {
        CustomUser loggedUser = (CustomUser) authentication.getPrincipal();
        UserDtls user = userService.getUserByEmail(loggedUser.getUsername());
        cartService.saveCart(pid, user.getId());
        return "redirect:/user/orders";
    }
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "user/change_password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        boolean status = userService.changePassword(
                user.getUsername(),
                currentPassword,
                newPassword);
        if (status) {
            redirectAttributes.addFlashAttribute(
                    "succMsg",
                    "Password Changed Successfully");
        } else {
            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    "Current Password is Incorrect");
        }
        return "redirect:/user/change-password";
    }

}
