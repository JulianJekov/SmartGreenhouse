package com.smartgreenhouse.greenhouse.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttAutoWateringToggleSubscriber {

    private final Logger LOGGER = LoggerFactory.getLogger(MqttWateringSubscriber.class);
    private final MqttClient mqttClient;
    private final GreenhouseService greenhouseService;
    private final ObjectMapper objectMapper;

    public MqttAutoWateringToggleSubscriber(MqttClient mqttClient,
                                            GreenhouseService greenhouseService,
                                            ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.greenhouseService = greenhouseService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void subscribe(){
        try {
            LOGGER.info("MQTT ToggleAutoWatering Subscriber initialized");
            mqttClient.subscribe("greenhouse/+/auto-watering-toggle", (topic, message) -> {
                try {
                    String payload = new String(message.getPayload());
                    ToggleAutoWateringDTO dto = objectMapper.readValue(payload, ToggleAutoWateringDTO.class);
                    greenhouseService.toggleAutoWatering(dto.getId(), dto.getEmail());
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
