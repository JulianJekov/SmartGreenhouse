package com.smartgreenhouse.greenhouse.dto.greenhouse;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGreenhouseDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location must be at most 100 characters")
    private String location;

    @Positive(message = "Capacity must be positive")
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
