package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseOverviewDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import com.smartgreenhouse.greenhouse.enums.SensorType;
import com.smartgreenhouse.greenhouse.exceptions.NameAlreadyExistsException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.repository.SensorReadingRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.util.greenhouseMapper.GreenhouseMapper;
import com.smartgreenhouse.greenhouse.util.sensorMapper.SensorMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GreenhouseServiceImpl implements GreenhouseService {

    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseMapper greenhouseMapper;
    private final SensorMapper sensorMapper;
    private final SensorReadingRepository sensorReadingRepository;

    public GreenhouseServiceImpl(GreenhouseRepository greenhouseRepository,
                                 GreenhouseMapper greenhouseMapper,
                                 SensorMapper sensorMapper, SensorReadingRepository sensorReadingRepository) {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.sensorMapper = sensorMapper;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public GreenhouseDTO getGreenhouseById(Long id) {
        Greenhouse greenhouse = getGreenhouseOrThrow(id);
        return greenhouseMapper.toDto(greenhouse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GreenhouseDTO> getAllGreenhouses() {
        return greenhouseRepository.findAll().stream()
                .map(greenhouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto) {
        throwIfDuplicatedNames(dto.getName());
        Greenhouse greenhouse = greenhouseMapper.toEntity(dto);
        greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toDto(greenhouse);
    }

    @Transactional
    @Override
    public GreenhouseDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto) {
        Greenhouse greenhouse = getGreenhouseOrThrow(id);
        throwIfDuplicatedNames(dto.getName());
        greenhouseMapper.updateEntity(dto, greenhouse);
        Greenhouse updated = greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toDto(updated);
    }

    @Transactional
    @Override
    public void deleteGreenhouse(Long id) {
        if (!greenhouseRepository.existsById(id)) {
            throw new ObjectNotFoundException("Greenhouse not found with ID: " + id);
        }
        greenhouseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Sensor> findActiveMoistureSensor(Greenhouse greenhouse) {
        return greenhouse.getSensors().stream()
                .filter(s -> s.getSensorType() == SensorType.SOIL_MOISTURE && s.getIsActive())
                .findFirst();
    }

    @Override
    public List<SensorDTO> getSensorsByGreenhouseId(Long id) {
        Greenhouse greenhouse = getGreenhouseOrThrow(id);
        return greenhouse.getSensors().stream()
                .map(sensorMapper::toDto)
                .toList();
    }

    @Override
    public List<GreenhouseOverviewDTO> getGreenhousesOverview() {
        //TODO: when add users change it find by user id
        List<Greenhouse> allGreenhouses = greenhouseRepository.findAll();

        List<Long> sensorIds = allGreenhouses.stream()
                .flatMap(g -> g.getSensors().stream())
                .map(Sensor::getId)
                .toList();

        List<SensorReading> latestReadings = sensorReadingRepository.findLatestReadingsForSensors(sensorIds);

        Map<Long, Double> latestValueMap = latestReadings.stream()
                .collect(Collectors.toMap(r -> r.getSensor().getId(), SensorReading::getValue));

        return allGreenhouses.stream()
                .map(g -> buildOverviewDTO(g, latestValueMap))
                .toList();
    }

    private GreenhouseOverviewDTO buildOverviewDTO(Greenhouse greenhouse, Map<Long, Double> latestValueMap) {
        Double currentTemperature = getLatestValue(greenhouse, SensorType.TEMPERATURE, latestValueMap);
        Double currentMoisture = getLatestValue(greenhouse, SensorType.SOIL_MOISTURE, latestValueMap);
        Long activeSensorCount = greenhouse.getSensors().stream().filter(Sensor::getIsActive).count();

        GreenhouseOverviewDTO overviewDto = greenhouseMapper.toOverviewDto(greenhouse);
        overviewDto.setCurrentTemperature(currentTemperature);
        overviewDto.setCurrentMoisture(currentMoisture);
        overviewDto.setActiveSensorCount(activeSensorCount);
        return overviewDto;
    }

    private Double getLatestValue(Greenhouse greenhouse, SensorType sensorType, Map<Long, Double> latestValueMap) {
        return greenhouse.getSensors().stream()
                .filter(s -> s.getSensorType().equals(sensorType) && s.getIsActive())
                .findFirst()
                .map(sensor -> latestValueMap.getOrDefault(sensor.getId(), null))
                .orElse(null);
    }

    private Greenhouse getGreenhouseOrThrow(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + id));
    }


    private void throwIfDuplicatedNames(String name) {
        if (greenhouseRepository.existsByName(name)) {
            throw new NameAlreadyExistsException("Greenhouse with this name: " + name + ", already exists");
        }
    }
}
