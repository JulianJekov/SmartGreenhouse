package com.smartgreenhouse.greenhouse.util.greenhouseMapper;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import org.springframework.stereotype.Component;

@Component
public class GreenhouseMapper {
    public GreenhouseDTO toDto(Greenhouse greenhouse){
        GreenhouseDTO dto = new GreenhouseDTO();
        dto.setId(greenhouse.getId());
        dto.setName(greenhouse.getName());
        dto.setLocation(greenhouse.getLocation());
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
}
