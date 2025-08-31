package com.smartgreenhouse.greenhouse.dto.greenhouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseBasicDTO {
    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private Double moistureThreshold;
    private Double defaultWateringAmount;
    private Boolean autoWateringEnabled;
    private Integer activeSensorCount;
    private Integer wateringLogCount;
}
