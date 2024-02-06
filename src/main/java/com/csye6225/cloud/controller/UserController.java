package com.csye6225.cloud.controller;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import com.csye6225.cloud.service.UserService;
import com.csye6225.cloud.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User controller", description = "APIs for user related activities")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/v1/user/self", produces = "application/json")
    @Operation(summary = "Get user")
    @ApiResponse(responseCode = "200", description = "Get user")
    public ResponseEntity<UserResponseDTO> getUser() {
        UserResponseDTO userResponseDTO = userService.getUser();
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.ok().headers(headers).body(userResponseDTO);
    }

    @PostMapping(value = "/v1/user", produces = "application/json")
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "Create user")
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody CreateUserRequestDTO createUserRequestDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(createUserRequestDTO);
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userResponseDTO);
    }

    @PutMapping(value = "/v1/user/self", produces = "application/json")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "Update user")
    public ResponseEntity<UserResponseDTO> updateUser(@Validated @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        UserResponseDTO userResponseDTO = userService.updateUser(updateUserRequestDTO);
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.ok().headers(headers).body(userResponseDTO);
    }

}
