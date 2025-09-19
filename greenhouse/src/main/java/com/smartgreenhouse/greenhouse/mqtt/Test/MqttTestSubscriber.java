package com.smartgreenhouse.greenhouse.mqtt.Test;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

@Service
public class MqttTestSubscriber {

    private final MqttClient mqttClient;

    public MqttTestSubscriber(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @PostConstruct
    public void subscribe() throws MqttException {
        mqttClient.subscribe("greenhouse/1/sensors/temp", (topic, message) -> {
            String payload = new String(message.getPayload());
            System.out.println("📥 Received from " + topic + ": " + payload);
        });
        System.out.println("✅ MQTT Subscriber initialized");
    }
}
