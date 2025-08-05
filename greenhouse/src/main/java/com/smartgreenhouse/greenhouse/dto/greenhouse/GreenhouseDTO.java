package com.smartgreenhouse.greenhouse.dto.greenhouse;

import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseDTO {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private List<WateringLogDTO> wateringLogs;
}
