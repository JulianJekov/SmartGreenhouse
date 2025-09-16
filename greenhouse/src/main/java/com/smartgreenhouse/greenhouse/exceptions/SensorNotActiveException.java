package com.smartgreenhouse.greenhouse.exceptions;

public class SensorNotActiveException extends RuntimeException {
    public SensorNotActiveException(String message) {
        super(message);
    }
}
