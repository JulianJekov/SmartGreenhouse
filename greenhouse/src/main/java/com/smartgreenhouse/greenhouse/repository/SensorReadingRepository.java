package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreenhouseRepository extends JpaRepository<SensorReading, Long> {
}
