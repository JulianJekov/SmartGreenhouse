package com.smartgreenhouse.greenhouse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private LocalDateTime timestamp;

    private Double waterAmount;

    @ManyToOne
    @JoinColumn(name = "greenhouse_id")
    @JsonBackReference
    private Greenhouse greenhouse;

}
