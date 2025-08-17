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

@Service
public class WateringServiceImpl implements WateringService {

    private final WateringLogRepository wateringLogRepository;
    private final GreenhouseRepository greenhouseRepository;
    private final WateringLogMapper wateringLogMapper;
    private final SimulatedWateringActuator wateringActuator;

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

        boolean success = wateringActuator.activateWatering(greenhouseId, amount);

        if (success) {
    private Greenhouse loadGreenhouseOrThrow(Long id) {
        return greenhouseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + id));
    }

            createWateringLog(greenhouse, amount, wateringSource);
        } else {
            throw new WateringFailedException("Watering failed");
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
