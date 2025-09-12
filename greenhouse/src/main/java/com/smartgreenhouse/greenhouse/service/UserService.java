package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.user.ChangePasswordRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.User;

public interface UserService {
    UserDTO getCurrentUser(String email);

    User getUserByEmail(String email);

    void changePassword(String email, ChangePasswordRequest request);
}
