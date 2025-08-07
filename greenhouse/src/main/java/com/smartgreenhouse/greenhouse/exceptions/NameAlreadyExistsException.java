package com.smartgreenhouse.greenhouse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONTINUE)
public class NameAlreadyExistsException extends RuntimeException {
    public NameAlreadyExistsException(String message) {
        super(message);
    }
}
