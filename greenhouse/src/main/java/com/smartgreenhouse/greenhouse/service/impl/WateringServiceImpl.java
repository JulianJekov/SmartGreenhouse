package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.enums.WateringSource;
import com.smartgreenhouse.greenhouse.service.WateringService;
import org.springframework.stereotype.Service;

@Service
public class WateringServiceImpl implements WateringService {
    @Override
    public void waterGreenhouse(Long greenhouseId, double amount, WateringSource source) {

    }
}
