package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.WateringLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {

    Page<WateringLog> findByGreenhouseId(Long greenhouseId, Pageable pageable);

    Page<WateringLog> findAll(Pageable pageable);
}
