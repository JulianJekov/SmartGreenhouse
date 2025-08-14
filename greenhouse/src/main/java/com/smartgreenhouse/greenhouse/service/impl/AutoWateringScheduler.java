package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.service.WateringService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AutoWateringScheduler {

    private final GreenhouseRepository greenhouseRepository;
    private final WateringService wateringService;
    private final GreenhouseService greenhouseService;

    public AutoWateringScheduler(GreenhouseRepository greenhouseRepository, WateringService wateringService, GreenhouseService greenhouseService) {
        this.greenhouseRepository = greenhouseRepository;
        this.wateringService = wateringService;
        this.greenhouseService = greenhouseService;
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void checkAndWater() {
        greenhouseRepository.findByAutoWateringEnabledTrue().forEach(greenhouse -> {
            greenhouseService.findActiveMoistureSensor(greenhouse).ifPresent(moistureSensor -> {
                if (moistureSensor.getCurrentValue() < greenhouse.getMoistureThreshold()) {
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
