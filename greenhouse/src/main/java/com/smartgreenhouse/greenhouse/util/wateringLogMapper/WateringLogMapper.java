package com.smartgreenhouse.greenhouse.util.wateringLogMapper;

import com.smartgreenhouse.greenhouse.dto.wateringLog.CreateWateringLogDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import org.springframework.stereotype.Component;

@Component
public class WateringLogMapper {

    public WateringLogDTO toDto(WateringLog wateringLog) {
        WateringLogDTO dto = new WateringLogDTO();
        dto.setId(wateringLog.getId());
        dto.setTimestamp(wateringLog.getTimestamp());
        dto.setWaterAmount(wateringLog.getWaterAmount());
        dto.setGreenhouseId(wateringLog.getGreenhouse().getId());
        return dto;
    }

    public WateringLog toEntity(CreateWateringLogDTO dto, Greenhouse greenhouse) {
        WateringLog wateringLog = new WateringLog();
        wateringLog.setWaterAmount(dto.getWaterAmount());
        wateringLog.setGreenhouse(greenhouse);
        return wateringLog;
    }

}
