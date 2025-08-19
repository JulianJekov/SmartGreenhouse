package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.service.WateringService;
import com.smartgreenhouse.greenhouse.simulation.SimulatedSensorReader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutoWateringScheduler {

    private final GreenhouseRepository greenhouseRepository;
    private final WateringService wateringService;
    private final GreenhouseService greenhouseService;
    private final SimulatedSensorReader sensorReader;

    public AutoWateringScheduler(GreenhouseRepository greenhouseRepository,
                                 WateringService wateringService,
                                 GreenhouseService greenhouseService,
                                 SimulatedSensorReader sensorReader) {
        this.greenhouseRepository = greenhouseRepository;
        this.wateringService = wateringService;
        this.greenhouseService = greenhouseService;
        this.sensorReader = sensorReader;
    }

    @Transactional
    @Scheduled(fixedRate = 30000) // 5 minutes
    public void checkAndWater() {
        greenhouseRepository.findByAutoWateringEnabledTrue()
                .forEach(greenhouse -> {
                    greenhouseService.findActiveMoistureSensor(greenhouse)
                            .ifPresent(moistureSensor -> {
                                double moistureValue = sensorReader.readValue(moistureSensor);
                                if (moistureValue < greenhouse.getMoistureThreshold()) {
                                    wateringService.waterGreenhouse(
                                            greenhouse.getId(),
                                            greenhouse.getDefaultWateringAmount(),
                                            WateringSource.AUTO
                                    );
                                }
                            });
                });
    }
}
