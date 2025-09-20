package com.smartgreenhouse.greenhouse.mqtt;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WateringEventDTO {
    private Long greenhouseId;
    private String email;
    private Double amount;
    private WateringSource source;
}

