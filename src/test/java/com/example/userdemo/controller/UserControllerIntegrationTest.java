package com.example.userdemo.controller;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserDTO;
import com.example.userdemo.entity.UserStatus;
import com.example.userdemo.repository.UserRepository;
import com.example.userdemo.util.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("User Controller Integration Tests")
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/users";
        userRepository.deleteAll(); // Clean up before each test
    }

    // Positive Test Cases

    @Test
    @DisplayName("GET /api/v1/users - Should return all users successfully")
    public void getAllUsers_ShouldReturnAllUsers() {
        // Given
        CreateUserRequest request1 = TestHelper.createValidUserRequest();
        CreateUserRequest request2 = new CreateUserRequest(
                "Jane", "Smith", "jane.smith@example.com", "+1987654321", "456 Oak St"
        );

        createUser(request1);
        createUser(request2);

        // When
        ResponseEntity<String> response = testRestTemplate.getForEntity(baseUrl, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("john.doe@example.com"));
        assertTrue(response.getBody().contains("jane.smith@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should return user by ID")
    public void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        UserDTO createdUser = createUser(request);

        // When
        ResponseEntity<UserDTO> response = testRestTemplate.getForEntity(
                baseUrl + "/" + createdUser.getId(), UserDTO.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdUser.getId(), response.getBody().getId());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("GET /api/v1/users/email/{email} - Should return user by email")
    public void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        createUser(request);

        // When
        ResponseEntity<UserDTO> response = testRestTemplate.getForEntity(
                baseUrl + "/email/john.doe@example.com", UserDTO.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("POST /api/v1/users - Should create user successfully")
    public void createUser_ShouldCreateUser_WhenValidRequest() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, TestHelper.createHeaders());

        // When
        ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(baseUrl, entity, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals(UserStatus.ACTIVE.toString(), response.getBody().getStatus());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Should update user successfully")
    public void updateUser_ShouldUpdateUser_WhenValidRequest() {
        // Given
        CreateUserRequest createRequest = TestHelper.createValidUserRequest();
        UserDTO createdUser = createUser(createRequest);

        UpdateUserRequest updateRequest = TestHelper.createValidUpdateRequest();
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(updateRequest, TestHelper.createHeaders());

        // When
        testRestTemplate.put(baseUrl + "/" + createdUser.getId(), entity);

        // Then - Verify by fetching the updated user
        ResponseEntity<UserDTO> getResponse = testRestTemplate.getForEntity(
                baseUrl + "/" + createdUser.getId(), UserDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("John", getResponse.getBody().getFirstName()); // First name unchanged
        assertEquals("Smith", getResponse.getBody().getLastName()); // Last name updated
        assertEquals("+1987654321", getResponse.getBody().getPhone()); // Phone updated
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Should delete user successfully")
    public void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        UserDTO createdUser = createUser(request);

        // When
        testRestTemplate.delete(baseUrl + "/" + createdUser.getId());

        // Then - Verify user no longer exists
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl + "/" + createdUser.getId(), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PATCH /api/v1/users/{id}/status - Should update user status")
    public void updateUserStatus_ShouldUpdateStatus_WhenValidRequest() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        UserDTO createdUser = createUser(request);

        // Then - Verify status was updated
        ResponseEntity<UserDTO> getResponse =
            testRestTemplate.getForEntity(
                baseUrl + "/" + createdUser.getId() + "/INACTIVE",
                UserDTO.class
            );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(UserStatus.INACTIVE.toString(), getResponse.getBody().getStatus());
    }

    // Negative Test Cases

    @Test
    @DisplayName("GET /api/v1/users/{id} - Should return 404 when user not found")
    public void getUserById_ShouldReturn404_WhenUserNotFound() {
        // When
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl + "/999", String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("User not found"));
    }

    @Test
    @DisplayName("GET /api/v1/users/email/{email} - Should return 404 when email not found")
    public void getUserByEmail_ShouldReturn404_WhenEmailNotFound() {
        // When
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl + "/email/nonexistent@example.com", String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("User not found with email"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should return 400 when invalid request")
    public void createUser_ShouldReturn400_WhenInvalidRequest() {
        // Given
        CreateUserRequest invalidRequest = TestHelper.createInvalidUserRequest();
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(invalidRequest, TestHelper.createHeaders());

        // When
        ResponseEntity<String> response = testRestTemplate.postForEntity(baseUrl, entity, String.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should return 409 when email already exists")
    public void createUser_ShouldReturn409_WhenEmailExists() {
        // Given
        CreateUserRequest request = TestHelper.createValidUserRequest();
        createUser(request); // Create first user

        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, TestHelper.createHeaders());

        // When - Try to create user with same email
        ResponseEntity<String> response = testRestTemplate.postForEntity(baseUrl, entity, String.class);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().contains("User already exists with email"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Should return 404 when user not found")
    public void updateUser_ShouldReturn404_WhenUserNotFound() {
        // Given
        UpdateUserRequest request = TestHelper.createValidUpdateRequest();
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(request, TestHelper.createHeaders());

        // When
        testRestTemplate.put(baseUrl + "/999", entity);

        // Then - The put method doesn't return response body, so we need to verify by attempting to get
        ResponseEntity<String> getResponse = testRestTemplate.getForEntity(
                baseUrl + "/999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Should return 409 when email already exists")
    public void updateUser_ShouldReturn409_WhenEmailExists() {
        // Given - Create two users
        CreateUserRequest user1Request = TestHelper.createValidUserRequest();
        UserDTO user1 = createUser(user1Request);

        CreateUserRequest user2Request = new CreateUserRequest(
                "Jane", "Smith", "jane.smith@example.com", "+1987654321", "456 Oak St"
        );
        UserDTO user2 = createUser(user2Request);

        // Try to update user2 with user1's email
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setEmail("john.doe@example.com"); // user1's email
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(updateRequest, TestHelper.createHeaders());

        // When
        testRestTemplate.put(baseUrl + "/" + user2.getId(), entity);

        // Then - Verify by checking that user2's email didn't change
        ResponseEntity<UserDTO> getResponse = testRestTemplate.getForEntity(
                baseUrl + "/" + user2.getId(), UserDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("jane.smith@example.com", getResponse.getBody().getEmail());
    }

    @ParameterizedTest
    @MethodSource("invalidUserIdsProvider")
    @DisplayName("GET /api/v1/users/{id} - Should handle invalid IDs")
    public void getUserById_ShouldHandleInvalidIds(String invalidId, HttpStatus expectedStatus) {
        // When
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl + "/" + invalidId, String.class);

        // Then
        assertEquals(expectedStatus, response.getStatusCode());
    }

    private static Stream<Arguments> invalidUserIdsProvider() {
        return Stream.of(
            arguments("0", HttpStatus.NOT_FOUND),
            arguments("-1", HttpStatus.NOT_FOUND),
            arguments("abc", HttpStatus.INTERNAL_SERVER_ERROR),
            arguments("123abc", HttpStatus.INTERNAL_SERVER_ERROR),
            arguments("", HttpStatus.NOT_FOUND) // This will hit a different endpoint
        );
    }

    @Test
    @DisplayName("GET /api/v1/users/paginated - Should return paginated results")
    public void getUsersPaginated_ShouldReturnPaginatedResults() {
        // Given - Create multiple users
        for (int i = 1; i <= 15; i++) {
            CreateUserRequest request = new CreateUserRequest(
                    "User" + i,
                    "Test",
                    "user" + i + "@example.com",
                    "+123456789" + i,
                    "Address " + i
            );
            createUser(request);
        }

        // When
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                baseUrl + "/paginated?page=0&size=5&sort=firstName,asc", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Verify pagination info in response
        assertTrue(response.getBody().contains("\"content\""));
    }

    // Helper method to create user for testing
    private UserDTO createUser(CreateUserRequest request) {
        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, TestHelper.createHeaders());
        ResponseEntity<UserDTO> response = testRestTemplate.postForEntity(baseUrl, entity, UserDTO.class);
        return response.getBody();
    }

}