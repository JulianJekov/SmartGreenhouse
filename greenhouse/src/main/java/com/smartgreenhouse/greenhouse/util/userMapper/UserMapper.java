package com.smartgreenhouse.greenhouse.util.userMapper;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setRole(Role.USER);
        return user;
    }

    public AuthResponse toAuthResponse(User user) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole());
        return authResponse;
    }

    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole());
        userDTO.setId(user.getId());
        return userDTO;
    }
}
