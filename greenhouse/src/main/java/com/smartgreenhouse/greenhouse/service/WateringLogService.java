package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import org.springframework.data.domain.Page;

public interface WateringLogService {
    WateringLogDTO createWateringLog(CreateWateringLogDTO dto);

    Page<WateringLogDTO> getWateringLogByGreenhouseId(Long id, int page, int size);

    Page<WateringLogDTO> getAllWateringLogs(int page, int size);


}
