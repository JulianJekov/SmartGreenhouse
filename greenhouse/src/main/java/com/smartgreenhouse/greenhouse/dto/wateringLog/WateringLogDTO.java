package com.smartgreenhouse.greenhouse.dto.wateringLog;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WateringLogDTO {
    private Long id;
    private LocalDateTime timestamp;
    private Double waterAmount;
    private Long greenhouseId;
    private String greenhouseName;
    private WateringSource wateringSource;
}
