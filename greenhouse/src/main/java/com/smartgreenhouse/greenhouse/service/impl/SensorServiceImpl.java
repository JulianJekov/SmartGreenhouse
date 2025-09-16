package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.sensor.CreateSensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorStatsDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.UpdateSensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.repository.SensorReadingRepository;
import com.smartgreenhouse.greenhouse.repository.SensorRepository;
import com.smartgreenhouse.greenhouse.service.SensorService;
import com.smartgreenhouse.greenhouse.simulation.SimulatedSensorReader;
import com.smartgreenhouse.greenhouse.util.sensorMapper.SensorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final SensorMapper sensorMapper;
    private final SimulatedSensorReader simulatedSensorReader;
    private final SensorReadingRepository sensorReadingRepository;

    public SensorServiceImpl(SensorRepository sensorRepository,
                             GreenhouseRepository greenhouseRepository,
                             SensorMapper sensorMapper,
                             SimulatedSensorReader simulatedSensorReader,
                             SensorReadingRepository sensorReadingRepository) {
        this.sensorRepository = sensorRepository;
        this.greenhouseRepository = greenhouseRepository;
        this.sensorMapper = sensorMapper;
        this.simulatedSensorReader = simulatedSensorReader;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SensorDTO getSensorById(Long id, String email) {
        Sensor sensor = getSensorByIdAndUserOrThrow(id, email);
        return sensorMapper.toDto(sensor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorDTO> getAllSensors(String email) {
        return sensorRepository.findAllByGreenhouseUserEmail(email)
                .stream()
                .map(sensorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SensorDTO createSensor(CreateSensorDTO dto, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(dto.getGreenhouseId(), email);
        Sensor sensor = sensorMapper.toEntity(dto, greenhouse);
        Sensor saved = sensorRepository.save(sensor);
        return sensorMapper.toDto(saved);
    }

    @Override
    @Transactional
    public SensorDTO updateSensor(Long id, UpdateSensorDTO dto, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(dto.getGreenhouseId(), email);
        Sensor sensor = getSensorByIdAndUserOrThrow(id, email);
        sensorMapper.updateEntity(dto, sensor, greenhouse);
        Sensor updated = sensorRepository.save(sensor);
        return sensorMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteSensor(Long id, String email) {
        if (!sensorRepository.existsByIdAndGreenhouseUserEmail(id, email)) {
            throw new ObjectNotFoundException("Resource not found");
        }
        sensorRepository.deleteById(id);
    }

    @Override
    public SensorStatsDTO getSensorStats(Long sensorId, String email) {
        Sensor sensor = getSensorByIdAndUserOrThrow(sensorId, email);
        Double currentValue = simulatedSensorReader.readValue(sensor);
        List<SensorReading> lastReadings = sensorReadingRepository.findTop10BySensorIdOrderByTimestampDesc(sensorId);
        Double minValue = lastReadings.stream().mapToDouble(SensorReading::getValue).min().orElse(0.0);
        Double maxValue = lastReadings.stream().mapToDouble(SensorReading::getValue).max().orElse(0.0);
        Double averageValue = lastReadings.stream().mapToDouble(SensorReading::getValue).average().orElse(0.0);
        Instant lastUpdate = lastReadings.isEmpty() ? null : lastReadings.get(0).getTimestamp();
        return new SensorStatsDTO(currentValue, minValue, maxValue, averageValue, lastUpdate);
    }

    private Sensor getSensorByIdAndUserOrThrow(Long id, String email) {
        return sensorRepository.findByIdAndGreenhouseUserEmail(id, email).orElseThrow(() ->
                new ObjectNotFoundException("Resource not found"));
    }

    private Greenhouse getGreenhouseByIdAndUserOrThrow(Long greenhouseId, String email) {
        return greenhouseRepository.findByIdAndUserEmail(greenhouseId, email)
                .orElseThrow(() -> new ObjectNotFoundException("Resource not found"));
    }
}
