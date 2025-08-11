package com.smartgreenhouse.greenhouse.util.sensorMapper;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import org.springframework.stereotype.Component;

@Component
public class SensorMapper {

    public SensorDTO toDto(Sensor sensor){
        SensorDTO dto = new SensorDTO();
        dto.setId(sensor.getId());
        dto.setSensorType(sensor.getSensorType());
        dto.setUnit(sensor.getUnit());
        dto.setMinThreshold(sensor.getMinThreshold());
        dto.setMaxThreshold(sensor.getMaxThreshold());
        dto.setCurrentValue(sensor.getCurrentValue());
        dto.setIsActive(sensor.getIsActive());
        dto.setGreenhouseId(sensor.getGreenhouse().getId());
        dto.setGreenhouseName(sensor.getGreenhouse().getName());
        return dto;
    }

    public Sensor toEntity(CreateSensorDTO dto, Greenhouse greenhouse){
        Sensor sensor = new Sensor();
        sensor.setSensorType(dto.getSensorType());
        sensor.setUnit(dto.getUnit());
        sensor.setMinThreshold(dto.getMinThreshold());
        sensor.setMaxThreshold(dto.getMaxThreshold());
        sensor.setIsActive(dto.getIsActive());
        sensor.setGreenhouse(greenhouse);
        return sensor;
    }

    public void updateEntity(UpdateSensorDTO dto, Sensor sensor, Greenhouse greenhouse) {
        sensor.setSensorType(dto.getSensorType());
        sensor.setUnit(dto.getUnit());
        sensor.setMinThreshold(dto.getMinThreshold());
        sensor.setMaxThreshold(dto.getMaxThreshold());
        sensor.setIsActive(dto.getIsActive());
        sensor.setGreenhouse(greenhouse); // If we want to change greenhouse later
    }
}
