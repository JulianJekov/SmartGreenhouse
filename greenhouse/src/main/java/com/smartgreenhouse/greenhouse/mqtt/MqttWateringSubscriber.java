package com.smartgreenhouse.greenhouse.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgreenhouse.greenhouse.service.WateringService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttWateringSubscriber {

    private final Logger LOGGER = LoggerFactory.getLogger(MqttWateringSubscriber.class);
    private final MqttClient mqttClient;
    private final WateringService wateringService;
    private final ObjectMapper objectMapper;

    public MqttWateringSubscriber(MqttClient mqttClient,
                                  WateringService wateringService,
                                  ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.wateringService = wateringService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe() {
        try {
            LOGGER.info("MQTT Watering Subscriber initialized");
            mqttClient.subscribe("greenhouse/+/watering/+", (topic, message) -> {
                try {
                    String payload = new String(message.getPayload());
                    WateringEventDTO dto = objectMapper.readValue(payload, WateringEventDTO.class);
                    wateringService.waterGreenhouse(dto.getGreenhouseId(), dto.getEmail(), dto.getAmount(), dto.getSource());
                } catch (Exception e) {
                    LOGGER.error("Failed to process MQTT message from topic {}", topic, e);
                }
            });
        } catch (MqttException e) {
            LOGGER.error("Failed to subscribe to MQTT topics", e);
            throw new IllegalStateException("Cannot subscribe to MQTT topics", e);
        }
    }
}
