package com.smartgreenhouse.greenhouse.exceptions;

public class WateringFailedException extends RuntimeException {
    public WateringFailedException(String message) {
        super(message);
    }
}
