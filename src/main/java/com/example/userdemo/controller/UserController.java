package com.example.userdemo.controller;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import com.example.userdemo.dto.UserDTO;
import com.example.userdemo.entity.UserStatus;
import com.example.userdemo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("GET /api/v1/users - Fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/paginated")
    @Operation(
        summary = "Get users with pagination",
        description = "Retrieve users with pagination support"
    )
    public ResponseEntity<Page<UserDTO>> getUsersPaginated(
            @PageableDefault(
                size = 10,
                sort = "id",
                direction = Sort.Direction.ASC
            ) Pageable pageable) {
        log.info("GET /api/v1/users/paginated - Fetching users with pagination");
        Page<UserDTO> users = userService.getUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to be retrieved")
            @PathVariable Long id
    ) {
        log.info("GET /api/v1/users/{} - Fetching user by ID", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/email/{email}")
    @Operation(
        summary = "Get user by email",
        description = "Retrieve a specific user by their email"
    )
    public ResponseEntity<UserDTO> getUserByEmail(
        @Parameter(description = "Email of the user to be retrieved")
        @PathVariable String email
    ) {
        log.info("GET /api/v1/users/email/{} - Fetching user by email", email);
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Create a new user with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<UserDTO> createUser(
        @Valid @RequestBody CreateUserRequest request
    ) {
        log.info("POST /api/v1/users - Creating new user with email: {}", request.getEmail());
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update user",
        description = "Update an existing user's information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID of the user to be updated")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /api/v1/users/{} - Updating user", id);
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/{id}/{status}")
    @Operation(
        summary = "Update user status",
        description = "Update the status of a user"
    )
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        log.info("PATCH /api/v1/users/{}/{} - updateUserStatus", id, status);
        UserDTO updatedUser =
            userService.updateUserStatus(
                id,
                UserStatus.valueOf(status)
            );
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}