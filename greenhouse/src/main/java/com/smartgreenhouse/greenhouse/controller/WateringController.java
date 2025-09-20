package com.smartgreenhouse.greenhouse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.mqtt.MqttWateringPublisher;
import com.smartgreenhouse.greenhouse.mqtt.WateringEventDTO;
import com.smartgreenhouse.greenhouse.service.WateringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/watering")
@Tag(name = "Watering", description = "API for manual watering")
public class WateringController {

    private final MqttWateringPublisher wateringPublisher;

    public WateringController(MqttWateringPublisher wateringPublisher) {
        this.wateringPublisher = wateringPublisher;
    }

    @Operation(
            summary = "Manual watering",
            description = "Initiates a manual watering operation for a specific greenhouse. " +
                    "This allows users to manually trigger watering outside of automated schedules"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Watering initiated successfully",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Watering completed successfully\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid parameters - water amount must be positive",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Greenhouse is already being watered",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "424",
                    description = "Watering failed after multiple attempts",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @PostMapping("/manual")
    public ResponseEntity<Map<String, String>> manualWatering(
            @RequestParam Long greenhouseId,
            @RequestParam Double amount,
            @AuthenticationPrincipal UserDetails userDetails) throws MqttException, JsonProcessingException {
        wateringPublisher.publishWateringEvent(
                new WateringEventDTO(greenhouseId, userDetails.getUsername(), amount, WateringSource.MANUAL));
        return ResponseEntity.ok(Map.of("message", "Watering completed successfully"));
    }
}
