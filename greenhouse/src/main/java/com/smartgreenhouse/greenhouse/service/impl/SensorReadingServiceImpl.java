package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.SensorReadingRepository;
import com.smartgreenhouse.greenhouse.repository.SensorRepository;
import com.smartgreenhouse.greenhouse.service.SensorReadingService;
import com.smartgreenhouse.greenhouse.util.sensorReadingMapper.SensorReadingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SensorReadingServiceImpl implements SensorReadingService {

    private final SensorReadingRepository sensorReadingRepository;
    private final SensorRepository sensorRepository;
    private final SensorReadingMapper sensorReadingMapper;

    public SensorReadingServiceImpl(SensorReadingRepository sensorReadingRepository, SensorRepository sensorRepository, SensorReadingMapper sensorReadingMapper) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.sensorRepository = sensorRepository;
        this.sensorReadingMapper = sensorReadingMapper;
    }

    @Transactional
    @Override
    public SensorReadingDTO createSensorReading(CreateSensorReadingDTO createSensorReadingDTO) {
        Long sensorId = createSensorReadingDTO.getSensorId();
        Sensor sensor = sensorRepository.findById(sensorId)
                .orElseThrow(() -> new ObjectNotFoundException("Sensor not found with ID " + sensorId));
        SensorReading sensorReading = sensorReadingMapper.toEntity(createSensorReadingDTO, sensor);
        SensorReading saved = sensorReadingRepository.save(sensorReading);

        return sensorReadingMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public SensorReadingDTO getLatestSensorReading(Long sensorId) {
        Optional<SensorReading> sensorReading = sensorReadingRepository
                .findTopBySensorIdOrderByTimestampDesc(sensorId);
        if (sensorReading.isEmpty()) {
            throw new ObjectNotFoundException("Sensor Reading not found");
        }
        return sensorReadingMapper.toDto(sensorReading.get());
    }

    @Transactional(readOnly = true)
    @Override
    public List<SensorReadingDTO> getSensorReadingsInRange(Long sensorId, LocalDateTime from, LocalDateTime to) {
        return sensorReadingRepository.findAllBySensorIdAndTimestampBetweenOrderByTimestampAsc(sensorId, from, to)
                .stream()
                .map(sensorReadingMapper::toDto)
                .toList();
    }
}
