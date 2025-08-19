package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {
    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByName(String name);

    @EntityGraph("Greenhouse.withSensors")
    List<Greenhouse> findByAutoWateringEnabledTrue();

}
