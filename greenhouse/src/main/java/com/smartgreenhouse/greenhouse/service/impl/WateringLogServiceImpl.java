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
    public WateringLogDTO createWateringLog(CreateWateringLogDTO dto, String email) {
        Long greenhouseId = dto.getGreenhouseId();
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(greenhouseId, email);
        WateringLog wateringLog = wateringLogMapper.toEntity(dto, greenhouse);
        WateringLog saved = wateringLogRepository.save(wateringLog);
        return wateringLogMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<WateringLogDTO> getWateringLogByGreenhouseId(Long greenhouseId, String email, int page, int size) {
        getGreenhouseByIdAndUserOrThrow(greenhouseId, email);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return wateringLogRepository.findByGreenhouseId(greenhouseId, pageable)
                .map(wateringLogMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<WateringLogDTO> getAllWateringLogs(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return wateringLogRepository.findAllByGreenhouseUserEmail(email, pageable)
                .map(wateringLogMapper::toDto);
    }

    private Greenhouse getGreenhouseByIdAndUserOrThrow(Long id, String email) {
        return greenhouseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ObjectNotFoundException("Resource not found"));
    }
}
