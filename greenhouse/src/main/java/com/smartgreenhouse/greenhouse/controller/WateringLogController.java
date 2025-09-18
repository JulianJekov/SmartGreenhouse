package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.service.WateringLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watering-logs")
@Tag(name = "WateringLog", description = "APIs for managing watering logs")
@SecurityRequirement(name = "bearerAuth")
public class WateringLogController {

    private final WateringLogService wateringLogService;

    public WateringLogController(WateringLogService wateringLogService) {
        this.wateringLogService = wateringLogService;
    }

    @Operation(
            summary = "Create watering log",
            description = "Create new watering log for a specific greenhouse"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Watering log created successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<WateringLogDTO> createWateringLog(
            @Valid @RequestBody CreateWateringLogDTO createWateringLogDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        WateringLogDTO wateringLog = wateringLogService.createWateringLog(createWateringLogDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(wateringLog);
    }

    @Operation(
            summary = "Get all watering logs",
            description = "Retrieves a paginated list of all watering logs for the authenticated user across all greenhouses"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Watering logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WateringLogDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    @GetMapping
    public ResponseEntity<Page<WateringLogDTO>> getAllWateringLogs(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WateringLogDTO> allWateringLogs = wateringLogService.getAllWateringLogs(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(allWateringLogs);
    }

    @Operation(
            summary = "Get watering logs by greenhouse",
            description = "Retrieves a paginated list of watering logs for a specific greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Greenhouse watering logs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WateringLogDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )}
    )
    @GetMapping("/{id}")
    public ResponseEntity<Page<WateringLogDTO>> getWateringLogByGreenhouse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WateringLogDTO> wateringLogByGreenhouseId = wateringLogService
                .getWateringLogByGreenhouseId(id, userDetails.getUsername(), page, size);
        return ResponseEntity.ok(wateringLogByGreenhouseId);
    }
}
