package com.ecommerce.service.impl;

import com.ecommerce.model.UserDtls;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.CloudinaryService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;


    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }
    @Override
    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public List<UserDtls> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public void updateAccountStatus(Integer userId, Boolean status) {
        UserDtls user = userRepository.findById(userId).get();
        user.setAccountNonLocked(status);
        userRepository.save(user);
    }

    @Override
    public void updatePassword(String email, String password) {
        UserDtls user = userRepository.findByEmail(email);
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        userRepository.save(user);
    }

    @Override
    public UserDtls updateProfile(
            UserDtls user,
            MultipartFile img) {
        UserDtls dbUser = userRepository.findById(user.getId()).orElse(null);
        dbUser.setName(user.getName());
        dbUser.setMobileNumber(user.getMobileNumber());
        dbUser.setAddress(user.getAddress());
        dbUser.setCity(user.getCity());
        dbUser.setState(user.getState());
        dbUser.setPinCode(user.getPinCode());
        if (img != null && !img.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(img);
            dbUser.setProfileImage(imageUrl);
        }
        return userRepository.save(dbUser);
    }

    @Override
    public boolean changePassword(String email,
                                  String currentPassword,
                                  String newPassword) {
        UserDtls user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}