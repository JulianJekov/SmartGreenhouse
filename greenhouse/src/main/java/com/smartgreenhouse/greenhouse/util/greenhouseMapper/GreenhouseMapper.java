package com.smartgreenhouse.greenhouse.util.greenhouseMapper;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GreenhouseMapper {
    public GreenhouseDTO toDto(Greenhouse greenhouse){
        GreenhouseDTO dto = new GreenhouseDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setLocation(greenhouse.getLocation());
        dto.setCapacity(greenhouse.getCapacity());
        if (greenhouse.getWateringLogs() != null){
            List<WateringLogDTO> logs = greenhouse.getWateringLogs()
                    .stream()
                    .map(this::toWateringLogDto)
                    .toList();
            dto.setWateringLogs(logs);
        }
        return dto;
    }

    public Greenhouse toEntity(CreateGreenhouseDTO dto) {
        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
        return greenhouse;
    }

    public void updateEntity(UpdateGreenhouseDTO dto, Greenhouse greenhouse) {
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
    }

    private WateringLogDTO toWateringLogDto(WateringLog wateringLog) {
        WateringLogDTO dto = new WateringLogDTO();
        dto.setId(wateringLog.getId());
        dto.setTimestamp(wateringLog.getTimestamp());
        dto.setAmount(wateringLog.getWaterAmount());
        return dto;
    }
}
