package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.repository.WateringLogRepository;
import com.smartgreenhouse.greenhouse.service.WateringLogService;
import com.smartgreenhouse.greenhouse.util.wateringLogMapper.WateringLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WateringLogServiceImpl implements WateringLogService {

    private final WateringLogRepository wateringLogRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final WateringLogMapper wateringLogMapper;

    public WateringLogServiceImpl(WateringLogRepository wateringLogRepository, GreenhouseRepository greenhouseRepository, WateringLogMapper wateringLogMapper) {
        this.wateringLogRepository = wateringLogRepository;
        this.greenhouseRepository = greenhouseRepository;
        this.wateringLogMapper = wateringLogMapper;
    }

    @Transactional
    @Override
    public WateringLogDTO createWateringLog(CreateWateringLogDTO dto) {
        Greenhouse greenhouse = greenhouseRepository.findById(dto.getGreenhouseId())
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + dto.getGreenhouseId()));
        WateringLog wateringLog = wateringLogMapper.toEntity(dto, greenhouse);
        WateringLog saved = wateringLogRepository.save(wateringLog);
        return wateringLogMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<WateringLogDTO> getWateringLogByGreenhouseId(Long greenhouseId) {
        if (!greenhouseRepository.existsById(greenhouseId)) {
            throw new ObjectNotFoundException("Greenhouse not found with ID: " + greenhouseId);
        }

        return wateringLogRepository.findByGreenhouseId(greenhouseId)
                .stream()
                .map(wateringLogMapper::toDto)
                .toList();
    }

    //TODO: Change it to pagination
    @Transactional(readOnly = true)
    @Override
    public List<WateringLogDTO> getAllWateringLogs() {
        return wateringLogRepository.findAll()
                .stream()
                .map(wateringLogMapper::toDto)
                .toList();
    }
}
