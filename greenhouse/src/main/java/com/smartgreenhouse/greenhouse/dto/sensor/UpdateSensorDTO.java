package com.smartgreenhouse.greenhouse.dto.sensor;

import com.smartgreenhouse.greenhouse.enums.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSensorDTO {

    @NotNull(message = "Sensor type is required")
    private SensorType sensorType;

    @NotBlank(message = "Unit is required")
    @Size(max = 10, message = "Unit must be at most 10 characters")
    private String unit;

    @Positive(message = "Threshold can not be negative")
    @NotNull(message = "Minimum threshold is required")
    private Double minThreshold;

    @Positive(message = "Threshold can not be negative")
    @NotNull(message = "Maximum threshold is required")
    private Double maxThreshold;

    @NotNull(message = "Active flag is required")
    private Boolean isActive;

    private Long greenhouseId;
}
