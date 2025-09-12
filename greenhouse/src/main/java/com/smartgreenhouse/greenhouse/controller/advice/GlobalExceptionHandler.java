package com.smartgreenhouse.greenhouse.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartgreenhouse.greenhouse.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        LOGGER.warn("Method argument type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        String parameterName = ex.getName();
        Object invalidValue = ex.getValue();
        Class<?> requiredType = ex.getRequiredType();

        String errorMessage;

        assert requiredType != null;
        if (Number.class.isAssignableFrom(requiredType)) {
            errorMessage = String.format(
                    "Invalid number format for parameter '%s': '%s'. Must be a valid number.",
                    parameterName, invalidValue
            );
        } else if (requiredType.isEnum()) {
            String enumValues = Arrays.toString(requiredType.getEnumConstants());
            errorMessage = String.format(
                    "Invalid enum value for parameter '%s': '%s'. Valid values are: %s",
                    parameterName, invalidValue, enumValues
            );
        } else {
            errorMessage = String.format(
                    "Invalid format for parameter '%s': '%s'. Must be of type %s",
                    parameterName, invalidValue, requiredType.getSimpleName()
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
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
        if (rootCause instanceof InvalidFormatException ife) {
            if (Number.class.isAssignableFrom(ife.getTargetType())) {
                String fieldName = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid number format for field '" + fieldName + "': '" + ife.getValue() + "'. Must be a valid number.");
            }
            if (ife.getTargetType().isEnum()) {
                String enumValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid enum value: '" + ife.getValue() + "'. Valid values are: " + enumValues);
            }
        }
        LOGGER.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Malformed JSON request. Please check your input data.");
    }

    @ExceptionHandler(InvalidWaterAmountException.class)
    public ResponseEntity<String> handleInvalidWaterAmountException(InvalidWaterAmountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AlreadyWateringException.class)
    public ResponseEntity<String> handleAlreadyWateringException(AlreadyWateringException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<String> handleBadRequests(Exception ex) {
        String message = ex instanceof MissingServletRequestParameterException
                ? String.format("%s parameter is required",
                ((MissingServletRequestParameterException) ex).getParameterName())
                : ex.getMessage();

        return ResponseEntity.badRequest()
                .body(message);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleTokenException(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<String> handleMissingPathVariableException(MissingPathVariableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required path parameter: " + ex.getVariableName());
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<String> handleNotVerifiedException(EmailNotVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<String> handleTooManyRequestsException(TooManyRequestsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
