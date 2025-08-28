package com.smartgreenhouse.greenhouse.util.greenhouseMapper;

import com.smartgreenhouse.greenhouse.dto.greenhouse.*;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.WateringLog;
import com.smartgreenhouse.greenhouse.util.sensorMapper.SensorMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GreenhouseMapper {

    public GreenhouseDTO toDto(Greenhouse greenhouse) {
        GreenhouseDTO dto = new GreenhouseDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setLocation(greenhouse.getLocation());
        dto.setCapacity(greenhouse.getCapacity());
        dto.setMoistureThreshold(greenhouse.getMoistureThreshold());
        dto.setDefaultWateringAmount(greenhouse.getDefaultWateringAmount());
        dto.setAutoWateringEnabled(greenhouse.getAutoWateringEnabled());

        if (greenhouse.getWateringLogs() != null) {
            List<WateringLogDTO> logs = greenhouse.getWateringLogs()
                    .stream()
                    .map(this::toWateringLogDto)
                    .toList();
            dto.setWateringLogs(logs);
        }

        if (greenhouse.getSensors() != null) {
            dto.setSensors(greenhouse.getSensors().stream()
                    .map(sensor -> new SensorMapper().toDto(sensor))
                    .toList());
        }
        return dto;
    }

    public Greenhouse toEntity(CreateGreenhouseDTO dto) {
        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
        greenhouse.setMoistureThreshold(dto.getMoistureThreshold());
        greenhouse.setDefaultWateringAmount(dto.getDefaultWateringAmount());
        greenhouse.setAutoWateringEnabled(dto.getAutoWateringEnabled());
        return greenhouse;
    }

    public void updateEntity(UpdateGreenhouseDTO dto, Greenhouse greenhouse) {
        greenhouse.setName(dto.getName());
        greenhouse.setLocation(dto.getLocation());
        greenhouse.setCapacity(dto.getCapacity());
        greenhouse.setMoistureThreshold(dto.getMoistureThreshold());
        greenhouse.setDefaultWateringAmount(dto.getDefaultWateringAmount());
        greenhouse.setAutoWateringEnabled(dto.getAutoWateringEnabled());
    }

    public GreenhouseOverviewDTO toOverviewDto(Greenhouse greenhouse) {
        GreenhouseOverviewDTO dto = new GreenhouseOverviewDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setAutoWateringEnabled(greenhouse.getAutoWateringEnabled());
        return dto;
    }

    public GreenhouseSettingsDTO toSettingsDto(Greenhouse greenhouse) {
        GreenhouseSettingsDTO dto = new GreenhouseSettingsDTO();
        dto.setMoistureThreshold(greenhouse.getMoistureThreshold());
        dto.setDefaultWaterAmount(greenhouse.getDefaultWateringAmount());
        dto.setAutoWateringEnabled(greenhouse.getAutoWateringEnabled());
        return dto;
    }

    public void updateSettings(GreenhouseSettingsDTO greenhouseSettingsDTO, Greenhouse greenhouse) {
        greenhouse.setMoistureThreshold(greenhouseSettingsDTO.getMoistureThreshold());
        greenhouse.setDefaultWateringAmount(greenhouseSettingsDTO.getDefaultWaterAmount());
        greenhouse.setAutoWateringEnabled(greenhouseSettingsDTO.getAutoWateringEnabled());
    }

    private WateringLogDTO toWateringLogDto(WateringLog wateringLog) {
        WateringLogDTO dto = new WateringLogDTO();
        dto.setId(wateringLog.getId());
        dto.setTimestamp(wateringLog.getTimestamp());
        dto.setWaterAmount(wateringLog.getWaterAmount());
        dto.setGreenhouseId(wateringLog.getGreenhouse().getId());
        dto.setGreenhouseName(wateringLog.getGreenhouse().getName());
        dto.setWateringSource(wateringLog.getWateringSource());
        return dto;
    }
}
