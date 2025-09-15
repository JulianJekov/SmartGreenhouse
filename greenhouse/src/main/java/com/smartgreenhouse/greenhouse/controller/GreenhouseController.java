package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.dto.greenhouse.*;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
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
@RequestMapping("/api/greenhouses")
@Tag(name = "Greenhouse Management", description = "APIs for managing greenhouses")
@SecurityRequirement(name = "bearerAuth")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @Operation(
            summary = "Create a new greenhouse",
            description = "Creates a new greenhouse for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Greenhouse created successfully",
                    content = @Content(schema = @Schema(implementation = GreenhouseDTO.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Name already exists"
            )

    })
    @PostMapping
    public ResponseEntity<GreenhouseDTO> createGreenhouse(
            @RequestBody @Valid CreateGreenhouseDTO createGreenhouseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseDTO createdGreenhouse = greenhouseService.createGreenhouse(createGreenhouseDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGreenhouse);
    }

    @Operation(
            summary = "Update greenhouse",
            description = "Updates basic information of an existing greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Greenhouse updated successfully",
                    content = @Content(schema = @Schema(implementation = GreenhouseBasicDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<GreenhouseBasicDTO> updateGreenhouse(
            @PathVariable Long id,
            @RequestBody @Valid UpdateGreenhouseDTO updateGreenhouseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseBasicDTO updatedGreenhouse = greenhouseService
                .updateGreenhouse(id, updateGreenhouseDTO, userDetails.getUsername());
        return ResponseEntity.ok(updatedGreenhouse);
    }

    @Operation(
            summary = "Get greenhouse by ID",
            description = "Retrieves detailed information about a specific greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Greenhouse found",
                    content = @Content(schema = @Schema(implementation = GreenhouseBasicDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Greenhouse not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<GreenhouseBasicDTO> getGreenhouseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseBasicDTO greenhouse = greenhouseService.getGreenhouseById(id, userDetails.getUsername());
        return ResponseEntity.ok(greenhouse);
    }

    @Operation(
            summary = "Get all user greenhouses",
            description = "Retrieves a list of all greenhouses belonging to the authenticated user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of user greenhouses",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GreenhouseBasicDTO.class)))
    )
    @GetMapping
    public ResponseEntity<List<GreenhouseBasicDTO>> getAllUserGreenhouses(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GreenhouseBasicDTO> greenhouses = greenhouseService.getUserGreenhousesBasic(userDetails.getUsername());
        return ResponseEntity.ok(greenhouses);
    }

    @Operation(
            summary = "Delete greenhouse",
            description = "Permanently deletes a greenhouse and all its associated data"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Greenhouse deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Greenhouse not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreenhouse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        greenhouseService.deleteGreenhouse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get greenhouse settings",
            description = "Retrieves the configuration settings for a specific greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Settings retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GreenhouseSettingsDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Greenhouse not found")
    })
    @GetMapping("/{id}/settings")
    public ResponseEntity<GreenhouseSettingsDTO> getGreenhouseSettings(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseSettingsDTO settingsDTO = greenhouseService.getSettings(id, userDetails.getUsername());
        return ResponseEntity.ok(settingsDTO);
    }

    @Operation(
            summary = "Update greenhouse settings",
            description = "Updates the configuration settings for a specific greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Settings updated successfully",
                    content = @Content(schema = @Schema(implementation = GreenhouseSettingsDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Greenhouse not found")
    })
    @PutMapping("/{id}/settings")
    public ResponseEntity<GreenhouseSettingsDTO> updateSettings(
            @PathVariable Long id,
            @Valid @RequestBody GreenhouseSettingsDTO settingsDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseSettingsDTO greenhouseDTO = greenhouseService.updateSettings(id, settingsDTO, userDetails.getUsername());
        return ResponseEntity.ok(greenhouseDTO);
    }

    @Operation(
            summary = "Get greenhouses overview",
            description = "Retrieves an overview of all greenhouses with summary information"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Overview retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = GreenhouseOverviewDTO.class)))
    )
    @GetMapping("/overview")
    public ResponseEntity<List<GreenhouseOverviewDTO>> getGreenhousesOverview(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GreenhouseOverviewDTO> greenhousesOverview = greenhouseService
                .getGreenhousesOverview(userDetails.getUsername());
        return ResponseEntity.ok(greenhousesOverview);
    }

    @Operation(
            summary = "Get greenhouse sensors",
            description = "Retrieves all sensors associated with a specific greenhouse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sensors retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SensorDTO.class)))
            ),
            @ApiResponse(responseCode = "404", description = "Greenhouse not found")
    })
    @GetMapping("/{id}/sensors")
    public ResponseEntity<List<SensorDTO>> getGreenhouseSensors(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SensorDTO> sensorsByGreenhouseId = greenhouseService.getSensorsByGreenhouseId(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorsByGreenhouseId);
    }

    @Operation(
            summary = "Toggle auto-watering",
            description = "Toggles the auto-watering feature for a greenhouse on/off"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Auto-watering toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Greenhouse not found")
    })
    @PatchMapping("/{id}/auto-watering")
    public ResponseEntity<Void> toggleAutoWatering(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        greenhouseService.toggleAutoWatering(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
