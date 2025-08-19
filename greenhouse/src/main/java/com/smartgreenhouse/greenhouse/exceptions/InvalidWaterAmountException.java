package com.smartgreenhouse.greenhouse.exceptions;

public class InvalidWaterAmountException extends RuntimeException {
    public InvalidWaterAmountException(String message) {
        super(message);
    }
}
