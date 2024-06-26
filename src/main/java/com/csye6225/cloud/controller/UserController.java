package com.csye6225.cloud.controller;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.EmailVerificationResponseDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import com.csye6225.cloud.service.UserService;
import com.csye6225.cloud.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The type User controller.
 */
@Tag(name = "User controller", description = "APIs for user related activities")
@RestController
@RequestMapping("/v7")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Gets user.
     *
     * @return the user
     */
    @GetMapping(value = "/user/self", produces = "application/json")
    @Operation(summary = "Get user")
    @ApiResponse(responseCode = "200", description = "Get user")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "403", description = "Unverified user")
    @ApiResponse(responseCode = "401", description = "Unauthorised")
    public ResponseEntity<UserResponseDTO> getUser() {
        UserResponseDTO userResponseDTO = userService.getUser();
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.ok().headers(headers).body(userResponseDTO);
    }

    /**
     * Create user response entity.
     *
     * @param createUserRequestDTO the create user request dto
     * @return the response entity
     */
    @PostMapping(value = "/user", produces = "application/json")
    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "Create user")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<UserResponseDTO> createUser(@Validated @RequestBody CreateUserRequestDTO createUserRequestDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(createUserRequestDTO);
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(userResponseDTO);
    }

    /**
     * Update user response entity.
     *
     * @param updateUserRequestDTO the update user request dto
     * @return the response entity
     */
    @PutMapping(value = "/user/self")
    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "403", description = "Unverified user")
    @ApiResponse(responseCode = "401", description = "Unauthorised")
    public ResponseEntity<Void> updateUser(@Validated @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        userService.updateUser(updateUserRequestDTO);
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    /**
     * Verify user response entity.
     *
     * @param token the token
     * @return the response entity
     */
    @GetMapping(value = "/user/verify", produces = "application/json")
    @Operation(summary = "Verify user")
    @ApiResponse(responseCode = "200", description = "User verified")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public ResponseEntity<EmailVerificationResponseDTO> verifyUser(@RequestParam String token) {
        EmailVerificationResponseDTO emailVerificationResponseDTO = userService.verifyUser(token);
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(emailVerificationResponseDTO);
    }
}
