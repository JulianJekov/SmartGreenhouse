package com.smartgreenhouse.greenhouse.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartgreenhouse.greenhouse.exceptions.MqttPublishException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttWateringPublisher {

    private final Logger LOGGER = LoggerFactory.getLogger(MqttWateringPublisher.class);
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    public MqttWateringPublisher(MqttClient mqttClient,
                                 ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
    }

    public void publishWateringEvent(WateringEventDTO dto) {

        try {
            String wateringSource = dto.getSource().name().toLowerCase();
            Long greenhouseId = dto.getGreenhouseId();

            String topic = "greenhouse/" + greenhouseId + "/watering/" + wateringSource;
            String payload = objectMapper.writeValueAsString(dto);

            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));

            LOGGER.info("Published watering event to {}: {}", topic, payload);
        } catch (MqttException | JsonProcessingException e) {
            LOGGER.error("Failed to publish watering event", e);
            throw new MqttPublishException("Could not publish watering event to MQTT", e);
        }

    }
}
