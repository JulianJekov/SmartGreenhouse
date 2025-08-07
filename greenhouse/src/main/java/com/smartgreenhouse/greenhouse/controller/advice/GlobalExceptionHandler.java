package com.smartgreenhouse.greenhouse.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartgreenhouse.greenhouse.enums.SensorType;
import com.smartgreenhouse.greenhouse.exceptions.NameAlreadyExistsException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        LOGGER.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(ObjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NameAlreadyExistsException.class)
    public ResponseEntity<String> handleNameAlreadyExistsException(NameAlreadyExistsException ex) {
        LOGGER.info("Duplicate name detected: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.warn("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof InvalidFormatException ife &&
                ife.getTargetType() != null &&
                ife.getTargetType().isEnum()) {
            String invalidValue = ife.getValue().toString();
            LOGGER.debug("Invalid enum value provided: {}", invalidValue);
            String enumValue = Arrays.toString(SensorType.values());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid sensor type. Valid values are " + enumValue);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed JSON request");
    }
}
