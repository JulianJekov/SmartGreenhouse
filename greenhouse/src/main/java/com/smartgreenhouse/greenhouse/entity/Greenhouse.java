package com.smartgreenhouse.greenhouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Greenhouse {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    @OneToMany(mappedBy =  "greenhouse", cascade = CascadeType.ALL)
    private List<WateringLog> wateringLogs;
}
