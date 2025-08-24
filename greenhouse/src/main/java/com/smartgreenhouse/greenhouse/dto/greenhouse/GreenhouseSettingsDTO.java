package com.smartgreenhouse.greenhouse.dto.greenhouse;

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
public class GreenhouseSettingsDTO {
    @Positive(message = "Moisture threshold must be positive")
    private Double moistureThreshold;

    @Positive(message = "Water amount must be positive")
    private Double defaultWaterAmount;

    @NotNull(message = "Auto watering flag is required")
    private Boolean autoWateringEnabled;

//    @NotNull(message = "Notifications flag is required")
//    private Boolean notificationEnabled; //For later when we implement notifications
}
