package com.smartgreenhouse.greenhouse.dto.sensor;

import com.smartgreenhouse.greenhouse.enums.SensorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SensorDTO {
    private Long id;
    private SensorType sensorType;
    private String unit;
    private Double minThreshold;
    private Double maxThreshold;
    private Double currentValue;
    private Boolean isActive;
    private Long greenhouseId;
    private String greenhouseName;
}