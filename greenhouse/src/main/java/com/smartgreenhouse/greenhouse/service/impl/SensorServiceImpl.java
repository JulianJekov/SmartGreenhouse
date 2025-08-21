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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final SensorMapper sensorMapper;
    private final SimulatedSensorReader simulatedSensorReader;
    private final SensorReadingRepository  sensorReadingRepository;

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
    public SensorDTO getSensorById(Long id) {
        Sensor sensor = getSensorOrThrow(id);
        return sensorMapper.toDto(sensor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorDTO> getAllSensors() {
        return sensorRepository.findAll()
                .stream()
                .map(sensorMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SensorDTO createSensor(CreateSensorDTO dto) {
        Greenhouse greenhouse = getGreenhouseOrThrow(dto.getGreenhouseId());
        Sensor sensor = sensorMapper.toEntity(dto, greenhouse);
        Sensor saved = sensorRepository.save(sensor);
        return sensorMapper.toDto(saved);
    }

    @Override
    @Transactional
    public SensorDTO updateSensor(Long id, UpdateSensorDTO dto) {
        Greenhouse greenhouse = getGreenhouseOrThrow(dto.getGreenhouseId());
        Sensor sensor = getSensorOrThrow(id);
        sensorMapper.updateEntity(dto, sensor, greenhouse);
        Sensor updated = sensorRepository.save(sensor);
        return sensorMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteSensor(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new ObjectNotFoundException("Sensor not found with ID: " + id);
        }
        sensorRepository.deleteById(id);
    }

    @Override
    public SensorStatsDTO getSensorStats(Long sensorId) {
        Sensor sensor = getSensorOrThrow(sensorId);
        Double currentValue = simulatedSensorReader.readValue(sensor);
        List<SensorReading> lastReadings = sensorReadingRepository.findTop10BySensorIdOrderByTimestamp(sensorId);
        Double minValue = lastReadings.stream().mapToDouble(SensorReading::getValue).min().orElse(0.0);
        Double maxValue = lastReadings.stream().mapToDouble(SensorReading::getValue).max().orElse(0.0);
        Double averageValue = lastReadings.stream().mapToDouble(SensorReading::getValue).average().orElse(0.0);
        LocalDateTime lastUpdate = lastReadings.isEmpty() ? null : lastReadings.get(0).getTimestamp();
        return new SensorStatsDTO(currentValue, minValue, maxValue, averageValue, lastUpdate);
    }

    private Sensor getSensorOrThrow(Long id) {
        return sensorRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Sensor not found with ID: " + id));
    }

    private Greenhouse getGreenhouseOrThrow(Long greenhouseId) {
        return greenhouseRepository.findById(greenhouseId)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + greenhouseId));
    }
}
