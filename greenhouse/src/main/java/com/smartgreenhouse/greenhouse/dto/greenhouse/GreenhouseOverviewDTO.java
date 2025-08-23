package com.smartgreenhouse.greenhouse.dto.greenhouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GreenhouseOverviewDTO {
    private Long id;
    private String name;
    private Double currentTemperature;
    private Double currentMoisture;
    private Boolean autoWateringEnabled;
    private Long activeSensorCount;
}
