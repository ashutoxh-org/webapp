package com.csye6225.cloud.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The type User response dto.
 */
@Getter
@Setter
@Builder
public class UserResponseDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime accountCreated;

    private LocalDateTime accountUpdated;

}
