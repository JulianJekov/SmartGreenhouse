package com.smartgreenhouse.greenhouse.service;


import com.smartgreenhouse.greenhouse.dto.greenhouse.*;
import com.smartgreenhouse.greenhouse.dto.sensor.SensorDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.entity.Sensor;

import java.util.List;
import java.util.Optional;

public interface GreenhouseService {
    GreenhouseBasicDTO getGreenhouseById(Long id, String email);

    GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto, String email);

    GreenhouseBasicDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto, String email);

    void deleteGreenhouse(Long id, String email);

    Optional<Sensor> findActiveMoistureSensor(Greenhouse greenhouse);

    List<SensorDTO> getSensorsByGreenhouseId(Long id, String email);

    List<GreenhouseOverviewDTO> getGreenhousesOverview(String email);

    GreenhouseSettingsDTO getSettings(Long id, String email);

    GreenhouseSettingsDTO updateSettings(Long id, GreenhouseSettingsDTO settingsDTO, String email);

    void toggleAutoWatering(Long id, String email);

    List<GreenhouseBasicDTO> getUserGreenhousesBasic(String email);
}
