package com.csye6225.cloud.controller;

import com.csye6225.cloud.service.DatabaseHealthIndicator;
import com.csye6225.cloud.util.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Health check", description = "API for health check")
@RestController
@RequiredArgsConstructor
public class HealthController {

    private final DatabaseHealthIndicator databaseHealthIndicator;

    @GetMapping(value = "/healthz")
    @Operation(summary = "Check health status", description = "Returns OK if the application is healthy, otherwise returns SERVICE UNAVAILABLE.")
    @ApiResponse(responseCode = "200", description = "Application is healthy")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "503", description = "Application is not healthy")
    public ResponseEntity<Void> getHealth(HttpServletRequest httpServletRequest,
                                          @Nullable @RequestParam Map<String, String> requestParam) {
        HttpHeaders headers = Util.getRequiredHeaders();
        if (httpServletRequest.getContentLength() > 0 || (requestParam != null && !requestParam.isEmpty()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        if (Status.UP.equals(databaseHealthIndicator.health().getStatus()))
            return ResponseEntity.ok().headers(headers).build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).headers(headers).build();
    }

    @PostMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> postHealth() {
        return getMethodNotAllowedResponse();
    }

    @PutMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> putHealth() {
        return getMethodNotAllowedResponse();
    }

    @PatchMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> patchHealth() {
        return getMethodNotAllowedResponse();
    }

    @DeleteMapping("/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> deleteHealth() {
        return getMethodNotAllowedResponse();
    }

    @RequestMapping(method = RequestMethod.HEAD, value = "/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> headHealth() {
        return getMethodNotAllowedResponse();
    }

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/healthz")
    @Operation(summary = "Check health status")
    @ApiResponse(responseCode = "405", description = "Method not allowed")
    public ResponseEntity<Void> optionsHealth() {
        return getMethodNotAllowedResponse();
    }

    private ResponseEntity<Void> getMethodNotAllowedResponse() {
        HttpHeaders headers = Util.getRequiredHeaders();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).build();
    }

}
