package com.example.userdemo.util;

import com.example.userdemo.dto.CreateUserRequest;
import com.example.userdemo.dto.UpdateUserRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class TestHelper {
    
    public static CreateUserRequest createValidUserRequest() {
        return new CreateUserRequest(
            "John",
            "Doe",
            "john.doe@example.com",
            "+1234567890",
            "123 Main St, City, Country"
        );
    }
    
    public static CreateUserRequest createInvalidUserRequest() {
        return new CreateUserRequest(
            "J", // Too short first name
            "D", // Too short last name
            "invalid-email", // Invalid email
            "123", // Too short phone
            "Address"
        );
    }
    
    public static UpdateUserRequest createValidUpdateRequest() {
        return new UpdateUserRequest(
            "John",
            "Smith", // Changed last name
            "+1987654321",
            "john.smith@johnsmith.com",
            "456 Oak St, Another City"
        );
    }
    
    public static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(
            Collections.singletonList(
                MediaType.APPLICATION_JSON
            )
        );
        return headers;
    }

}