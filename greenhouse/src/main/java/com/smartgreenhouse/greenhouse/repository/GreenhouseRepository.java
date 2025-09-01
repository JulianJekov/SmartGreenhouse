package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {

    @EntityGraph("Greenhouse.withSensors")
    List<Greenhouse> findByAutoWateringEnabledTrue();

    Optional<Greenhouse> findByIdAndUserEmail(Long id, String email);

    List<Greenhouse> findAllByUserEmail(String email);

    boolean existsByIdAndUserEmail(Long id, String email);

    boolean existsByNameAndUserEmail(String name, String email);

    boolean existsByNameAndIdNotAndUserEmail(String name, Long id, String email);
}
