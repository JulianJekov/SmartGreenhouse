package com.smartgreenhouse.greenhouse.service;


import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;

import java.util.List;

public interface GreenhouseService {
    GreenhouseDTO getGreenhouseById(Long id);
    List<GreenhouseDTO> getAllGreenhouses();
    GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto);
    GreenhouseDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto);
    void deleteGreenhouse(Long id);
}
