package com.smartgreenhouse.greenhouse.dto.greenhouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGreenhouseDTO {
    private String name;
    private String location;
    private Integer capacity;
}
