package com.csye6225.cloud.advice;

import com.csye6225.cloud.dto.CustomErrorResponseDTO;
import com.csye6225.cloud.exception.CustomException;
import com.csye6225.cloud.exception.EmailVerificationException;
import com.csye6225.cloud.util.Util;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Custom global exception handler.
 */
@RestControllerAdvice
public class CustomGlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);

    /**
     * Handle generic exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<CustomErrorResponseDTO> handleGenericException(Exception ex, WebRequest request) {
        CustomErrorResponseDTO errors = new CustomErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle mail verification exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = EmailVerificationException.class)
    public ResponseEntity<CustomErrorResponseDTO> handleMailVerificationException(Exception ex, WebRequest request) {
        CustomErrorResponseDTO errors = new CustomErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    /**
     * Handle unrecognized property exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = UnrecognizedPropertyException.class)
    public ResponseEntity<CustomErrorResponseDTO> handleUnrecognizedPropertyException(Exception ex, WebRequest request) {
        CustomErrorResponseDTO errors = new CustomErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Attempt to send extra or unauthorised fields",
                request.getDescription(false)
        );
        LOGGER.error("Attempt to send extra or unauthorised fields for user {}", Util.getUserEmail());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle validation exceptions response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        CustomErrorResponseDTO response = new CustomErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                request.getDescription(false),
                errors
        );
        LOGGER.warn("Field validation Error(s) {} for user {}", errors.entrySet(), Util.getUserEmail());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle sql exception response entity.
     *
     * @param ex      the ex
     * @param request the request
     * @return the response entity
     */
    @ExceptionHandler(value = {SQLException.class, DataAccessException.class})
    public ResponseEntity<CustomErrorResponseDTO> handleSQLException(Exception ex, WebRequest request) {
        CustomErrorResponseDTO errors = new CustomErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                request.getDescription(false)
        );
        LOGGER.error("Exception while trying to connect to the database: {}", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.SERVICE_UNAVAILABLE);
    }

}

