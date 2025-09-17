package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.dto.user.ChangePasswordRequest;
import com.smartgreenhouse.greenhouse.dto.user.UserDTO;
import com.smartgreenhouse.greenhouse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/profile")
@Tag(name = "User Profile", description = "APIs for managing user profile and account settings")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping()
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO currentUser = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(currentUser);
    }

    @Operation(
            summary = "Change user password",
            description = "Changes the password for the currently authenticated user. " +
                    "Requires the current password for verification."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Password changed successfully\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid current password or new password validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("message","Password changed successfully"));
    }
}
