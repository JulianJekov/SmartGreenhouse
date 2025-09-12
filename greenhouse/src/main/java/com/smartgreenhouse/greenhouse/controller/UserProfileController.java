package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.user.ChangePasswordRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO currentUser = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(currentUser);
    }

    //
//    @PutMapping
//    public UserDTO updateProfile(@AuthenticationPrincipal UserDetails userDetails,
//                                 @RequestBody UpdateProfileRequest request) {
//        return userService.updateProfile(userDetails.getUsername(), request);
//    }
//
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
