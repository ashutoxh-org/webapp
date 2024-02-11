package com.csye6225.cloud.controller;

import com.csye6225.cloud.dto.CreateUserRequestDTO;
import com.csye6225.cloud.dto.UpdateUserRequestDTO;
import com.csye6225.cloud.dto.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

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


    @Test
    void testCreateAndCheckUserExisting() {
        // Create user
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO("Ashutosh", "Singh", "Ashutosh@123", "ashutosh@gmail.com");
        ResponseEntity<UserResponseDTO> createResponse = restTemplate.postForEntity(getPostUrl(), createUserRequest, UserResponseDTO.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Validate user exists
        ResponseEntity<String> userExistsResponse = restTemplate.postForEntity(getPostUrl(), createUserRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, userExistsResponse.getStatusCode());
        assertThat(userExistsResponse.getBody(), containsString("User already exists"));
    }

    @Test
    void testUpdateAndCheckUser() {
        //Update user
        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO("Ashu", "Si", "Ashutosh@123");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YXNodXRvc2hAZ21haWwuY29tOkFzaHV0b3NoQDEyMw");
        HttpEntity<UpdateUserRequestDTO> updateUserRequestDTOHttpEntity = new HttpEntity<>(updateUserRequestDTO, headers);
        restTemplate.put(getPutAndGetUrl(), updateUserRequestDTOHttpEntity, UserResponseDTO.class);

        //Validate by get user
        HttpEntity<String> getUserEntity = new HttpEntity<>(null, headers);
        ResponseEntity<UserResponseDTO> getUser = restTemplate.exchange(getPutAndGetUrl(), HttpMethod.GET, getUserEntity, UserResponseDTO.class);
        assertEquals(HttpStatus.OK, getUser.getStatusCode());
        assertEquals(updateUserRequestDTO.getFirstName(), getUser.getBody().getFirstName());
        assertEquals(updateUserRequestDTO.getLastName(), getUser.getBody().getLastName());

    }
}
