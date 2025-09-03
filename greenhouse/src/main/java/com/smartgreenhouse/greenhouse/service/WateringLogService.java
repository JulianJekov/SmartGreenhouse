package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import org.springframework.data.domain.Page;

public interface WateringLogService {
    WateringLogDTO createWateringLog(CreateWateringLogDTO dto, String email);

    Page<WateringLogDTO> getWateringLogByGreenhouseId(Long id, String email, int page, int size);

    Page<WateringLogDTO> getAllWateringLogs(String email, int page, int size);


}
