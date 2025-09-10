package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.UserRepository;
import com.smartgreenhouse.greenhouse.service.UserService;
import com.smartgreenhouse.greenhouse.util.userMapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDTO getCurrentUser(String email) {
        User user = getUser(email);
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByEmail(String email) {
        return getUser(email);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
    }
}
