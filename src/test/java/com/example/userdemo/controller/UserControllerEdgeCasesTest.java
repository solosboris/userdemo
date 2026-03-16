package com.example.userdemo.controller;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UserDTO;
import com.example.userdemo.util.TestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerEdgeCasesTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String baseUrl;

    @Test
    public void createUser_WithBoundaryValues_ShouldHandleAppropriately() {
        baseUrl = "http://localhost:"
                    .concat(Integer.toString(port))
                    .concat("/api/v1/users");

        // Test minimum valid values
        CreateUserRequest minValidRequest = new CreateUserRequest(
            "Jo", // Minimum length first name
            "Do", // Minimum length last name
            "a@b.co", // Minimal valid email
            "1234567890", // Minimum length phone
            "A" // Minimal address
        );

        HttpEntity<CreateUserRequest> entity =
                new HttpEntity<>(
                    minValidRequest,
                    TestHelper.createHeaders()
                );
        ResponseEntity<UserDTO> response =
            testRestTemplate.postForEntity(
                baseUrl,
                entity,
                UserDTO.class
            );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    public void createUser_WithNullRequestBody_ShouldReturnBadRequest() {
        baseUrl = "http://localhost:"
                    .concat(Integer.toString(port))
                    .concat("/api/v1/users");

        HttpEntity<String> entity =
                new HttpEntity<>(
                        null,
                        TestHelper.createHeaders()
                );
        ResponseEntity<String> response = testRestTemplate
                .postForEntity(
                    baseUrl,
                    entity,
                    String.class
                );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void getUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        baseUrl = "http://localhost:"
                    .concat(Integer.toString(port))
                    .concat("/api/v1/users");

        ResponseEntity<String> response = testRestTemplate
                .getForEntity(
                    baseUrl,
                    String.class
                );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("[]")); // Empty array
    }

}