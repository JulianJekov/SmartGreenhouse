package com.smartgreenhouse.greenhouse.controller;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/greenhouses")
public class GreenhouseController {

    private final GreenhouseService greenhouseService;

    public GreenhouseController(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
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
    public ResponseEntity<GreenhouseDTO> createGreenhouse(@RequestBody @Valid CreateGreenhouseDTO createGreenhouseDTO) {
        GreenhouseDTO createdGreenhouse = greenhouseService.createGreenhouse(createGreenhouseDTO);
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
