package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.greenhouse.*;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/greenhouses")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    @PostMapping
    public ResponseEntity<GreenhouseDTO> createGreenhouse(
            @RequestBody @Valid CreateGreenhouseDTO createGreenhouseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseDTO createdGreenhouse = greenhouseService.createGreenhouse(createGreenhouseDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGreenhouse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GreenhouseBasicDTO> updateGreenhouse(
            @PathVariable Long id,
            @RequestBody @Valid UpdateGreenhouseDTO updateGreenhouseDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseBasicDTO updatedGreenhouse = greenhouseService
                .updateGreenhouse(id, updateGreenhouseDTO, userDetails.getUsername());
        return ResponseEntity.ok(updatedGreenhouse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GreenhouseBasicDTO> getGreenhouseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseBasicDTO greenhouse = greenhouseService.getGreenhouseById(id, userDetails.getUsername());
        return ResponseEntity.ok(greenhouse);
    }

    @GetMapping
    public ResponseEntity<List<GreenhouseBasicDTO>> getAllUserGreenhouses(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GreenhouseBasicDTO> greenhouses = greenhouseService.getUserGreenhousesBasic(userDetails.getUsername());
        return ResponseEntity.ok(greenhouses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreenhouse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        greenhouseService.deleteGreenhouse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/settings")
    public ResponseEntity<GreenhouseSettingsDTO> getGreenhouseSettings(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseSettingsDTO settingsDTO = greenhouseService.getSettings(id, userDetails.getUsername());
        return ResponseEntity.ok(settingsDTO);
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<GreenhouseSettingsDTO> updateSettings(
            @PathVariable Long id,
            @Valid @RequestBody GreenhouseSettingsDTO settingsDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseSettingsDTO greenhouseDTO = greenhouseService.updateSettings(id, settingsDTO, userDetails.getUsername());
        return ResponseEntity.ok(greenhouseDTO);
    }

    @GetMapping("/overview")
    public ResponseEntity<List<GreenhouseOverviewDTO>> getGreenhousesOverview(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GreenhouseOverviewDTO> greenhousesOverview = greenhouseService
                .getGreenhousesOverview(userDetails.getUsername());
        return ResponseEntity.ok(greenhousesOverview);
    }

    @GetMapping("/{id}/sensors")
    public ResponseEntity<List<SensorDTO>> getGreenhouseSensors(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SensorDTO> sensorsByGreenhouseId = greenhouseService.getSensorsByGreenhouseId(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorsByGreenhouseId);
    }

    @PatchMapping("/{id}/auto-watering")
    public ResponseEntity<Void> toggleAutoWatering(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        greenhouseService.toggleAutoWatering(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
