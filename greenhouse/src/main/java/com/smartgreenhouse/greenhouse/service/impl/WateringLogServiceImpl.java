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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Long greenhouseId = dto.getGreenhouseId();
        Greenhouse greenhouse = getGreenhouseOrThrow(greenhouseId);
        WateringLog wateringLog = wateringLogMapper.toEntity(dto, greenhouse);
        WateringLog saved = wateringLogRepository.save(wateringLog);
        return wateringLogMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<WateringLogDTO> getWateringLogByGreenhouseId(Long greenhouseId, int page, int size) {
        getGreenhouseOrThrow(greenhouseId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return wateringLogRepository.findByGreenhouseId(greenhouseId, pageable)
                .map(wateringLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<WateringLogDTO> getAllWateringLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return wateringLogRepository.findAll(pageable)
                .map(wateringLogMapper::toDto);
    }

    private Greenhouse getGreenhouseOrThrow(Long greenhouseId) {
        return greenhouseRepository.findById(greenhouseId)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + greenhouseId));
    }
}
