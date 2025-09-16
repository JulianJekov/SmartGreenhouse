package com.smartgreenhouse.greenhouse.util.sensorReadingMapper;

import com.smartgreenhouse.greenhouse.dto.sensorReading.CreateSensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.SensorReadingDTO;
import com.smartgreenhouse.greenhouse.dto.sensorReading.TimeSeriesPointDTO;
import com.smartgreenhouse.greenhouse.entity.Sensor;
import com.smartgreenhouse.greenhouse.entity.SensorReading;
import org.springframework.stereotype.Component;

@Component
public class SensorReadingMapper {

    public SensorReadingDTO toDto(SensorReading sensorReading){
        SensorReadingDTO sensorReadingDTO = new SensorReadingDTO();
        sensorReadingDTO.setId(sensorReading.getId());
        sensorReadingDTO.setSensorId(sensorReading.getSensor().getId());
        sensorReadingDTO.setTimestamp(sensorReading.getTimestamp());
        sensorReadingDTO.setValue(sensorReading.getValue());
        sensorReadingDTO.setSensorUnit(sensorReading.getSensor().getUnit());
        sensorReadingDTO.setSensorType(sensorReading.getSensor().getSensorType());
        sensorReadingDTO.setGreenhouseId(sensorReading.getSensor().getGreenhouse().getId());
        sensorReadingDTO.setGreenhouseName(sensorReading.getSensor().getGreenhouse().getName());
        return sensorReadingDTO;
    }

    public SensorReading toEntity(CreateSensorReadingDTO createSensorReadingDTO, Sensor sensor) {
        SensorReading sensorReading = new SensorReading();
        sensorReading.setSensor(sensor);
        sensorReading.setValue(createSensorReadingDTO.getValue());
        return sensorReading;
    }

    public TimeSeriesPointDTO toTimeSeriesPointDTO(SensorReading sensorReading){
        TimeSeriesPointDTO timeSeriesPointDTO = new TimeSeriesPointDTO();
        timeSeriesPointDTO.setTimestamp(sensorReading.getTimestamp());
        timeSeriesPointDTO.setValue(sensorReading.getValue());
        return timeSeriesPointDTO;
    }
}
