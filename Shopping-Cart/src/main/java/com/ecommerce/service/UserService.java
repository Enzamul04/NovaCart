package com.ecommerce.service;
import com.ecommerce.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserDtls saveUser(UserDtls user);
    Boolean existsEmail(String email);
    UserDtls getUserByEmail(String email);
    List<UserDtls> getAllUsers();
    void updateAccountStatus(Integer userId, Boolean status);
    void updatePassword(String email, String password);
    UserDtls updateProfile(UserDtls user, MultipartFile img);
    boolean changePassword(String email, String currentPassword, String newPassword);
}
