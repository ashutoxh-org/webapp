package com.csye6225.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmailVerificationResponseDTO {
    private boolean isVerified;
    private String message;
}