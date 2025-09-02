package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorStatsDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;

import java.util.List;

public interface SensorService {
    SensorDTO getSensorById(Long id, String email);

    List<SensorDTO> getAllSensors(String email);

    SensorDTO createSensor(CreateSensorDTO dto, String email);

    SensorDTO updateSensor(Long id, UpdateSensorDTO dto, String email);

    void deleteSensor(Long id, String email);

    SensorStatsDTO getSensorStats(Long sensorId, String email);
}
