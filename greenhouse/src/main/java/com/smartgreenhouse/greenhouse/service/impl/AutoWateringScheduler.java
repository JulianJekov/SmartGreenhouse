package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.mqtt.MqttWateringPublisher;
import com.smartgreenhouse.greenhouse.mqtt.WateringEventDTO;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.simulation.SimulatedSensorReader;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutoWateringScheduler {

    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseService greenhouseService;
    private final SimulatedSensorReader sensorReader;
    private final MqttWateringPublisher mqttWateringPublisher;

    public AutoWateringScheduler(GreenhouseRepository greenhouseRepository,
                                 GreenhouseService greenhouseService,
                                 SimulatedSensorReader sensorReader,
                                 MqttWateringPublisher mqttWateringPublisher) {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseService = greenhouseService;
        this.sensorReader = sensorReader;
        this.mqttWateringPublisher = mqttWateringPublisher;
    }

    @Transactional
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void checkAndWater() {
        greenhouseRepository.findByAutoWateringEnabledTrue()
                .forEach(greenhouse -> {
                    greenhouseService.findActiveMoistureSensor(greenhouse)
                            .ifPresent(moistureSensor -> {
                                double moistureValue = sensorReader.readValue(moistureSensor);
                                if (moistureValue < greenhouse.getMoistureThreshold()) {
                                    mqttWateringPublisher.publishWateringEvent(
                                            new WateringEventDTO(
                                                    greenhouse.getId(),
                                                    greenhouse.getUser().getEmail(),
                                                    greenhouse.getDefaultWateringAmount(),
                                                    WateringSource.AUTO
                                            )
                                    );
                                }
                            });
                });
    }
}
