package com.smartgreenhouse.greenhouse.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttWateringPublisher {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper;

    public MqttWateringPublisher(MqttClient mqttClient, ObjectMapper objectMapper) {
        this.mqttClient = mqttClient;
        this.objectMapper = objectMapper;
    }

    public void publishWateringEvent(WateringEventDTO dto)
            throws MqttException, JsonProcessingException {

        String wateringSource = dto.getSource().name().toLowerCase();
        Long greenhouseId = dto.getGreenhouseId();

        String topic = "greenhouse/" + greenhouseId + "/watering/" + wateringSource;
        String payload = objectMapper.writeValueAsString(dto);

        mqttClient.publish(topic, new MqttMessage(payload.getBytes()));

        System.out.println("📤 Published watering event to " + topic + ": " + payload);
    }
}
