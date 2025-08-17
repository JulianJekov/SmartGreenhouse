package com.smartgreenhouse.greenhouse.exceptions;

public class WateringFailedException extends RuntimeException {
    public WateringFailedException(String message) {
        super(message);
    }
    public  WateringFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
