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

    private final MqttClient mqttClient;
    private final WateringService wateringService;
    private final ObjectMapper objectMapper;
    private final Logger LOGGER = LoggerFactory.getLogger(MqttWateringSubscriber.class);

    public MqttWateringSubscriber(MqttClient mqttClient, WateringService wateringService, ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.wateringService = wateringService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe() throws MqttException {
        LOGGER.info("MQTT Watering Subscriber initialized");
        mqttClient.subscribe("greenhouse/+/watering/+", (topic, message) -> {
            String payload = new String(message.getPayload());

            WateringEventDTO dto = objectMapper.readValue(payload, WateringEventDTO.class);
            wateringService.waterGreenhouse(dto.getGreenhouseId(), dto.getEmail(), dto.getAmount(), dto.getSource());
        });
    }
}
