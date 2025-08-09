package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.service.WateringLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<WateringLogDTO>> getAllWateringLogs(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        Page<WateringLogDTO> allWateringLogs = wateringLogService.getAllWateringLogs(page, size);
        return ResponseEntity.ok(allWateringLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Page<WateringLogDTO>> getWateringLogByGreenhouse(@PathVariable Long id,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        Page<WateringLogDTO> wateringLogByGreenhouseId = wateringLogService.getWateringLogByGreenhouseId(id, page, size);
        return ResponseEntity.ok(wateringLogByGreenhouseId);
    }
}
