package com.smartgreenhouse.greenhouse.service;

import com.smartgreenhouse.greenhouse.enums.WateringSource;

public interface WateringService {
    void waterGreenhouse(Long greenhouseId, double amount, WateringSource source);
}
