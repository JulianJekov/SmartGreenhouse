package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;
import com.smartgreenhouse.greenhouse.service.SensorReadingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-readings")
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    public SensorReadingController(SensorReadingService sensorReadingService) {
        this.sensorReadingService = sensorReadingService;
    }

    @PostMapping
    public ResponseEntity<SensorReadingDTO> createSensorReading(
            @Valid @RequestBody CreateSensorReadingDTO createSensorReadingDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        SensorReadingDTO sensorReading = sensorReadingService
                .createSensorReading(createSensorReadingDTO, userDetails.getUsername());
        return ResponseEntity.ok(sensorReading);
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorReadingDTO> getLatestSensorReadings(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal UserDetails userDetails) {

        SensorReadingDTO latestSensorReading = sensorReadingService
                .getLatestSensorReading(sensorId, userDetails.getUsername());
        return ResponseEntity.ok(latestSensorReading);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SensorReadingDTO>> getSensorReadingInRange(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        if (from == null) from = LocalDateTime.now().minusDays(24);
        if (to == null) to = LocalDateTime.now();
        List<SensorReadingDTO> sensorReadingsInRange = sensorReadingService
                .getSensorReadingsInRange(sensorId, userDetails.getUsername(), from, to);
        return ResponseEntity.ok(sensorReadingsInRange);
    }

    @GetMapping
    public List<SensorReadingDTO> getAllReadingsForSensor(
            @RequestParam Long sensorId,
            @AuthenticationPrincipal  UserDetails userDetails) {
        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        LocalDateTime to = LocalDateTime.now();
        return sensorReadingService.getSensorReadingsInRange(sensorId, userDetails.getUsername(), from, to);
    }
}

