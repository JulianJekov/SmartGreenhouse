package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.simulation.SimulatedWateringActuator;
import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.exceptions.WateringFailedException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.repository.WateringLogRepository;
import com.smartgreenhouse.greenhouse.service.WateringService;
import com.smartgreenhouse.greenhouse.util.wateringLogMapper.WateringLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WateringServiceImpl implements WateringService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private final WateringLogRepository wateringLogRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final WateringLogMapper wateringLogMapper;
    private final SimulatedWateringActuator wateringActuator;
    private final Map<Long, Boolean> wateringLocks = new ConcurrentHashMap<>();

    public WateringServiceImpl(WateringLogRepository wateringLogRepository,
                               GreenhouseRepository greenhouseRepository,
                               WateringLogMapper wateringLogMapper,
                               SimulatedWateringActuator wateringActuator) {
        this.wateringLogRepository = wateringLogRepository;
        this.greenhouseRepository = greenhouseRepository;
        this.wateringLogMapper = wateringLogMapper;
        this.wateringActuator = wateringActuator;
    }

    @Override
    @Transactional
    public void waterGreenhouse(Long greenhouseId, Double amount, WateringSource wateringSource) {
        Greenhouse greenhouse = loadGreenhouseOrThrow(greenhouseId);
        checkAndAcquireLock(greenhouseId);
        try {
            performWatering(greenhouse, amount, wateringSource);

    private void checkAndAcquireLock(Long greenhouseId) {
        if (wateringLocks.getOrDefault(greenhouseId, false)) {
            throw new AlreadyWateringException("Greenhouse " + greenhouseId + " is already being watered.");
        }
        wateringLocks.put(greenhouseId, true);
    }

    private Greenhouse loadGreenhouseOrThrow(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + id));
    }

    private void performWatering(Greenhouse greenhouse, Double amount, WateringSource wateringSource) {
        boolean success = false;
        int attempts = 0;
        while (!success && attempts < MAX_ATTEMPTS) {
            attempts++;
            success = wateringActuator.activateWatering(greenhouse.getId(), amount);

            if (!success && attempts < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (success) {
            createWateringLog(greenhouse, amount, wateringSource);
        } else {
            throw new WateringFailedException("Watering failed");
            throw new WateringFailedException
                    ("Watering failed after " + attempts + " attempts for greenhouse " + greenhouse.getId());
        }
    }

    private void createWateringLog(Greenhouse greenhouse, Double amount, WateringSource wateringSource) {
        CreateWateringLogDTO createWateringLogDTO = new CreateWateringLogDTO();
        createWateringLogDTO.setWaterAmount(amount);
        createWateringLogDTO.setGreenhouseId(greenhouse.getId());
        createWateringLogDTO.setWateringSource(wateringSource);

        WateringLog wateringLog = wateringLogMapper.toEntity(createWateringLogDTO, greenhouse);
        wateringLogRepository.save(wateringLog);
    }
}
