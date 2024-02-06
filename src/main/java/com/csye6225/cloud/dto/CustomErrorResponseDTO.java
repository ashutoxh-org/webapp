package com.csye6225.cloud.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
public class CustomErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;
    private Map<String, String> validationErrors;

    public CustomErrorResponseDTO(LocalDateTime timestamp, int status, String error, String path, Map<String, String> validationErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    public CustomErrorResponseDTO(LocalDateTime timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
        validationErrors = new HashMap<>();
    }

}

