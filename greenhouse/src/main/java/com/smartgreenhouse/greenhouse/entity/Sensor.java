package com.smartgreenhouse.greenhouse.entity;

import com.smartgreenhouse.greenhouse.enums.SensorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private SensorType sensorType;

    private String unit;

    private Double minThreshold;

    private Double maxThreshold;

    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "greenhouse_id",  nullable = false)
    private Greenhouse greenhouse;
}
