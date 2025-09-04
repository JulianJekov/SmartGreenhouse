package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorReadingService {

    SensorReadingDTO createSensorReading(CreateSensorReadingDTO createSensorReadingDTO, String email);

    SensorReadingDTO getLatestSensorReading(Long sensorId, String email);

    List<SensorReadingDTO> getSensorReadingsInRange(Long sensorId, String email, LocalDateTime from, LocalDateTime to);

}
