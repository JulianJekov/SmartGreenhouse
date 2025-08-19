package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.exceptions.WateringFailedException;
import com.smartgreenhouse.greenhouse.service.WateringService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watering")
public class WateringController {

    private final WateringService wateringService;

    public WateringController(WateringService wateringService) {
        this.wateringService = wateringService;
    }

    @PostMapping("/manual")
    public ResponseEntity<String> manualWatering(@RequestParam Long greenhouseId, @RequestParam Double amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Water amount parameter is required");
        }
        try {
            wateringService.waterGreenhouse(greenhouseId, amount, WateringSource.MANUAL);
            return ResponseEntity.ok().build();
        } catch (WateringFailedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
