package com.smartgreenhouse.greenhouse.dto.sensorReading;

import com.smartgreenhouse.greenhouse.enums.SensorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingDTO {
    private Long id;
    private Double value;
    private Instant timestamp;
    private Long sensorId;
    private SensorType sensorType;
    private String sensorUnit;
    private Long greenhouseId;
    private String greenhouseName;
}
