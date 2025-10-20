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
public class MqttAutoWateringTogglePublisher {

    private final Logger LOGGER = LoggerFactory.getLogger(MqttAutoWateringTogglePublisher.class);
    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    public MqttAutoWateringTogglePublisher(MqttClient mqttClient,
                                           ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
    }

    public void publishToggleAutoWateringEvent(ToggleAutoWateringDTO dto) {

        try {
            Long greenhouseId = dto.getId();
            String topic = "greenhouse/" + greenhouseId + "/auto-watering-toggle";
            String payload = objectMapper.writeValueAsString(dto);

            mqttClient.publish(topic, new MqttMessage(payload.getBytes()));
            LOGGER.info("Published toggle auto watering event to {}: {}", topic, payload);
        } catch (MqttException | JsonProcessingException e) {
            LOGGER.error("Failed to publish toggle-auto-watering event", e);
            throw new MqttPublishException("Could not publish toggle-auto-watering event to MQTT", e);
        }
    }
}
