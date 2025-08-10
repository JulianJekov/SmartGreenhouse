package com.smartgreenhouse.greenhouse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.smartgreenhouse.greenhouse.enums.WateringSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
public class WateringLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double waterAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "greenhouse_id", nullable = false)
    @JsonBackReference
    private Greenhouse greenhouse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WateringSource wateringSource;

    @PrePersist
    private void prePersist(){
        if (timestamp == null)
            timestamp = LocalDateTime.now();
    }
}
