package com.smartgreenhouse.greenhouse.simulation;

import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.exceptions.SensorNotActiveException;
import org.springframework.stereotype.Component;

@Component
public class SimulatedSensorReader {

    public double readValue(Sensor sensor) {
        if (!sensor.getIsActive()) {
            throw new SensorNotActiveException("Sensor is not active.");
        }
        return sensor.getMinThreshold() + Math.random() * (sensor.getMaxThreshold() - sensor.getMinThreshold());
    }
}
