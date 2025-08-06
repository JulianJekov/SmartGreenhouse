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
public class UpdateSensorDTO {

    private SensorType type;

    private String unit;

    private Double minThreshold;

    private Double maxThreshold;

    private boolean isActive;

    private Long greenhouseId;
}
