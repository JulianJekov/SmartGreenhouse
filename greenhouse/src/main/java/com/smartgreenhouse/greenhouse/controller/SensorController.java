package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorStatsDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;
import com.smartgreenhouse.greenhouse.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@Tag(name = "Sensor Management", description = "APIs for managing sensors")
@SecurityRequirement(name = "bearerAuth")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }


    @Operation(
            summary = "Create sensor",
            description = "Create new sensor for the authenticated user greenhouse"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Sensor created successfully",
                    content = @Content(schema = @Schema(implementation = SensorDTO.class))
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
            ),
    })

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(
            @RequestBody @Valid CreateSensorDTO createSensorDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO createdSensor = sensorService.createSensor(createSensorDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
    }

    @Operation(
            summary = "Update sensor",
            description = "Updates sensor of an existing greenhouse and authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor updated successfully",
                    content = @Content(schema = @Schema(implementation = SensorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse or Sensor not found. Or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long id,
                                                  @RequestBody @Valid UpdateSensorDTO updateSensorDTO,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO updatedSensor = sensorService.updateSensor(id, updateSensorDTO, userDetails.getUsername());
        return ResponseEntity.ok(updatedSensor);
    }

    @Operation(
            summary = "Get sensor by ID",
            description = "Retrieves detailed information about a specific sensor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor found",
                    content = @Content(schema = @Schema(implementation = SensorDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found or access denied"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO sensorById = sensorService.getSensorById(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorById);
    }

    @Operation(
            summary = "Get all user sensors",
            description = "Retrieves a list of all sensors belonging to the authenticated user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of user sensors",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SensorDTO.class)))
    )
    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors(@AuthenticationPrincipal UserDetails userDetails) {
        List<SensorDTO> allSensors = sensorService.getAllSensors(userDetails.getUsername());
        return ResponseEntity.ok(allSensors);
    }

    @Operation(
            summary = "Delete sensor",
            description = "Permanently deletes a sensor and all its associated data"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Sensor deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found or access denied"
            )
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        sensorService.deleteSensor(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get sensor statistics",
            description = "Retrieves comprehensive statistics and current reading for a specific sensor. " +
                    "Includes current value, min/max/average of last 10 readings, and timestamp of last update. " +
                    "The sensor must be active and accessible to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensor statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SensorStatsDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sensor not found, or access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sensor is not active",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}/stats")
    public ResponseEntity<SensorStatsDTO> getSensorStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorStatsDTO sensorStats = sensorService.getSensorStats(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorStats);
    }
}
