package com.smartgreenhouse.greenhouse.dto.sensorReading;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPointDTO {

    @NotNull(message = "Timestamp is required")
    private Instant timestamp;

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    @DecimalMax(value = "100.0", message = "Value cannot exceed 100")
    private Double value;
}
