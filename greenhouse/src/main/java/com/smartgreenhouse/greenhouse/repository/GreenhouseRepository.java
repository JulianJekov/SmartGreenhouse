package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreenhouseRepository extends JpaRepository<Greenhouse, Long> {
}
