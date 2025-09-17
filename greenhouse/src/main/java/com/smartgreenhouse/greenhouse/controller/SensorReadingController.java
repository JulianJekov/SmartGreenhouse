package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;
import com.smartgreenhouse.greenhouse.service.SensorReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-readings")
@Tag(name = "Sensor Readings", description = "APIs for managing and retrieving sensor reading data")
@SecurityRequirement(name = "bearerAuth")
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    public SensorReadingController(SensorReadingService sensorReadingService) {
        this.sensorReadingService = sensorReadingService;
    }

    @Operation(
            summary = "Create a new sensor reading",
            description = "Creates a new sensor reading for a specific sensor. " +
                    "Typically used by IoT devices or simulation services to report sensor data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor reading created successfully",
                    content = @Content(schema = @Schema(implementation = SensorReadingDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid sensor reading data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<SensorReadingDTO> createSensorReading(
            @Valid @RequestBody CreateSensorReadingDTO createSensorReadingDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        SensorReadingDTO sensorReading = sensorReadingService
                .createSensorReading(createSensorReadingDTO, userDetails.getUsername());
        return ResponseEntity.ok(sensorReading);
    }

    @Operation(
            summary = "Get latest sensor reading",
            description = "Retrieves the most recent reading for a specific sensor."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Latest sensor reading retrieved",
                    content = @Content(schema = @Schema(implementation = SensorReadingDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found, access denied, or no readings available",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/latest")
    public ResponseEntity<SensorReadingDTO> getLatestSensorReadings(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal UserDetails userDetails) {

        SensorReadingDTO latestSensorReading = sensorReadingService
                .getLatestSensorReading(sensorId, userDetails.getUsername());
        return ResponseEntity.ok(latestSensorReading);
    }

    @Operation(
            summary = "Get sensor readings in time range",
            description = "Retrieves sensor readings within a specified time range. " +
                    "Defaults to last 24 hours if no range is specified."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor readings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SensorReadingDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid time range parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/history")
    public ResponseEntity<List<SensorReadingDTO>> getSensorReadingInRange(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        if (from == null) from = Instant.now().minus(24, ChronoUnit.DAYS);
        if (to == null) to = Instant.now();
        List<SensorReadingDTO> sensorReadingsInRange = sensorReadingService
                .getSensorReadingsInRange(sensorId, userDetails.getUsername(), from, to);
        return ResponseEntity.ok(sensorReadingsInRange);
    }

    @Operation(
            summary = "Get all sensor readings from last month",
            description = "Retrieves all sensor readings from the past month for a specific sensor."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor readings retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SensorReadingDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public List<SensorReadingDTO> getAllReadingsForSensor(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Instant from = Instant.now().minus(1, ChronoUnit.MONTHS);
        Instant to = Instant.now();
        return sensorReadingService.getSensorReadingsInRange(sensorId, userDetails.getUsername(), from, to);
    }
}

