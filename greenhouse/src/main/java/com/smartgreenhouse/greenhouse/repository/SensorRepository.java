package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor,Long> {
    List<Sensor> findByGreenhouseId(Long greenhouseId);

    Optional<Sensor> findByIdAndGreenhouseUserEmail(Long id, String email);

    List<Sensor> findAllByGreenhouseUserEmail(String email);

    boolean existsByIdAndGreenhouseUserEmail(Long id, String email);
}
