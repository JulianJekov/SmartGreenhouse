package com.smartgreenhouse.greenhouse.repository;

import com.smartgreenhouse.greenhouse.entity.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {
    Optional<SensorReading> findTopBySensorIdAndSensorGreenhouseUserEmailOrderByTimestampDesc(Long id, String email);

    List<SensorReading> findAllBySensorIdAndSensorGreenhouseUserEmailAndTimestampBetweenOrderByTimestampAsc
            (Long sensorId, String email, LocalDateTime from, LocalDateTime to);

    List<SensorReading> findAllBySensorIdOrderByTimestampAsc(Long sensorId);

    List<SensorReading> findTop10BySensorIdOrderByTimestampDesc(Long sensorId);

    @Query("""
            SELECT r
            FROM SensorReading r
            WHERE r.timestamp = (
            SELECT MAX(r2.timestamp)
            FROM SensorReading r2
            WHERE r2.sensor.id = r.sensor.id
            )
            """)
    List<SensorReading> findLatestReadingsForAllSensors();

    @Query("""
                SELECT r
                FROM SensorReading r
                WHERE r.sensor.id IN :sensorIds
                  AND r.timestamp = (
                      SELECT MAX(r2.timestamp)
                      FROM SensorReading r2
                      WHERE r2.sensor.id = r.sensor.id
                  )
            """)
    List<SensorReading> findLatestReadingsForSensors(@Param("sensorIds") List<Long> sensorIds);

    boolean existsBySensorIdAndSensorGreenhouseUserEmail(Long sensorId, String email);
}
