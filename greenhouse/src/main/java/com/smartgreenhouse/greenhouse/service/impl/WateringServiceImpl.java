package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.exceptions.AlreadyWateringException;
import com.smartgreenhouse.greenhouse.exceptions.InvalidWaterAmountException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WateringServiceImpl implements WateringService {

    private final Logger LOGGER = LoggerFactory.getLogger(WateringServiceImpl.class.getName());
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private final WateringLogRepository wateringLogRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final WateringLogMapper wateringLogMapper;
    private final SimulatedWateringActuator wateringActuator;
    private final Map<Long, Boolean> wateringLocks = new ConcurrentHashMap<>();
    private final NotificationService notificationService;

    public WateringServiceImpl(WateringLogRepository wateringLogRepository,
                               GreenhouseRepository greenhouseRepository,
                               WateringLogMapper wateringLogMapper,
                               SimulatedWateringActuator wateringActuator,
                               NotificationService notificationService) {
        this.wateringLogRepository = wateringLogRepository;
        this.greenhouseRepository = greenhouseRepository;
        this.wateringLogMapper = wateringLogMapper;
        this.wateringActuator = wateringActuator;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void waterGreenhouse(Long greenhouseId, String email, Double amount, WateringSource wateringSource) {
        Greenhouse greenhouse = getGreenhouseByIdAndUserOrThrow(greenhouseId, email);

        validateWaterAmount(amount);

        checkAndAcquireLock(greenhouseId);

        try {
            performWatering(greenhouse, amount, wateringSource);
        } finally {
            releaseLock(greenhouseId);
        }

    }

    private static void validateWaterAmount(Double amount) {
        if (amount == null) {
            throw new InvalidWaterAmountException("Water amount parameter is required");
        }
        if (amount <= 0) {
            throw new InvalidWaterAmountException("Water amount must be positive");
        }
    }

    private void checkAndAcquireLock(Long greenhouseId) {
        if (wateringLocks.getOrDefault(greenhouseId, false)) {
            throw new AlreadyWateringException("Greenhouse " + greenhouseId + " is already being watered.");
        }
        wateringLocks.put(greenhouseId, true);
    }

    private Greenhouse getGreenhouseByIdAndUserOrThrow(Long id, String email) {
        return greenhouseRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ObjectNotFoundException("Resource not found"));
    }

    private void performWatering(Greenhouse greenhouse, Double amount, WateringSource wateringSource) {
        boolean success = false;
        int attempts = 0;
        String errorDetails = null;
        while (!success && attempts < MAX_ATTEMPTS) {
            attempts++;
            success = wateringActuator.activateWatering(greenhouse.getId(), amount);

            if (!success && attempts < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    errorDetails = "Watering interrupted during retry - attempt " + attempts;
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        notificationService.sendWateringNotification(
                greenhouse.getId(), wateringSource, success, amount, attempts, errorDetails);
        if (success) {
            createWateringLog(greenhouse, amount, wateringSource);

            LOGGER.info("Successfully {} watered greenhouse with ID: {} from {} attempt",
                    wateringSource, greenhouse.getId(), attempts);
        } else {
            throw new WateringFailedException
                    ("Watering failed after " + attempts + " attempts for greenhouse " + greenhouse.getId());
        }
    }

    private void releaseLock(Long greenhouseId) {
        wateringLocks.put(greenhouseId, false);
    }

    private void createWateringLog(Greenhouse greenhouse, Double amount, WateringSource wateringSource) {
        CreateWateringLogDTO createWateringLogDTO = wateringLogMapper.toCreateDto(amount, greenhouse.getId(), wateringSource);
        WateringLog wateringLog = wateringLogMapper.toEntity(createWateringLogDTO, greenhouse);
        wateringLogRepository.save(wateringLog);
        LOGGER.info("Successfully created watering log with ID: {}", wateringLog.getId());
    }
}
