package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;

import java.util.List;

public interface SensorService {
    SensorDTO getSensorById(Long id);

    List<SensorDTO> getAllSensors();

    SensorDTO createSensor(CreateSensorDTO dto);

    SensorDTO updateSensor(Long id, UpdateSensorDTO dto);

    void deleteSensor(Long id);
}
