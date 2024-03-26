package com.csye6225.cloud.controller;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import com.csye6225.cloud.model.User;
import com.csye6225.cloud.repository.UserRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    UserRepository userRepository;

    @Test
    @Order(1) //JVM might pick random test to execute first. Test will fail if update runs before create
    void testCreateAndCheckUser() {
        // Create user
        CreateUserRequestDTO createUserRequestDTO = new CreateUserRequestDTO("John", "Doe", "Johnnyboi@123", "john@doe.com");
        ResponseEntity<UserResponseDTO> createResponse = createUser(createUserRequestDTO);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        //Set isVerified true to skip user verification
        setIsVerifiedTrue(createResponse.getBody().getEmail());

        // Validate by get user
        HttpHeaders headers = createHeaders();
        ResponseEntity<UserResponseDTO> getUser = getUser(headers);
        assertEquals(HttpStatus.OK, getUser.getStatusCode());
        assertNotNull(getUser.getBody());
        assertEquals(createUserRequestDTO.getFirstName(), getUser.getBody().getFirstName());
        assertEquals(createUserRequestDTO.getLastName(), getUser.getBody().getLastName());
    }

    @Test
    @Order(2)
    void testUpdateAndCheckUser() {
        // Update user
        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO("Johnny", "Does", "Johnnyboi@123");
        HttpHeaders headers = createHeaders();
        ResponseEntity<UserResponseDTO> updateResponse = updateUser(updateUserRequestDTO, headers);
        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatusCode());

        // Validate by get user
        ResponseEntity<UserResponseDTO> getUser = getUser(headers);
        assertEquals(HttpStatus.OK, getUser.getStatusCode());
        assertNotNull(getUser.getBody());
        assertEquals(updateUserRequestDTO.getFirstName(), getUser.getBody().getFirstName());
        assertEquals(updateUserRequestDTO.getLastName(), getUser.getBody().getLastName());
    }

    private void setIsVerifiedTrue(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            optionalUser.get().setVerified(true);
            userRepository.save(optionalUser.get());
        }
    }
    private String getPostUrl() {
        return "http://localhost:" + port + "/v1/user";
    }

    private String getPutAndGetUrl() {
        return "http://localhost:" + port + "/v1/user/self";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic am9obkBkb2UuY29tOkpvaG5ueWJvaUAxMjM=");
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

}