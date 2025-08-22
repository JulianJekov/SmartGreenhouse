package com.smartgreenhouse.greenhouse.util.greenhouseMapper;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseOverviewDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import com.smartgreenhouse.greenhouse.enums.SensorType;
import com.smartgreenhouse.greenhouse.repository.SensorReadingRepository;
import com.smartgreenhouse.greenhouse.simulation.SimulatedSensorReader;
import com.smartgreenhouse.greenhouse.util.sensorMapper.SensorMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GreenhouseMapper {
    private final SensorReadingRepository sensorReadingRepository;
    private final SimulatedSensorReader simulatedSensorReader;

    public GreenhouseMapper(SensorReadingRepository sensorReadingRepository, SimulatedSensorReader simulatedSensorReader) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.simulatedSensorReader = simulatedSensorReader;
    }

    public GreenhouseDTO toDto(Greenhouse greenhouse) {
        GreenhouseDTO dto = new GreenhouseDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setLocation(greenhouse.getLocation());
        dto.setCapacity(greenhouse.getCapacity());
        dto.setMoistureThreshold(greenhouse.getMoistureThreshold());
        dto.setDefaultWateringAmount(greenhouse.getDefaultWateringAmount());
        dto.setAutoWateringEnabled(greenhouse.getAutoWateringEnabled());

        if (greenhouse.getWateringLogs() != null) {
            List<WateringLogDTO> logs = greenhouse.getWateringLogs()
                    .stream()
                    .map(this::toWateringLogDto)
                    .toList();
            dto.setWateringLogs(logs);
        }

        if (greenhouse.getSensors() != null) {
            dto.setSensors(greenhouse.getSensors().stream()
                    .map(sensor -> new SensorMapper().toDto(sensor))
                    .toList());
        }
        return dto;
    }

    public Greenhouse toEntity(CreateGreenhouseDTO dto) {
        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
        greenhouse.setMoistureThreshold(dto.getMoistureThreshold());
        greenhouse.setDefaultWateringAmount(dto.getDefaultWateringAmount());
        greenhouse.setAutoWateringEnabled(dto.getAutoWateringEnabled());
        return greenhouse;
    }

    public void updateEntity(UpdateGreenhouseDTO dto, Greenhouse greenhouse) {
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
        greenhouse.setMoistureThreshold(dto.getMoistureThreshold());
        greenhouse.setDefaultWateringAmount(dto.getDefaultWateringAmount());
        greenhouse.setAutoWateringEnabled(dto.getAutoWateringEnabled());
    }

    public GreenhouseOverviewDTO toOverviewDto(Greenhouse greenhouse) {
        GreenhouseOverviewDTO dto = new GreenhouseOverviewDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setCurrentTemperature(getSensorValue(greenhouse, SensorType.TEMPERATURE));
        dto.setCurrentMoisture(getSensorValue(greenhouse, SensorType.SOIL_MOISTURE));
        dto.setAutoWateringEnabled(greenhouse.getAutoWateringEnabled());
        dto.setActiveSensorCount(countActiveSensors(greenhouse));
        return dto;
    }

    private int countActiveSensors(Greenhouse greenhouse) {
        return (int) greenhouse.getSensors().stream().filter(Sensor::getIsActive).count();
    }

    private Double getSensorValue(Greenhouse greenhouse, SensorType sensorType) {
        Optional<Sensor> optionalSensor = greenhouse.getSensors()
                .stream()
                .filter(s -> s.getSensorType().equals(sensorType) && s.getIsActive())
                .findFirst();
        if (optionalSensor.isEmpty()) {
            return null;
        }
        Sensor sensor = optionalSensor.get();

        Optional<SensorReading> sensorReading = sensorReadingRepository.findTopBySensorIdOrderByTimestampDesc(sensor.getId());
        return sensorReading.map(SensorReading::getValue).orElseGet(() -> simulatedSensorReader.readValue(sensor));
    }

    private WateringLogDTO toWateringLogDto(WateringLog wateringLog) {
        WateringLogDTO dto = new WateringLogDTO();
        dto.setId(wateringLog.getId());
        dto.setTimestamp(wateringLog.getTimestamp());
        dto.setWaterAmount(wateringLog.getWaterAmount());
        dto.setGreenhouseId(wateringLog.getGreenhouse().getId());
        return dto;
    }
}
