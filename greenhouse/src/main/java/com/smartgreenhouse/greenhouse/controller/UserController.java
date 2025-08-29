package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.user.AuthResponse;
import com.smartgreenhouse.greenhouse.dto.user.LoginRequest;
import com.smartgreenhouse.greenhouse.dto.user.RegisterRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails){
        UserDTO currentUser = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(currentUser);
    }

}
