package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorStatsDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;
import com.smartgreenhouse.greenhouse.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<SensorStatsDTO> getSensorStats(@PathVariable Long id){
        SensorStatsDTO sensorStats = sensorService.getSensorStats(id);
        return ResponseEntity.ok(sensorStats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable Long id) {
        SensorDTO sensorById = sensorService.getSensorById(id);
        return ResponseEntity.ok(sensorById);
    }

    @GetMapping
    public ResponseEntity<List<SensorDTO>> getAllSensors() {
        List<SensorDTO> allSensors = sensorService.getAllSensors();
        return ResponseEntity.ok(allSensors);
    }

    @PostMapping
    public ResponseEntity<SensorDTO> createSensor(@RequestBody @Valid CreateSensorDTO createSensorDTO) {
        SensorDTO createdSensor = sensorService.createSensor(createSensorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSensor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorDTO> updateSensor(@PathVariable Long id,
                                                  @RequestBody @Valid UpdateSensorDTO updateSensorDTO) {
        SensorDTO updatedSensor = sensorService.updateSensor(id, updateSensorDTO);
        return ResponseEntity.ok(updatedSensor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
