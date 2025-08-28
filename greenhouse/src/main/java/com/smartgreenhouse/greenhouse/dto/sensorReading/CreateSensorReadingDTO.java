package com.smartgreenhouse.greenhouse.dto.sensorReading;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSensorReadingDTO {

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private Double value;

    @NotNull(message = "Sensor ID is required")
    private Long sensorId;
}
