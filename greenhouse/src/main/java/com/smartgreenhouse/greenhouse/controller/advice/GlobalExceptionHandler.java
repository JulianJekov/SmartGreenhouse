package com.smartgreenhouse.greenhouse.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartgreenhouse.greenhouse.dto.ErrorResponse;
import com.smartgreenhouse.greenhouse.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
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

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception ex,
            HttpStatus status,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex,
                                                                HttpServletRequest request) {
        LOGGER.error("An unexpected error occurred", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(ObjectNotFoundException ex,
                                                                       HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(NameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleNameAlreadyExistsException(NameAlreadyExistsException ex,
                                                                          HttpServletRequest request) {
        LOGGER.info("Duplicate name detected: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                                   HttpServletRequest request) {
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

        return buildErrorResponse(new IllegalArgumentException(errorMessage), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                               HttpServletRequest request) {
        LOGGER.warn("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return buildErrorResponse(
                new IllegalArgumentException(errors.toString()),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                                                                               HttpServletRequest request) {
        Throwable rootCause = ex.getRootCause();
        String message = "Malformed JSON request";
        if (rootCause instanceof InvalidFormatException ife) {
            if (Number.class.isAssignableFrom(ife.getTargetType())) {
                String field = ife.getPath().get(ife.getPath().size() - 1).getFieldName();
                message = "Invalid number format for field '" + field + "'";
            }
            if (ife.getTargetType().isEnum()) {
                String enumValues = Arrays.toString(ife.getTargetType().getEnumConstants());
                message = "Invalid enum value. Valid values: " + enumValues;
            }
        }
        LOGGER.warn("Malformed JSON request: {}", ex.getMessage());

        return buildErrorResponse(new IllegalArgumentException(message), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AlreadyWateringException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyWateringException(AlreadyWateringException ex,
                                                                        HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            IllegalArgumentException.class,
            InvalidWaterAmountException.class,
            InvalidPasswordException.class,
            EmailNotVerifiedException.class,
            SensorNotActiveException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex,
                                                                           HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleTokenException(InvalidTokenException ex, HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex,
                                                                           HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException ex,
                                                                        HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.TOO_MANY_REQUESTS, request);
    }
}
