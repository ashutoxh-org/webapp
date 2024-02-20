package com.csye6225.cloud.controller;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getPostUrl() {
        return "http://localhost:" + port + "/v1/user";
    }

    private String getPutAndGetUrl() {
        return "http://localhost:" + port + "/v1/user/self";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YXNodXRvc2hAZ21haWwuY29tOkFzaHV0b3NoQDEyMw");
        return headers;
    }

    private ResponseEntity<UserResponseDTO> createUser(CreateUserRequestDTO createUserRequest) {
        return restTemplate.postForEntity(getPostUrl(), createUserRequest, UserResponseDTO.class);
    }

    private ResponseEntity<UserResponseDTO> getUser(HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(getPutAndGetUrl(), HttpMethod.GET, entity, UserResponseDTO.class);
    }

    private ResponseEntity<UserResponseDTO> updateUser(UpdateUserRequestDTO updateUserRequest, HttpHeaders headers) {
        HttpEntity<UpdateUserRequestDTO> entity = new HttpEntity<>(updateUserRequest, headers);
        return restTemplate.exchange(getPutAndGetUrl(), HttpMethod.PUT, entity, UserResponseDTO.class);
    }

    @Test
    @Order(1) //JVM might pick random test to execute first. Test will fail if update runs before create
    void testCreateAndCheckUser() {
        // Create user
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO("Ashutosh", "Singh", "Ashutosh@123", "ashutosh@gmail.com");
        ResponseEntity<UserResponseDTO> createResponse = createUser(createUserRequest);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Validate by get user
        HttpHeaders headers = createHeaders();
        ResponseEntity<UserResponseDTO> getUser = getUser(headers);
        assertEquals(HttpStatus.NO_CONTENT, getUser.getStatusCode());
        assertNotNull(getUser.getBody());
        assertEquals(createUserRequest.getFirstName(), getUser.getBody().getFirstName());
        assertEquals(createUserRequest.getLastName(), getUser.getBody().getLastName());
    }

    @Test
    @Order(2)
    void testUpdateAndCheckUser() {
        // Update user
        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO("Ashu", "Si", "Ashutosh@123");
        HttpHeaders headers = createHeaders();
        ResponseEntity<UserResponseDTO> updateResponse = updateUser(updateUserRequestDTO, headers);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // Validate by get user
        ResponseEntity<UserResponseDTO> getUser = getUser(headers);
        assertEquals(HttpStatus.OK, getUser.getStatusCode());
        assertNotNull(getUser.getBody());
        assertEquals(updateUserRequestDTO.getFirstName(), getUser.getBody().getFirstName());
        assertEquals(updateUserRequestDTO.getLastName(), getUser.getBody().getLastName());
    }
}
