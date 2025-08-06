package com.smartgreenhouse.greenhouse.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String name;

    private String location;

    private Integer capacity;

    @OneToMany(mappedBy =  "greenhouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WateringLog> wateringLogs;
}
