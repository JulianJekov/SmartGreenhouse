package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.WateringLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WateringLogRepository extends JpaRepository<WateringLog, Long> {

    Page<WateringLog> findByGreenhouseId(Long greenhouseId, Pageable pageable);

    Page<WateringLog> findAll(Pageable pageable);

    Page<WateringLog> findAllByGreenhouseUserEmail(String email, Pageable pageable);
}
