package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.WateringLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WateringLogRepository extends JpaRepository<WateringLog,Long> {
}
