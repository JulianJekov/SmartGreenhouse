package com.smartgreenhouse.greenhouse.dto.greenhouse;

import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.dto.wateringLog.WateringLogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseDTO {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private Double moistureThreshold;
    private Double defaultWateringAmount;
    private Boolean autoWateringEnabled;
    private List<WateringLogDTO> wateringLogs;
    private List<SensorDTO> sensors;
}
