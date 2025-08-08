package com.smartgreenhouse.greenhouse.dto.wateringLog;

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
public class CreateWateringLogDTO {

    @NotNull(message = "Water amount is required")
    @Positive(message = "Water amount must be positive")
    private Double waterAmount;

    @NotNull(message = "Greenhouse ID is required")
    private Long greenhouseId;
}
