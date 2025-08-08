package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.service.WateringLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watering-logs")
public class WateringLogController {

    private final WateringLogService wateringLogService;

    public WateringLogController(WateringLogService wateringLogService) {
        this.wateringLogService = wateringLogService;
    }

    @PostMapping
    public ResponseEntity<WateringLogDTO> createWateringLog(@Valid @RequestBody CreateWateringLogDTO createWateringLogDTO) {
        WateringLogDTO wateringLog = wateringLogService.createWateringLog(createWateringLogDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(wateringLog);
    }

    @GetMapping
    public ResponseEntity<List<WateringLogDTO>> getAllWateringLogs() {
        List<WateringLogDTO> allWateringLogs = wateringLogService.getAllWateringLogs();
        return ResponseEntity.ok(allWateringLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<WateringLogDTO>> getWateringLogByGreenhouse(@PathVariable Long id) {
        List<WateringLogDTO> wateringLogByGreenhouseId = wateringLogService.getWateringLogByGreenhouseId(id);
        return ResponseEntity.ok(wateringLogByGreenhouseId);
    }
}
