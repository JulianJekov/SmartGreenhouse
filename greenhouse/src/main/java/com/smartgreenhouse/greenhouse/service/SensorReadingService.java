package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;

import java.time.Instant;
import java.util.List;

public interface SensorReadingService {

    SensorReadingDTO createSensorReading(CreateSensorReadingDTO createSensorReadingDTO, String email);

    SensorReadingDTO getLatestSensorReading(Long sensorId, String email);

    List<SensorReadingDTO> getSensorReadingsInRange(Long sensorId, String email, Instant from, Instant to);

}
