package com.smartgreenhouse.greenhouse.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Greenhouse {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private Integer capacity;

    @OneToMany(mappedBy =  "greenhouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WateringLog> wateringLogs = new ArrayList<>();

    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Sensor> sensors = new ArrayList<>();
}
