package com.csye6225.cloud.controller;

import com.csye6225.cloud.service.DatabaseHealthIndicator;
import com.csye6225.cloud.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The type Health controller.
 */
@Tag(name = "Health check", description = "API for health check")
@RestController
@RequiredArgsConstructor
public class HealthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);
    private final DatabaseHealthIndicator databaseHealthIndicator;

    /**
     * Gets health.
     *
     * @return the health
     */
    @GetMapping(value = "/healthz")
    @Operation(summary = "Check health status", description = "Returns OK if the application is healthy, otherwise returns SERVICE UNAVAILABLE.")
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "503", description = "Application is not healthy")
    public ResponseEntity<Void> getHealth() {
        HttpHeaders headers = Util.getRequiredHeaders();
        if (Status.UP.equals(databaseHealthIndicator.health().getStatus()))
            return ResponseEntity.ok().headers(headers).build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(headers).build();
    }

    /**
     * Post health response entity.
     *
     * @return the response entity
     */
    @PostMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> postHealth() {
        return getMethodNotAllowedResponse();
    }

    /**
     * Put health response entity.
     *
     * @return the response entity
     */
    @PutMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> putHealth() {
        return getMethodNotAllowedResponse();
    }

    /**
     * Patch health response entity.
     *
     * @return the response entity
     */
    @PatchMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> patchHealth() {
        return getMethodNotAllowedResponse();
    }

    /**
     * Delete health response entity.
     *
     * @return the response entity
     */
    @DeleteMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> deleteHealth() {
        return getMethodNotAllowedResponse();
    }

    /**
     * Head health response entity.
     *
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> headHealth() {
        return getMethodNotAllowedResponse();
    }

    /**
     * Options health response entity.
     *
     * @return the response entity
     */
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> optionsHealth() {
        return getMethodNotAllowedResponse();
    }

    private ResponseEntity<Void> getMethodNotAllowedResponse() {
        HttpHeaders headers = Util.getRequiredHeaders();
        LOGGER.error("Unauthorized request for /healthz");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).build();
    }

}
