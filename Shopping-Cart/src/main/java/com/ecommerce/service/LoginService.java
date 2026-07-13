package com.ecommerce.service;

import com.ecommerce.model.UserDtls;

public interface LoginService {
    UserDtls saveUser(UserDtls user);
    Boolean existsEmail(String email);
    UserDtls login(String email, String password);
}
