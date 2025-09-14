package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
@Tag(name = "Email Verification", description = "Email verification and resend endpoints")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Operation(summary = "Verify email address",
            description = "Verifies user's email address using the verification token sent to their email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Verification email sent\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid, expired or already used verification token")
    })
    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @Operation(summary = "Resend verification email",
            description = "Resend email verification link to the user's email address. " +
                    "Rate limited to prevent spam (max 3 attempts per hour).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification email sent successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Verification email sent\"}"))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "429", description = "Too many verification requests. Please try again later.")
    })
    @PostMapping("/resend")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(@RequestParam String email) {
        emailVerificationService.resendVerificationEmail(email);
        return ResponseEntity.ok(Map.of("message", "Verification email sent"));
    }
}
