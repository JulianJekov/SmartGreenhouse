package com.smartgreenhouse.greenhouse.dto.sensor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SensorStatsDTO {
    private Double currentValue;
    private Double minValue;
    private Double maxValue;
    private Double averageValue;
    private Instant lastUpdate;
}
