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

    @GetMapping("/{id}/settings")
    public ResponseEntity<GreenhouseSettingsDTO> getGreenhouseSettings(@PathVariable Long id){
        GreenhouseSettingsDTO settingsDTO = greenhouseService.getSettings(id);
        return ResponseEntity.ok(settingsDTO);
    }

    @PatchMapping("/{id}/auto-watering")
    public ResponseEntity<Void> toggleAutoWatering(@PathVariable Long id){
        greenhouseService.toggleAutoWatering(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/settings")
    public ResponseEntity<GreenhouseDTO> updateSettings(@PathVariable Long id, @Valid @RequestBody GreenhouseSettingsDTO settingsDTO){
        GreenhouseDTO greenhouseDTO = greenhouseService.updateSettings(id, settingsDTO);
        return ResponseEntity.ok(greenhouseDTO);
    }

    @GetMapping("/overview")
    public ResponseEntity<List<GreenhouseOverviewDTO>> getGreenhousesOverview(){
        List<GreenhouseOverviewDTO> greenhousesOverview = greenhouseService.getGreenhousesOverview();
        return ResponseEntity.ok(greenhousesOverview);
    }

    @GetMapping("/{id}/sensors")
    public ResponseEntity<List<SensorDTO>> getGreenhouseSensors(@PathVariable Long id){
        List<SensorDTO> sensorsByGreenhouseId = greenhouseService.getSensorsByGreenhouseId(id);
        return ResponseEntity.ok(sensorsByGreenhouseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GreenhouseDTO> getGreenhouseById(@PathVariable Long id) {
        GreenhouseDTO greenhouse = greenhouseService.getGreenhouseById(id);
        return ResponseEntity.ok(greenhouse);
    }

    @GetMapping
    public ResponseEntity<List<GreenhouseDTO>> getAllGreenhouses() {
        List<GreenhouseDTO> allGreenhouses = greenhouseService.getAllGreenhouses();
        return ResponseEntity.ok(allGreenhouses);
    }

    @PostMapping
    public ResponseEntity<GreenhouseDTO> createGreenhouse(@RequestBody @Valid CreateGreenhouseDTO createGreenhouseDTO,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        GreenhouseDTO createdGreenhouse = greenhouseService.createGreenhouse(createGreenhouseDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGreenhouse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GreenhouseDTO> updateGreenhouse(@PathVariable Long id,
                                                          @RequestBody @Valid UpdateGreenhouseDTO updateGreenhouseDTO) {
        GreenhouseDTO updatedGreenhouse = greenhouseService.updateGreenhouse(id, updateGreenhouseDTO);
        return ResponseEntity.ok(updatedGreenhouse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreenhouse(@PathVariable Long id) {
        greenhouseService.deleteGreenhouse(id);
        return ResponseEntity.noContent().build();
    }
}
