package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.greenhouse.*;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import com.smartgreenhouse.greenhouse.entity.User;
import com.smartgreenhouse.greenhouse.enums.SensorType;
import com.smartgreenhouse.greenhouse.exceptions.NameAlreadyExistsException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.repository.SensorReadingRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.service.UserService;
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
    private final UserService userService;

    public GreenhouseServiceImpl(GreenhouseRepository greenhouseRepository,
                                 GreenhouseMapper greenhouseMapper,
                                 SensorMapper sensorMapper,
                                 SensorReadingRepository sensorReadingRepository,
                                 UserService userService) {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
        this.sensorMapper = sensorMapper;
        this.sensorReadingRepository = sensorReadingRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    @Override
    public GreenhouseBasicDTO getGreenhouseById(Long id, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        return greenhouseMapper.toBasicDTO(greenhouse);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GreenhouseBasicDTO> getUserGreenhousesBasic(String email) {
        return greenhouseRepository.findAllByUserEmail(email)
                .stream()
                .map(greenhouseMapper::toBasicDTO)
                .toList();
    }

    @Transactional
    @Override
    public GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto, String email) {
        User user = userService.getUserByEmail(email);
        throwIfDuplicatedNames(dto.getName(), email);
        Greenhouse greenhouse = greenhouseMapper.toEntity(dto);
        greenhouse.setUser(user);
        greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toDto(greenhouse);
    }

    @Transactional
    @Override
    public GreenhouseBasicDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        throwIfDuplicatedNames(dto.getName(), id, email);
        greenhouseMapper.updateEntity(dto, greenhouse);
        Greenhouse updated = greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toBasicDTO(updated);
    }

    @Transactional
    @Override
    public void deleteGreenhouse(Long id, String email) {
        if (!greenhouseRepository.existsByIdAndUserEmail(id, email)) {
            throw new ObjectNotFoundException("Resource not found");
        }
        greenhouseRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Optional<Sensor> findActiveMoistureSensor(Greenhouse greenhouse) {
        return greenhouse.getSensors().stream()
                .filter(s -> s.getSensorType() == SensorType.SOIL_MOISTURE && s.getIsActive())
                .findFirst();
    }

    @Transactional
    @Override
    public List<SensorDTO> getSensorsByGreenhouseId(Long id, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        return greenhouse.getSensors().stream()
                .map(sensorMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public List<GreenhouseOverviewDTO> getGreenhousesOverview(String email) {
        List<Greenhouse> allGreenhouses = greenhouseRepository.findAllByUserEmail(email);

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

    @Transactional(readOnly = true)
    @Override
    public GreenhouseSettingsDTO getSettings(Long id, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        return greenhouseMapper.toSettingsDto(greenhouse);
    }

    @Transactional
    @Override
    public GreenhouseSettingsDTO updateSettings(Long id, GreenhouseSettingsDTO settingsDTO, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        greenhouseMapper.updateSettings(settingsDTO, greenhouse);
        Greenhouse updated = greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toSettingsDto(updated);
    }

    @Transactional
    @Override
    public void toggleAutoWatering(Long id, String email) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(id, email);
        greenhouse.setAutoWateringEnabled(!greenhouse.getAutoWateringEnabled());
        greenhouseRepository.save(greenhouse);
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

    private Greenhouse getGreenhouseByIdAndUserOrThrow(Long id, String email) {
        return greenhouseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ObjectNotFoundException("Resource not found"));
    }


    private void throwIfDuplicatedNames(String name, Long id, String email) {
        if (greenhouseRepository.existsByNameAndIdNotAndUserEmail(name, id, email)) {
            throw new NameAlreadyExistsException("Greenhouse with this name: " + name + ", already exists");
        }
    }

    private void throwIfDuplicatedNames(String name, String email) {
        if (greenhouseRepository.existsByNameAndUserEmail(name, email)) {
            throw new NameAlreadyExistsException("Greenhouse with this name: " + name + ", already exists");
        }
    }
}
