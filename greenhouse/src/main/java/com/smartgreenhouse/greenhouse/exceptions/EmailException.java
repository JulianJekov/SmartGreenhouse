package com.smartgreenhouse.greenhouse.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message, Exception e) {
        super(message);
    }
}
