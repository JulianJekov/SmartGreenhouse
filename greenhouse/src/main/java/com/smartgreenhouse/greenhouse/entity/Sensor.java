package com.smartgreenhouse.greenhouse.entity;

import com.smartgreenhouse.greenhouse.enums.SensorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorType sensorType;

    @Column(nullable = false, length = 10)
    private String unit;

    @Column(nullable = false)
    private Double minThreshold;

    @Column(nullable = false)
    private Double maxThreshold;

    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "greenhouse_id",  nullable = false)
    private Greenhouse greenhouse;
}
