package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;

import java.util.List;

public interface WateringLogService {
    WateringLogDTO createWateringLog(CreateWateringLogDTO dto);

    List<WateringLogDTO> getWateringLogByGreenhouseId(Long id);

    List<WateringLogDTO> getAllWateringLogs();


}
