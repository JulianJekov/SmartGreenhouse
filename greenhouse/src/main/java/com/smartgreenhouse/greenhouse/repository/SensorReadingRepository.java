package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    Optional<SensorReading> findTopBySensorIdOrderByTimestampDesc(Long sensorId);

    List<SensorReading> findAllBySensorIdAndTimestampBetweenOrderByTimestampAsc
            (Long sensorId, LocalDateTime from, LocalDateTime to);

    List<SensorReading> findAllBySensorIdOrderByTimestampAsc(Long sensorId);
}
