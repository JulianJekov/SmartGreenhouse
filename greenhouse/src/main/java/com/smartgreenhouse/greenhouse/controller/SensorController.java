package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorStatsDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;
import com.smartgreenhouse.greenhouse.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(
            @RequestBody @Valid CreateSensorDTO createSensorDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO createdSensor = sensorService.createSensor(createSensorDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long id,
                                                  @RequestBody @Valid UpdateSensorDTO updateSensorDTO,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO updatedSensor = sensorService.updateSensor(id, updateSensorDTO, userDetails.getUsername());
        return ResponseEntity.ok(updatedSensor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorDTO sensorById = sensorService.getSensorById(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorById);
    }

    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors(@AuthenticationPrincipal UserDetails userDetails) {
        List<SensorDTO> allSensors = sensorService.getAllSensors(userDetails.getUsername());
        return ResponseEntity.ok(allSensors);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        sensorService.deleteSensor(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<SensorStatsDTO> getSensorStats(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        SensorStatsDTO sensorStats = sensorService.getSensorStats(id, userDetails.getUsername());
        return ResponseEntity.ok(sensorStats);
    }
}
