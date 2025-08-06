package com.smartgreenhouse.greenhouse.dto.wateringLog;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WateringLogDTO {
    private Long id;
    private LocalDateTime timestamp;
    private Double amount;
}
