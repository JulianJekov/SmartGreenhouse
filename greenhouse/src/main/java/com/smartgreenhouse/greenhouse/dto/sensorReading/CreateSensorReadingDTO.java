package com.smartgreenhouse.greenhouse.dto.sensorReading;

import jakarta.validation.constraints.NotNull;
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
    private Double value;

    @NotNull(message = "Sensor ID is required")
    private Long sensorId;
}
