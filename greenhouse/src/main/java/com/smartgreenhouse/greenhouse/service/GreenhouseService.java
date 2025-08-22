package com.smartgreenhouse.greenhouse.service;


import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseOverviewDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;

import java.util.List;
import java.util.Optional;

public interface GreenhouseService {
    GreenhouseDTO getGreenhouseById(Long id);

    List<GreenhouseDTO> getAllGreenhouses();

    GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto);

    GreenhouseDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto);

    void deleteGreenhouse(Long id);

    Optional<Sensor> findActiveMoistureSensor(Greenhouse greenhouse);

    List<SensorDTO> getSensorsByGreenhouseId(Long id);

    List<GreenhouseOverviewDTO> getGreenhousesOverview();
}
