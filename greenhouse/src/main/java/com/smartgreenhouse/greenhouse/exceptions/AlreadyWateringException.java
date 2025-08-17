package com.smartgreenhouse.greenhouse.exceptions;

public class AlreadyWateringException extends RuntimeException {
    public AlreadyWateringException(String message) {
        super(message);
    }
}
